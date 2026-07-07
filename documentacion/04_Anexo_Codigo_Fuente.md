# Anexo: Código Fuente Completo - BiblioMobil

Este documento contiene una recopilación de las clases más críticas del proyecto BiblioMobil, organizadas por capas para facilitar su revisión técnica.

---

## 1. Capa de Datos (Data Layer)

### VolumeRepositoryImpl.kt
Gestiona la lógica de negocio de los datos, integrando Room, Retrofit y el sistema de archivos para Backups.

```kotlin
@Singleton
class VolumeRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val volumeDao: VolumeDao,
    private val googleBooksApi: GoogleBooksApiService,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : VolumeRepository {

    override fun searchVolumes(query: String): Flow<List<DetailedVolume>> {
        val trimmedQuery = query.trim()
        return if (trimmedQuery.isEmpty()) {
            volumeDao.getAllDetailedVolumes()
        } else {
            volumeDao.searchVolumesFts(trimmedQuery)
        }
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
Definición de las consultas SQL y transacciones atómicas.

```kotlin
@Dao
interface VolumeDao {
    @Transaction
    @Query("SELECT * FROM volumes ORDER BY createdAt DESC")
    fun getAllDetailedVolumes(): Flow<List<DetailedVolume>>

    @Transaction
    @Query("SELECT * FROM volumes WHERE title LIKE '%' || :searchQuery || '%' OR synopsis LIKE '%' || :query || '%'")
    fun searchVolumesFts(searchQuery: String): Flow<List<DetailedVolume>>
}
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
Configuración del cliente HTTP con interceptores de seguridad para API Keys.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val apiKey = BuildConfig.GOOGLE_BOOKS_API_KEY
                val url = chain.request().url.newBuilder().setQueryParameter("key", apiKey).build()
                val request = chain.request().newBuilder().url(url)
                    .header("X-Android-Package", "com.fasby.bibliomobil")
                    .header("X-Android-Cert", "C90FDADC5CA9C56618328F6695584ABCBFFDEB29")
                    .build()
                chain.proceed(request)
            }.build()
    }
}
```
