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
    val collectionId: String? = null,
    val title: String,
    val number: Int = 0,
    val publishedYear: Int = 0,
    val synopsis: String = "",
    val coverPath: String = "",
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
Gestiona la lógica de búsqueda secuencial (Google Books -> Open Library) con soporte de sinopsis ampliada mediante Work Keys.

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
        // Lógica de búsqueda con fallback automático de Google a Open Library
        // ... (Ver implementación completa en el repositorio)
    }

    override suspend fun createBackup(onUriReady: (Uri) -> Unit) {
        // Implementación física de backup SQLite con WAL checkpoint
    }
}
```

---

## 2. Capa de Presentación (UI Layer)

### CatalogViewModel.kt
Gestiona el estado del catálogo y la sincronización entre búsqueda manual y por voz, incluyendo el reseteo de estados para evitar errores al borrar.

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
        // Combinación reactiva de flujos
        Triple(query, collectionId, voiceState)
    }.flatMapLatest { (currentQuery, collectionId, voiceState) ->
        val flow = if (collectionId != null) repository.getVolumesByCollection(collectionId)
                   else repository.searchVolumes(currentQuery)
        
        flow.map { list -> CatalogUiState(currentQuery, list, voiceState) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CatalogUiState(isLoading = true))

    init {
        // ... Sincronización búsqueda por voz ...

        // TFM FIX: Estrategia reactiva para evitar volúmenes huérfanos en la UI.
        // Si se elimina una colección, el ViewModel detecta la ausencia del ID 
        // en la tabla de colecciones y limpia el filtro automáticamente.
        viewModelScope.launch {
            repository.getAllCollections().collect { collections ->
                val currentFilter = _collectionId.value
                if (currentFilter != null && collections.none { it.id == currentFilter }) {
                    _collectionId.value = null
                }
            }
        }
    }
}
```

### CameraOcrViewModel.kt
Lógica del escáner con el temporizador de confirmación optimizado a 1,5 segundos.

```kotlin
private fun startIsbnConfirmation(isbn: String) {
    confirmationJob?.cancel()
    confirmationJob = viewModelScope.launch {
        _uiState.update { it.copy(confirmingIsbn = isbn, confirmationProgress = 0f) }
        val totalTimeMs = 1500L
        val stepMs = 100L
        val totalSteps = (totalTimeMs / stepMs).toInt()
        for (step in 1..totalSteps) {
            delay(stepMs)
            _uiState.update { it.copy(confirmationProgress = step.toFloat() / totalSteps) }
        }
        _uiState.update { it.copy(detectedIsbn = isbn, lastNavigatedIsbn = isbn) }
    }
}
```

---

## 3. Capa de Red e Infraestructura

### NetworkModule.kt
Configuración de clientes Retrofit con interceptor de reintentos para errores de servidor (503/504).

```kotlin
@Provides
@Singleton
fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            var response = chain.proceed(request)
            var retries = 0
            // Reintentos automáticos para mejorar la resiliencia
            while (!response.isSuccessful && response.code in 503..504 && retries < 2) {
                retries++
                response.close()
                Thread.sleep(1500)
                response = chain.proceed(request)
            }
            response
        }.build()
}
```

---

## 4. Capa de Inteligencia Artificial (AI Layer)

### GeminiAiRepositoryImpl.kt
Implementación del cliente de Gemini 1.5 Flash para la generación de resúmenes.

```kotlin
@Singleton
class GeminiAiRepositoryImpl @Inject constructor() : AiRepository {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    override suspend fun generateSummary(title: String, synopsis: String): String? {
        val response = generativeModel.generateContent("Resume en español: $title")
        return response.text
    }
}
```
