# Anexo: Código Fuente Destacado - BiblioMobil

Este documento contiene los fragmentos de código más relevantes del proyecto, organizados por capas arquitectónicas y funcionalidades críticas para la defensa del TFM.

---

## 1. Capa de Datos y Seguridad (Network & Persistence)

### NetworkModule.kt (Interceptor de Seguridad)
Configuración centralizada de OkHttp para inyectar claves de API y certificados de seguridad SHA-1.

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
                val apiKey = BuildConfig.GOOGLE_BOOKS_API_KEY
                val urlWithKey = originalRequest.url.newBuilder()
                    .setQueryParameter("key", apiKey).build()
                
                val request = originalRequest.newBuilder()
                    .url(urlWithKey)
                    .header("X-Android-Package", "com.fasby.bibliomobil")
                    .header("X-Android-Cert", "C90FDADC5CA9C56618328F6695584ABCBFFDEB29")
                    .build()
                chain.proceed(request)
            }.build()
    }
}
```

### VolumeDao.kt (Persistencia Room)
Implementación de transacciones atómicas y consultas reactivas con Flow.

```kotlin
@Dao
interface VolumeDao {
    @Transaction
    suspend fun insertCompleteVolume(volume: VolumeEntity, authors: List<AuthorEntity>) {
        insertVolume(volume)
        authors.forEach { author ->
            insertAuthor(author)
            insertVolumeAuthorCrossRef(VolumeAuthorCrossRef(volume.isbn, author.id))
        }
    }

    @Query("SELECT * FROM volumes WHERE title LIKE '%' || :query || '%' OR synopsis LIKE '%' || :query || '%'")
    fun searchVolumes(query: String): Flow<List<DetailedVolume>>
}
```

---

## 2. Inteligencia Artificial y Visión Artificial

### GeminiAiRepositoryImpl.kt (Integración LLM)
Lógica de comunicación con el modelo Gemini 1.5 Flash para generación de resúmenes.

```kotlin
@Singleton
class GeminiAiRepositoryImpl @Inject constructor() : AiRepository {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY,
        safetySettings = listOf(...)
    )

    override suspend fun generateSummary(title: String, synopsis: String): String? {
        return try {
            val response = generativeModel.generateContent("Genera un resumen en español para: $title")
            response.text
        } catch (e: Exception) { null }
    }
}
```

### CameraOcrViewModel.kt (Lógica de Escaneo con Delay)
Implementación del temporizador de 3 segundos para confirmar la lectura de un ISBN.

```kotlin
private fun startIsbnConfirmation(isbn: String) {
    confirmationJob?.cancel()
    confirmationJob = viewModelScope.launch {
        _uiState.update { it.copy(confirmingIsbn = isbn, confirmationProgress = 0f) }
        val totalSteps = 30 // 3 segundos
        for (step in 1..totalSteps) {
            delay(100)
            _uiState.update { it.copy(confirmationProgress = step.toFloat() / totalSteps) }
        }
        _uiState.update { it.copy(detectedIsbn = isbn) }
    }
}
```

---

## 3. Interfaz de Usuario (Jetpack Compose)

### AddVolumeScreen.kt (Edición y Recorte de Portada)
Uso de `rememberLauncherForActivityResult` para integrar el motor de cropping.

```kotlin
@Composable
fun AddVolumeScreen(viewModel: AddVolumeViewModel, onBack: () -> Unit) {
    val cropImageLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let { viewModel.onCoverPathChanged(it.toString()) }
        }
    }

    // Botón para disparar la cámara con recorte
    Button(onClick = { 
        cropImageLauncher.launch(CropImageContractOptions(uri = null, CropImageOptions(imageSourceIncludeCamera = true))) 
    }) { 
        Text("Cámara con Recorte") 
    }
}
```

---
**Nota**: El código completo está disponible en el repositorio adjunto al proyecto de investigación.
