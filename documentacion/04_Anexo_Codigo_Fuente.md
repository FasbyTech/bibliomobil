# Anexo: Código Fuente Completo - BiblioMobil

Este documento contiene una recopilación de las clases más críticas del proyecto BiblioMobil, organizadas por capas para facilitar su revisión técnica.

---

## 1. Capa de Datos (Data Layer)

### VolumeEntity.kt
Representa el modelo persistente de un libro con campos extendidos para valoraciones y reseñas.

```kotlin
@Entity(tableName = "volumes")
data class VolumeEntity(
    @PrimaryKey val isbn: String,
    val title: String,
    val rating: Int, // 1-5 estrellas
    val personalReview: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
```

### OpenLibraryApiService.kt
Interfaz para el sistema de fallback que permite recuperar metadatos y portadas cuando el servicio de Google falla.

```kotlin
interface OpenLibraryApiService {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String? = null,
        @Query("isbn") isbn: String? = null,
        @Query("limit") limit: Int = 1
    ): Response<OpenLibraryResponse>

    @GET("{workKey}.json")
    suspend fun getWorkDetails(
        @Path("workKey", encoded = false) workKey: String
    ): Response<OpenLibraryWorkResponse>
}
```

### VolumeRepositoryImpl.kt
Gestiona la lógica de negocio de los datos, integrando Room, Retrofit y el sistema de archivos para Backups. Incluye una estrategia secuencial de búsqueda (Google Books -> Open Library).

```kotlin
@Singleton
class VolumeRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val volumeDao: VolumeDao,
    private val googleBooksApi: GoogleBooksApiService,
    private val openLibraryApi: OpenLibraryApiService,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : VolumeRepository {

    override suspend fun searchRemoteBook(query: String): Pair<VolumeEntity, List<AuthorEntity>>? = withContext(ioDispatcher) {
        try {
            // INTENTO 1: Google Books
            val googleResponse = googleBooksApi.searchBooks(query)
            var items = if (googleResponse.isSuccessful) googleResponse.body()?.items else null

            if (!items.isNullOrEmpty()) {
                val book = items.first().volumeInfo
                // ... Mapeo de Google Books ...
                return@withContext Pair(volume, authors)
            }

            // INTENTO 2: Open Library (Fallback)
            val cleanIsbnOnly = query.removePrefix("isbn:").trim()
            val olResponse = openLibraryApi.searchBooks(query = cleanIsbnOnly)
            val olDocs = if (olResponse.isSuccessful) olResponse.body()?.docs else null
            
            if (!olDocs.isNullOrEmpty()) {
                val doc = olDocs.first()
                // Búsqueda de descripción ampliada (Work Key)
                var remoteSynopsis = ""
                doc.key?.let { key ->
                    val workResponse = openLibraryApi.getWorkDetails(key.removePrefix("/"))
                    if (workResponse.isSuccessful) remoteSynopsis = workResponse.body()?.getDescriptionText() ?: ""
                }
                // ... Mapeo de Open Library ...
                return@withContext Pair(volume, authors)
            }
            null
        } catch (e: Exception) { null }
    }

    override suspend fun createBackup(onUriReady: (Uri) -> Unit): Unit = withContext(ioDispatcher) {
        try {
            database.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)").moveToFirst()
            val dbFile = context.getDatabasePath("biblio_mobil_db")
            if (dbFile.exists()) {
                val backupFile = File(context.cacheDir, "backup_bibliomobil_${System.currentTimeMillis()}.db")
                dbFile.copyTo(backupFile, overwrite = true)
                val contentUri = FileProvider.getUriForFile(context, "com.fasby.bibliomobil.fileprovider", backupFile)
                onUriReady(contentUri)
            }
        } catch (e: Exception) {
            android.util.Log.e("BiblioMobil", "Error creando backup", e)
        }
    }
}
```

### VolumeDao.kt
Definición de las consultas SQL, transacciones atómicas y gestión de la tabla de préstamos.

```kotlin
@Dao
interface VolumeDao {
    @Transaction
    @Query("SELECT * FROM volumes ORDER BY createdAt DESC")
    fun getAllDetailedVolumes(): Flow<List<DetailedVolume>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity)

    @Query("SELECT * FROM loans WHERE isbn = :isbn ORDER BY loanDate DESC")
    fun getLoansForVolume(isbn: String): Flow<List<LoanEntity>>
}
```

### LoanEntity.kt
Entidad que representa un préstamo vinculado a un volumen mediante una relación 1:N con borrado en cascada.

```kotlin
@Entity(
    tableName = "loans",
    foreignKeys = [
        ForeignKey(
            entity = VolumeEntity::class,
            parentColumns = ["isbn"],
            childColumns = ["isbn"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LoanEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val isbn: String,
    val lentTo: String,
    val loanDate: Long = System.currentTimeMillis(),
    val returnDate: Long? = null
)
```

---

## 2. Capa de Presentación (UI Layer)

### CatalogViewModel.kt
Gestiona el estado del catálogo y la sincronización entre búsqueda manual y por voz.

```kotlin
@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val repository: VolumeRepository,
    private val voiceRecognizerManager: VoiceRecognizerManager
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CatalogUiState> = combine(
        _searchQuery, _collectionId, voiceRecognizerManager.state
    ) { query, collectionId, voiceState ->
        Triple(query, collectionId, voiceState)
    }.flatMapLatest { (currentQuery, collectionId, voiceState) ->
        val flow = if (collectionId != null) {
            repository.getVolumesByCollection(collectionId)
        } else {
            repository.searchVolumes(currentQuery)
        }
        flow.map { volumesList ->
            CatalogUiState(currentQuery, volumesList, voiceState, false)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CatalogUiState(isLoading = true))
}
```

### CameraOcrViewModel.kt
Lógica del escáner con el temporizador de confirmación de 3 segundos.

```kotlin
@HiltViewModel
class CameraOcrViewModel @Inject constructor(
    private val searchVolumesUseCase: SearchVolumesUseCase
) : ViewModel() {

    private fun startIsbnConfirmation(isbn: String) {
        confirmationJob?.cancel()
        confirmationJob = viewModelScope.launch {
            _uiState.update { it.copy(confirmingIsbn = isbn, confirmationProgress = 0f) }
            val totalSteps = 30
            for (step in 1..totalSteps) {
                delay(100)
                _uiState.update { it.copy(confirmationProgress = step.toFloat() / totalSteps) }
            }
            _uiState.update { it.copy(detectedIsbn = isbn, lastNavigatedIsbn = isbn) }
        }
    }
}
```

---

## 3. Seguridad y Configuración (DI & Gradle)

### NetworkModule.kt
Configuración del cliente HTTP con interceptores de seguridad para API Keys y lógica de reintentos para errores 503/504.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                // Inyección selectiva de API Key según el host
                val request = if (originalRequest.url.host.contains("googleapis")) {
                    originalRequest.newBuilder()
                        .url(originalRequest.url.newBuilder().addQueryParameter("key", BuildConfig.GOOGLE_BOOKS_API_KEY).build())
                        .build()
                } else originalRequest
                
                var response = chain.proceed(request)
                // Lógica de reintentos automática (Exponential Backoff simulado)
                var retries = 0
                while (!response.isSuccessful && response.code in 503..504 && retries < 2) {
                    retries++
                    response.close()
                    Thread.sleep(1500)
                    response = chain.proceed(request)
                }
                response
            }.build()
    }
}

---

## 4. Capa de Inteligencia Artificial (AI Layer)

### GeminiAiRepositoryImpl.kt
Implementación del cliente de Gemini 1.5 Flash para la generación de resúmenes y categorización automática mediante modelos generativos.

```kotlin
@Singleton
class GeminiAiRepositoryImpl @Inject constructor() : AiRepository {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    override suspend fun generateSummary(title: String, synopsis: String): String? {
        return try {
            val response = generativeModel.generateContent("Genera un resumen corto en español para: $title. Sinopsis: $synopsis")
            response.text
        } catch (e: Exception) { null }
    }
}
```
```
