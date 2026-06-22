# Código Fuente Completo - BiblioMobil

Este documento contiene la totalidad del código fuente de la aplicación **BiblioMobil**, organizado por paquetes y capas. Este anexo técnico sirve como referencia exhaustiva para la revisión del tribunal.

---

## Índice de Clases
1.  **Core & UI Principal**
    - BiblioMobilApp.kt
    - MainActivity.kt
    - SplashScreen.kt
    - Navigation (MainScreen, Screen)
2.  **Capa de Dominio (Business Logic)**
    - AI & Repository Interfaces
    - Use Cases (Fuzzy Match, Search)
    - Voice Recognition Interfaces
3.  **Capa de Datos (Data Layer)**
    - AI Implementation (Gemini)
    - Local DB (Room, DAOs, Entities)
    - Remote API (Retrofit, DTOs)
    - Voice Implementation
4.  **Inyección de Dependencias (Dagger Hilt)**
5.  **Pantallas y ViewModels (Feature Layers)**
    - Catálogo
    - Añadir/Editar Volumen
    - Escáner OCR
    - Colecciones
    - Detalle de Volumen
    - Ajustes & Backup

---

## BiblioMobilApp.kt

` kotlin
package com.fasby.bibliomobil

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BiblioMobilApp : Application()

 `  

## MainActivity.kt

` kotlin
package com.fasby.bibliomobil

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.fasby.bibliomobil.navigation.MainScreen
import com.fasby.bibliomobil.ui.SplashScreen
import com.fasby.bibliomobil.ui.theme.BiblioMobilTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Punto de entrada principal de la aplicación.
 * Maneja la lógica de permisos iniciales y la visualización de la Splash Screen.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Lanzador múltiple para asegurar los permisos críticos (Cámara y Audio)
    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false

        if (cameraGranted && audioGranted) {
            setupMainUi()
        } else {
            // Gestión académica del TFM: Aquí se pintaría una UI explicativa
            println("BiblioMobil: Permisos críticos rechazados.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val requiredPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

        val allPermissionsGranted = requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (allPermissionsGranted) {
            setupMainUi()
        } else {
            requestPermissionsLauncher.launch(requiredPermissions)
        }
    }

    private fun setupMainUi() {
        setContent {
            BiblioMobilTheme {
                var showSplash by remember { mutableStateOf(true) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showSplash) {
                        SplashScreen(onTimeout = { showSplash = false })
                    } else {
                        MainScreen()
                    }
                }
            }
        }
    }
}

 `  

## SplashScreen.kt

` kotlin
package com.fasby.bibliomobil.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // Estado para disparar las animaciones
    var startAnimation by remember { mutableStateOf(false) }
    
    // Animación de escala para el logo (efecto rebote suave)
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1.2f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    // Animación de opacidad
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500) // Duración de la splash: 2.5 segundos para que de tiempo a ver la animación
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo espectacular con icono de libros
            Icon(
                imageVector = Icons.Default.AutoStories,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .alpha(alpha),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Texto con degradado y estilo
            Text(
                text = "BiblioMobil",
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.alpha(alpha),
                letterSpacing = 2.sp
            )
            
            Text(
                text = "Tu biblioteca inteligente",
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.alpha(alpha).padding(top = 8.dp)
            )
        }
        
        // Indicador sutil en la parte inferior
        Text(
            text = "Cargando cultura...",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .alpha(alpha * 0.7f),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

 `  

## Color.kt

` kotlin
package com.fasby.bibliomobil.ui.theme

import androidx.compose.ui.graphics.Color

// Professional "Deep Ink" Palette
// Dark Theme Colors
val InkPrimary = Color(0xFF90CAF9)       // Light Blue 200
val InkOnPrimary = Color(0xFF0D47A1)     // Deep Blue 900
val InkPrimaryContainer = Color(0xFF1976D2)
val InkOnPrimaryContainer = Color(0xFFE3F2FD)

val InkSecondary = Color(0xFFB0BEC5)     // Blue Grey 200
val InkOnSecondary = Color(0xFF263238)   // Blue Grey 900
val InkSecondaryContainer = Color(0xFF455A64)
val InkOnSecondaryContainer = Color(0xFFECEFF1)

val InkTertiary = Color(0xFFFFB74D)      // Orange 300 (Accent)
val InkOnTertiary = Color(0xFFE65100)    // Deep Orange 900

val InkBackground = Color(0xFF0F172A)    // Deep Navy
val InkSurface = Color(0xFF1E293B)       // Slate Navy
val InkOnBackground = Color(0xFFF8FAFC)
val InkOnSurface = Color(0xFFF8FAFC)

// Light Theme Colors (Optional, but good for completeness)
val InkPrimaryLight = Color(0xFF0284C7)
val InkOnPrimaryLight = Color(0xFFFFFFFF)
val InkPrimaryContainerLight = Color(0xFFE0F2FE)

val InkSecondaryLight = Color(0xFF64748B)
val InkBackgroundLight = Color(0xFFF8FAFC)
val InkSurfaceLight = Color(0xFFFFFFFF)
val InkOnSurfaceLight = Color(0xFF0F172A)

 `  

## Theme.kt

` kotlin
package com.fasby.bibliomobil.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val InkDarkColorScheme = darkColorScheme(
    primary = InkPrimary,
    onPrimary = InkOnPrimary,
    primaryContainer = InkPrimaryContainer,
    onPrimaryContainer = InkOnPrimaryContainer,
    secondary = InkSecondary,
    onSecondary = InkOnSecondary,
    secondaryContainer = InkSecondaryContainer,
    onSecondaryContainer = InkOnSecondaryContainer,
    tertiary = InkTertiary,
    onTertiary = InkOnTertiary,
    background = InkBackground,
    onBackground = InkOnBackground,
    surface = InkSurface,
    onSurface = InkOnSurface,
    surfaceVariant = InkSurface,
    onSurfaceVariant = InkSecondary
)

private val InkLightColorScheme = lightColorScheme(
    primary = InkPrimaryLight,
    onPrimary = InkOnPrimaryLight,
    primaryContainer = InkPrimaryContainerLight,
    onPrimaryContainer = InkPrimaryLight,
    secondary = InkSecondaryLight,
    onSecondary = InkOnPrimaryLight,
    background = InkBackgroundLight,
    onBackground = InkOnSurfaceLight,
    surface = InkSurfaceLight,
    onSurface = InkOnSurfaceLight,
    surfaceVariant = InkBackgroundLight,
    onSurfaceVariant = InkSecondaryLight
)

@Composable
fun BiblioMobilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> InkDarkColorScheme
        else -> InkLightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

 `  

## Type.kt

` kotlin
package com.fasby.bibliomobil.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Professional Sans-Serif Typography
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

 `  

## AiRepository.kt (Domain)

` kotlin
package com.fasby.bibliomobil.domain.ai

interface AiRepository {
    suspend fun generateSummary(title: String, synopsis: String): String?
    suspend fun categorizeBook(title: String, synopsis: String): String?
}

 `  

## VolumeRepository.kt (Domain)

` kotlin
package com.fasby.bibliomobil.domain.repository

import com.fasby.bibliomobil.data.local.entity.AuthorEntity
import com.fasby.bibliomobil.data.local.entity.CollectionEntity
import com.fasby.bibliomobil.data.local.entity.LoanEntity
import com.fasby.bibliomobil.data.local.entity.VolumeEntity
import com.fasby.bibliomobil.data.local.model.DetailedVolume
import kotlinx.coroutines.flow.Flow

interface VolumeRepository {
    
    // Transacciones de lectura reactiva
    fun getAllVolumes(): Flow<List<DetailedVolume>>
    
    fun getVolumesByCollection(collectionId: String): Flow<List<DetailedVolume>>
    
    suspend fun getVolumeByIsbn(isbn: String): DetailedVolume?
    
    // Transacción de búsqueda optimizada por FTS5
    fun searchVolumes(query: String): Flow<List<DetailedVolume>>
    
    // Transacciones de escritura
    suspend fun saveCompleteVolume(volume: VolumeEntity, authors: List<AuthorEntity>)
    
    suspend fun saveCollection(collection: CollectionEntity)
    
    suspend fun deleteVolume(volume: VolumeEntity)

    suspend fun updateVolume(volume: VolumeEntity)

    fun getAllCollections(): Flow<List<CollectionEntity>>

    suspend fun getCollectionById(id: String): CollectionEntity?

    // Préstamos
    suspend fun registerLoan(isbn: String, lentTo: String)
    suspend fun markAsReturned(loanId: String)
    fun getLoanHistory(isbn: String): Flow<List<LoanEntity>>

    // Sincronización remota
    suspend fun searchRemoteBook(query: String): Pair<VolumeEntity, List<AuthorEntity>>?

    // Exportación
    suspend fun generateCsvReport(): String

    // Backup & Restore
    suspend fun clearDatabase()
    suspend fun createBackup(onUriReady: (android.net.Uri) -> Unit)
    suspend fun restoreBackup(uri: android.net.Uri): Boolean
}

 `  

## CalculateFuzzyMatchUseCase.kt (Domain)

` kotlin
package com.fasby.bibliomobil.domain.usecase

import javax.inject.Inject

class CalculateFuzzyMatchUseCase @Inject constructor() {
    
    /**
     * Devuelve un valor entre 0.0 y 1.0 que representa el porcentaje de similitud.
     */
    fun execute(str1: String, str2: String): Double {
        if (str1 == str2) return 1.0
        if (str1.isEmpty() || str2.isEmpty()) return 0.0

        val s1 = str1.lowercase()
        val s2 = str2.lowercase()
        val len1 = s1.length
        val len2 = s2.length

        // Optimizamos el espacio usando solo dos filas
        var prevRow = IntArray(len2 + 1) { it }
        var currentRow = IntArray(len2 + 1)

        for (i in 1..len1) {
            currentRow[0] = i
            for (j in 1..len2) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                currentRow[j] = minOf(
                    prevRow[j] + 1,
                    currentRow[j - 1] + 1,
                    prevRow[j - 1] + cost
                )
            }
            // Swap rows
            val temp = prevRow
            prevRow = currentRow
            currentRow = temp
        }

        val distance = prevRow[len2]
        val maxLen = maxOf(len1, len2)
        return (maxLen - distance).toDouble() / maxLen.toDouble()
    }
}

 `  

## SearchVolumesUseCase.kt (Domain)

` kotlin
package com.fasby.bibliomobil.domain.usecase

import com.fasby.bibliomobil.data.local.model.DetailedVolume
import com.fasby.bibliomobil.domain.repository.VolumeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchVolumesUseCase @Inject constructor(
    private val repository: VolumeRepository,
    private val calculateFuzzyMatchUseCase: CalculateFuzzyMatchUseCase
) {

    /**
     * Busca volúmenes y los ordena por similitud con el texto extraído por OCR.
     */
    fun executeOcrFuzzyMatch(ocrRawText: String): Flow<List<Pair<DetailedVolume, Double>>> {
        return repository.getAllVolumes().map { volumes ->
            volumes.map { volume ->
                val score = calculateFuzzyMatchUseCase.execute(ocrRawText, volume.volume.title)
                volume to score
            }.filter { it.second > 0.3 } // Filtro de confianza mínimo
                .sortedByDescending { it.second }
        }
    }
    
    fun executeTextSearch(query: String): Flow<List<DetailedVolume>> {
        return repository.searchVolumes(query)
    }
}

 `  

## VoiceRecognizerManager.kt (Domain)

` kotlin
package com.fasby.bibliomobil.domain.voice

import kotlinx.coroutines.flow.StateFlow

sealed interface VoiceRecognizerState {
    data object Idle : VoiceRecognizerState
    data object Listening : VoiceRecognizerState
    data class Success(val text: String) : VoiceRecognizerState
    data class Error(val message: String) : VoiceRecognizerState
}

interface VoiceRecognizerManager {
    val state: StateFlow<VoiceRecognizerState>
    fun startListening()
    fun stopListening()
    fun destroy()
}

 `  

## GeminiAiRepositoryImpl.kt (Data Implementations)

` kotlin
package com.fasby.bibliomobil.data.ai

import com.fasby.bibliomobil.BuildConfig
import com.fasby.bibliomobil.domain.ai.AiRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiAiRepositoryImpl @Inject constructor() : AiRepository {

    // Configuración de Seguridad (Safety Settings)
    private val safetySettings = listOf(
        SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE)
    )

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY,
        safetySettings = safetySettings
    )

    override suspend fun generateSummary(title: String, synopsis: String): String? {
        return try {
            val response = generativeModel.generateContent(
                content {
                    text("Genera un resumen corto y atractivo en español para el siguiente libro/cómic: $title. Sinopsis actual: $synopsis")
                }
            )
            response.text
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun categorizeBook(title: String, synopsis: String): String? {
        return try {
            val response = generativeModel.generateContent(
                content {
                    text("Basándote en el título '$title' y la sinopsis '$synopsis', ¿cuál es el género principal? Responde solo con una palabra (ej: Shonen, Seinen, Terror, Ciencia Ficción).")
                }
            )
            response.text?.trim()
        } catch (e: Exception) {
            null
        }
    }
}

 `  

## VolumeRepositoryImpl.kt (Data Implementations)

` kotlin
package com.fasby.bibliomobil.data.repository

import com.fasby.bibliomobil.data.local.dao.VolumeDao
import com.fasby.bibliomobil.data.local.database.AppDatabase
import com.fasby.bibliomobil.data.local.entity.*
import com.fasby.bibliomobil.data.local.model.DetailedVolume
import com.fasby.bibliomobil.data.remote.api.GoogleBooksApiService
import com.fasby.bibliomobil.domain.repository.VolumeRepository
import com.fasby.bibliomobil.di.IoDispatcher
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VolumeRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val volumeDao: VolumeDao,
    private val googleBooksApi: GoogleBooksApiService,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : VolumeRepository {

    override fun getAllVolumes(): Flow<List<DetailedVolume>> {
        return volumeDao.getAllDetailedVolumes()
    }

    override fun getVolumesByCollection(collectionId: String): Flow<List<DetailedVolume>> {
        return volumeDao.getVolumesByCollection(collectionId)
    }

    override suspend fun getVolumeByIsbn(isbn: String): DetailedVolume? = withContext(ioDispatcher) {
        volumeDao.getVolumeByIsbn(isbn)
    }

    override fun searchVolumes(query: String): Flow<List<DetailedVolume>> {
        val trimmedQuery = query.trim()
        // Si el query está vacío, devolvemos todo
        return if (trimmedQuery.isEmpty()) {
            volumeDao.getAllDetailedVolumes()
        } else {
            volumeDao.searchVolumesFts(trimmedQuery)
        }
    }

    override suspend fun saveCompleteVolume(
        volume: VolumeEntity, 
        authors: List<AuthorEntity>
    ) = withContext(ioDispatcher) {
        volumeDao.insertCompleteVolume(volume, authors)
    }

    override suspend fun saveCollection(collection: CollectionEntity) = withContext(ioDispatcher) {
        volumeDao.insertCollection(collection)
    }

    override suspend fun deleteVolume(volume: VolumeEntity) = withContext(ioDispatcher) {
        volumeDao.deleteVolume(volume)
    }

    override suspend fun updateVolume(volume: VolumeEntity) = withContext(ioDispatcher) {
        volumeDao.updateVolume(volume)
    }

    override fun getAllCollections(): Flow<List<CollectionEntity>> {
        return volumeDao.getAllCollections()
    }

    override suspend fun getCollectionById(id: String): CollectionEntity? = withContext(ioDispatcher) {
        volumeDao.getCollectionById(id)
    }

    override suspend fun registerLoan(isbn: String, lentTo: String): Unit = withContext(ioDispatcher) {
        volumeDao.insertLoan(LoanEntity(isbn = isbn, lentTo = lentTo))
    }

    override suspend fun markAsReturned(loanId: String): Unit = withContext(ioDispatcher) {
        volumeDao.getLoanById(loanId)?.let { loan ->
             volumeDao.updateLoan(loan.copy(returnDate = System.currentTimeMillis()))
        }
    }

    override fun getLoanHistory(isbn: String): Flow<List<LoanEntity>> {
        return volumeDao.getLoansForVolume(isbn)
    }

    override suspend fun searchRemoteBook(query: String): Pair<VolumeEntity, List<AuthorEntity>>? = withContext(ioDispatcher) {
        try {
            val response = try {
                googleBooksApi.searchBooks(query)
            } catch (e: Exception) {
                null
            }
            
            // Si no hay resultados por ISBN, intentamos una búsqueda general con el mismo término
            val items = if (response?.items.isNullOrEmpty() && query.startsWith("isbn:")) {
                val generalQuery = query.removePrefix("isbn:").trim()
                googleBooksApi.searchBooks(generalQuery, maxResults = 3).items
            } else {
                response?.items
            }

            val book = items?.firstOrNull()?.volumeInfo ?: return@withContext null

            android.util.Log.d("BiblioMobil", "Libro encontrado: ${book.title}")
            val authors = book.authors?.map { name ->
                AuthorEntity(id = UUID.randomUUID().toString(), name = name, role = "Autor")
            } ?: emptyList()

            // Intentamos extraer el ISBN13, si no, el ISBN10, si no, usamos el de la query o un UUID
            val remoteIsbn = book.industryIdentifiers?.find { it.type == "ISBN_13" }?.identifier
                ?: book.industryIdentifiers?.find { it.type == "ISBN_10" }?.identifier
                ?: query.filter { it.isDigit() }.ifBlank { UUID.randomUUID().toString() }

            val volume = VolumeEntity(
                isbn = remoteIsbn,
                collectionId = null,
                title = book.title,
                number = 0,
                publishedYear = book.publishedDate?.take(4)?.toIntOrNull() ?: 0,
                synopsis = book.description ?: "",
                coverPath = book.imageLinks?.thumbnail?.replace("http:", "https:") ?: "",
                rating = 0,
                isRead = false
            )

            Pair(volume, authors)
        } catch (e: Exception) {
            android.util.Log.e("BiblioMobil", "Error en búsqueda remota", e)
            null
        }
    }

    override suspend fun generateCsvReport(): String = withContext(ioDispatcher) {
        val volumes = volumeDao.getAllDetailedVolumesStatic()
        val header = "ISBN,Titulo,Autores,Año,Leido\n"
        val rows = volumes.joinToString("\n") { detailed ->
            val volume = detailed.volume
            val authors = detailed.authors.joinToString(";") { it.name }
            "${volume.isbn},\"${volume.title}\",\"$authors\",${volume.publishedYear},${if (volume.isRead) "SI" else "NO"}"
        }
        header + rows
    }

    override suspend fun clearDatabase() = withContext(ioDispatcher) {
        database.clearAllTables()
    }

    /**
     * Implementación de la creación de backup físico del archivo SQLite.
     * Utiliza un checkpoint de WAL para garantizar la integridad de los datos.
     */
    override suspend fun createBackup(onUriReady: (Uri) -> Unit): Unit = withContext(ioDispatcher) {
        try {
            // Forzamos un checkpoint para que todo el contenido de WAL pase al .db principal
            try {
                database.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)").moveToFirst()
            } catch (e: Exception) {
                android.util.Log.w("BiblioMobil", "WAL checkpoint failed or DB not ready, continuing with file copy", e)
            }

            val dbFile = context.getDatabasePath("biblio_mobil_db")
            if (dbFile.exists()) {
                // Copiamos el archivo de la base de datos a la caché para poder compartirlo
                val backupFile = File(context.cacheDir, "backup_bibliomobil_${System.currentTimeMillis()}.db")
                dbFile.copyTo(backupFile, overwrite = true)
                
                val contentUri = FileProvider.getUriForFile(
                    context,
                    "com.fasby.bibliomobil.fileprovider",
                    backupFile
                )
                onUriReady(contentUri)
            } else {
                android.util.Log.e("BiblioMobil", "Database file not found at ${dbFile.absolutePath}")
            }
        } catch (e: Exception) {
            android.util.Log.e("BiblioMobil", "Error fatal creando backup", e)
        }
    }

    /**
     * Restaura la base de datos reemplazando el archivo actual por uno externo.
     * Requiere el reinicio de la aplicación tras la operación.
     */
    override suspend fun restoreBackup(uri: Uri): Boolean = withContext(ioDispatcher) {
        try {
            database.close()
            val dbFile = context.getDatabasePath("biblio_mobil_db")
            context.contentResolver.openInputStream(uri)?.use { input ->
                dbFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            android.util.Log.e("BiblioMobil", "Error restaurando backup", e)
            false
        }
    }
}

 `  

## AndroidVoiceRecognizerManager.kt (Data Implementations)

` kotlin
package com.fasby.bibliomobil.data.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.fasby.bibliomobil.domain.voice.VoiceRecognizerManager
import com.fasby.bibliomobil.domain.voice.VoiceRecognizerState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidVoiceRecognizerManager @Inject constructor(
    @ApplicationContext private val context: Context
) : VoiceRecognizerManager, RecognitionListener {

    private val speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    
    private val _state = MutableStateFlow<VoiceRecognizerState>(VoiceRecognizerState.Idle)
    override val state: StateFlow<VoiceRecognizerState> = _state.asStateFlow()

    // Configuración del Intent nativo de reconocimiento de voz
    private val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault()) // Adapta al idioma local del móvil
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
    }

    init {
        speechRecognizer.setRecognitionListener(this)
    }

    override fun startListening() {
        _state.value = VoiceRecognizerState.Listening
        speechRecognizer.startListening(recognizerIntent)
    }

    override fun stopListening() {
        speechRecognizer.stopListening()
    }

    override fun destroy() {
        speechRecognizer.destroy()
    }

    // ========================================================================
    // Callbacks obligatorios de la interfaz RecognitionListener de Android
    // ========================================================================

    override fun onReadyForSpeech(params: Bundle?) {}
    
    override fun onBeginningOfSpeech() {
        _state.value = VoiceRecognizerState.Listening
    }

    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    
    override fun onEndOfSpeech() {
        _state.value = VoiceRecognizerState.Idle
    }

    override fun onError(error: Int) {
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Error de grabación de audio."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Faltan permisos de micrófono."
            SpeechRecognizer.ERROR_NETWORK -> "Error de red."
            SpeechRecognizer.ERROR_NO_MATCH -> "No se entendió el título. Inténtalo de nuevo."
            else -> "Error desconocido en el reconocimiento de voz."
        }
        _state.value = VoiceRecognizerState.Error(errorMessage)
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            // Nos quedamos con la hipótesis de texto con mayor porcentaje de acierto
            _state.value = VoiceRecognizerState.Success(matches[0])
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {}
    override fun onEvent(eventType: Int, params: Bundle?) {}
}

 `  

## AppDatabase.kt (Database)

` kotlin
package com.fasby.bibliomobil.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.fasby.bibliomobil.data.local.dao.VolumeDao
import com.fasby.bibliomobil.data.local.entity.*

@Database(
    entities = [
        VolumeEntity::class,
        CollectionEntity::class,
        AuthorEntity::class,
        VolumeAuthorCrossRef::class,
        VolumeFtsEntity::class,
        LoanEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun volumeDao(): VolumeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "biblio_mobil_db"
                ).fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

 `  

## VolumeDao.kt (Database)

` kotlin
package com.fasby.bibliomobil.data.local.dao

import androidx.room.*
import com.fasby.bibliomobil.data.local.entity.*
import com.fasby.bibliomobil.data.local.model.DetailedVolume
import kotlinx.coroutines.flow.Flow

@Dao
interface VolumeDao {

    // ========================================================================
    // INSERCIONES Y BORRADOS (Escritura)
    // ========================================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolume(volume: VolumeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: CollectionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthor(author: AuthorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolumeAuthorCrossRef(crossRef: VolumeAuthorCrossRef)

    /**
     * Una transacción atómica para insertar un volumen completo con sus autores.
     * Si falla la inserción de un autor, se revierte toda la operación.
     */
    @Transaction
    suspend fun insertCompleteVolume(
        volume: VolumeEntity, 
        authors: List<AuthorEntity>
    ) {
        insertVolume(volume)
        authors.forEach { author ->
            insertAuthor(author)
            insertVolumeAuthorCrossRef(
                VolumeAuthorCrossRef(isbn = volume.isbn, authorId = author.id)
            )
        }
    }

    @Delete
    suspend fun deleteVolume(volume: VolumeEntity)

    @Update
    suspend fun updateVolume(volume: VolumeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity)

    @Update
    suspend fun updateLoan(loan: LoanEntity)

    @Query("SELECT * FROM loans WHERE id = :loanId LIMIT 1")
    suspend fun getLoanById(loanId: String): LoanEntity?

    @Query("SELECT * FROM loans WHERE isbn = :isbn ORDER BY loanDate DESC")
    fun getLoansForVolume(isbn: String): Flow<List<LoanEntity>>

    // ========================================================================
    // CONSULTAS REACTIVAS (Lectura mediante Flow)
    // ========================================================================

    /**
     * Obtiene el catálogo completo ordenado por fecha de adición.
     * Al devolver [DetailedVolume], Room resolverá automáticamente las relaciones
     * con la colección y los autores subyacentes en paralelo.
     */
    @Transaction
    @Query("SELECT * FROM volumes ORDER BY createdAt DESC")
    fun getAllDetailedVolumes(): Flow<List<DetailedVolume>>

    @Transaction
    @Query("SELECT * FROM volumes ORDER BY createdAt DESC")
    suspend fun getAllDetailedVolumesStatic(): List<DetailedVolume>

    /**
     * Obtiene un volumen específico por su clave primaria (ISBN).
     */
    @Transaction
    @Query("SELECT * FROM volumes WHERE isbn = :isbn LIMIT 1")
    suspend fun getVolumeByIsbn(isbn: String): DetailedVolume?

    /**
     * Filtra volúmenes que pertenecen a una colección concreta.
     */
    @Transaction
    @Query("SELECT * FROM volumes WHERE collection_id = :collectionId ORDER BY number ASC")
    fun getVolumesByCollection(collectionId: String): Flow<List<DetailedVolume>>

    @Query("SELECT * FROM collections ORDER BY name ASC")
    fun getAllCollections(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collections WHERE id = :id LIMIT 1")
    suspend fun getCollectionById(id: String): CollectionEntity?

    // ========================================================================
    // LA JOYA DEL TFM: BÚSQUEDA INDEXADA FTS5
    // ========================================================================

    /**
     * Ejecuta una consulta MATCH de texto completo de alto rendimiento.
     * En lugar de escanear la tabla entera con un LIKE '%texto%', Room consulta
     * el índice invertido virtual de FTS5 y hace un JOIN con la tabla real.
     *
     * @param searchQuery Término formateado para SQLite (ej: "Dragon*")
     */
    @Transaction
    @Query("""
        SELECT * FROM volumes 
        WHERE title LIKE '%' || :searchQuery || '%' 
        OR synopsis LIKE '%' || :searchQuery || '%'
    """)
    fun searchVolumesFts(searchQuery: String): Flow<List<DetailedVolume>>
}

 `  

## AuthorEntity.kt (Database)

` kotlin
package com.fasby.bibliomobil.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "authors")
data class AuthorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val role: String // "Guionista", "Dibujante", "Escritor"
)

 `  

## CollectionEntity.kt (Database)

` kotlin
package com.fasby.bibliomobil.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey 
    val id: String, // Usaremos UUID.randomUUID().toString() o slugs
    val name: String,
    val publisher: String, // Editorial (Panini, Planeta, ECC...)
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

 `  

## VolumeEntity.kt (Database)

` kotlin
package com.fasby.bibliomobil.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "volumes",
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collection_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class VolumeEntity(
    @PrimaryKey 
    val isbn: String, // El código de barras o un ID autogenerado si no tiene
    @ColumnInfo(name = "collection_id", index = true) 
    val collectionId: String?, // Nullable si es un tomo único (One-shot)
    val title: String,
    val number: Int, // Número del tomo/volumen dentro de la colección
    val publishedYear: Int,
    val synopsis: String,
    val coverPath: String, // Ruta local en el almacenamiento del dispositivo para la foto
    val rating: Int, // Puntuación de 1 a 5 estrellas
    val personalReview: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

 `  

## VolumeAuthorCrossRef.kt (Database)

` kotlin
package com.fasby.bibliomobil.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "volume_author_cross_ref",
    primaryKeys = ["isbn", "authorId"],
    indices = [Index(value = ["authorId"])]
)
data class VolumeAuthorCrossRef(
    val isbn: String,
    val authorId: String
)

 `  

## VolumeFtsEntity.kt (Database)

` kotlin
package com.fasby.bibliomobil.data.local.entity

import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = VolumeEntity::class)
@Entity(tableName = "volumes_fts")
data class VolumeFtsEntity(
    val title: String,
    val synopsis: String
)

 `  

## LoanEntity.kt (Database)

` kotlin
package com.fasby.bibliomobil.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

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
    val returnDate: Long? = null // Null si aún no ha sido devuelto
)

 `  

## DetailedVolume.kt (Database)

` kotlin
package com.fasby.bibliomobil.data.local.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.fasby.bibliomobil.data.local.entity.AuthorEntity
import com.fasby.bibliomobil.data.local.entity.CollectionEntity
import com.fasby.bibliomobil.data.local.entity.LoanEntity
import com.fasby.bibliomobil.data.local.entity.VolumeAuthorCrossRef
import com.fasby.bibliomobil.data.local.entity.VolumeEntity

data class DetailedVolume(
    @Embedded 
    val volume: VolumeEntity,

    @Relation(
        parentColumn = "collection_id",
        entityColumn = "id"
    )
    val collection: CollectionEntity?,

    @Relation(
        parentColumn = "isbn",
        entityColumn = "id",
        associateBy = Junction(
            value = VolumeAuthorCrossRef::class,
            parentColumn = "isbn",
            entityColumn = "authorId"
        )
    )
    val authors: List<AuthorEntity>,

    @Relation(
        parentColumn = "isbn",
        entityColumn = "isbn"
    )
    val loanHistory: List<LoanEntity> = emptyList()
)

 `  

## GoogleBooksApiService.kt (Remote API)

` kotlin
package com.fasby.bibliomobil.data.remote.api

import com.fasby.bibliomobil.data.remote.dto.GoogleBooksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {

    /**
     * Consulta libros filtrando por metadatos o códigos ISBN masivos.
     * @param query Ejemplo: "isbn:9788411405126" o "Dragon Ball 01"
     */
    @GET("books/v1/volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 1
    ): GoogleBooksResponse
}

 `  

## GoogleBooksResponse.kt (Remote API)

` kotlin
package com.fasby.bibliomobil.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GoogleBooksResponse(
    @SerializedName("items") val items: List<BookItem>?
)

data class BookItem(
    @SerializedName("volumeInfo") val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    @SerializedName("title") val title: String,
    @SerializedName("authors") val authors: List<String>?,
    @SerializedName("publishedDate") val publishedDate: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("imageLinks") val imageLinks: ImageLinks?,
    @SerializedName("industryIdentifiers") val industryIdentifiers: List<IndustryIdentifier>?
)

data class IndustryIdentifier(
    @SerializedName("type") val type: String,
    @SerializedName("identifier") val identifier: String
)

data class ImageLinks(
    @SerializedName("thumbnail") val thumbnail: String?
)

 `  

## AiModule.kt (Dagger Hilt Modules)

` kotlin
package com.fasby.bibliomobil.di

import com.fasby.bibliomobil.data.ai.GeminiAiRepositoryImpl
import com.fasby.bibliomobil.domain.ai.AiRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    @Singleton
    abstract fun bindAiRepository(
        geminiAiRepositoryImpl: GeminiAiRepositoryImpl
    ): AiRepository
}

 `  

## DatabaseModule.kt (Dagger Hilt Modules)

` kotlin
package com.fasby.bibliomobil.di

import android.content.Context
import com.fasby.bibliomobil.data.local.dao.VolumeDao
import com.fasby.bibliomobil.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideVolumeDao(
        appDatabase: AppDatabase
    ): VolumeDao {
        return appDatabase.volumeDao()
    }
}

 `  

## DispatchersModule.kt (Dagger Hilt Modules)

` kotlin
package com.fasby.bibliomobil.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    // Also provide a default CoroutineDispatcher without qualifier if needed, 
    // but it's better to use qualifiers.
    // Given VolumeRepositoryImpl is already using it without qualifier, 
    // I'll provide one as default for now to fix the build quickly, 
    // but I should ideally update VolumeRepositoryImpl.
    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

 `  

## NetworkModule.kt (Dagger Hilt Modules)

` kotlin
package com.fasby.bibliomobil.di

import com.fasby.bibliomobil.data.remote.api.GoogleBooksApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (com.fasby.bibliomobil.BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val apiKey = com.fasby.bibliomobil.BuildConfig.GOOGLE_BOOKS_API_KEY

                // Inyección dinámica de la API Key en todas las peticiones
                val urlWithKey = originalRequest.url.newBuilder()
                    .setQueryParameter("key", apiKey)
                    .build()
                
                // Configuración de cabeceras de seguridad para restringir el uso de la Key
                val request = originalRequest.newBuilder()
                    .url(urlWithKey)
                    .header("X-Android-Package", "com.fasby.bibliomobil")
                    .header("X-Android-Cert", "C90FDADC5CA9C56618328F6695584ABCBFFDEB29")
                    .build()
                
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        }

    @Provides
    @Singleton
    fun provideGoogleBooksApiService(retrofit: Retrofit): GoogleBooksApiService {
        return retrofit.create(GoogleBooksApiService::class.java)
    }
}

 `  

## RepositoryModule.kt (Dagger Hilt Modules)

` kotlin
package com.fasby.bibliomobil.di

import com.fasby.bibliomobil.data.repository.VolumeRepositoryImpl
import com.fasby.bibliomobil.domain.repository.VolumeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVolumeRepository(
        volumeRepositoryImpl: VolumeRepositoryImpl
    ): VolumeRepository
}

 `  

## VoiceModule.kt (Dagger Hilt Modules)

` kotlin
package com.fasby.bibliomobil.di

import com.fasby.bibliomobil.data.voice.AndroidVoiceRecognizerManager
import com.fasby.bibliomobil.domain.voice.VoiceRecognizerManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VoiceModule {

    @Binds
    @Singleton
    abstract fun bindVoiceRecognizerManager(
        androidVoiceRecognizerManager: AndroidVoiceRecognizerManager
    ): VoiceRecognizerManager
}

 `  

## CatalogViewModel.kt (Features)

` kotlin
package com.fasby.bibliomobil.catalog.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasby.bibliomobil.data.local.model.DetailedVolume
import com.fasby.bibliomobil.domain.repository.VolumeRepository
import com.fasby.bibliomobil.domain.voice.VoiceRecognizerManager
import com.fasby.bibliomobil.domain.voice.VoiceRecognizerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Representa el estado de la interfaz de usuario del catálogo general.
 */
data class CatalogUiState(
    val searchQuery: String = "",
    val volumes: List<DetailedVolume> = emptyList(),
    val voiceState: VoiceRecognizerState = VoiceRecognizerState.Idle,
    val isLoading: Boolean = false
)

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val repository: VolumeRepository,
    private val voiceRecognizerManager: VoiceRecognizerManager
) : ViewModel() {

    // Flujo mutable interno para controlar la cadena de búsqueda (texto o voz)
    private val _searchQuery = MutableStateFlow("")
    private val _collectionId = MutableStateFlow<String?>(null)

    // Unificamos múltiples fuentes de estado en un único flujo para la UI.
    // flatMapLatest asegura que si cambia la búsqueda, se cancele la consulta anterior.
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CatalogUiState> = combine(
        _searchQuery,
        _collectionId,
        voiceRecognizerManager.state
    ) { query, collectionId, voiceState ->
        Triple(query, collectionId, voiceState)
    }.flatMapLatest { (currentQuery, collectionId, voiceState) ->
        val flow = if (collectionId != null) {
            repository.getVolumesByCollection(collectionId)
        } else {
            repository.searchVolumes(currentQuery)
        }
        
        flow.map { volumesList ->
            CatalogUiState(
                searchQuery = currentQuery,
                volumes = volumesList,
                voiceState = voiceState,
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CatalogUiState(isLoading = true)
    )

    init {
        // Escuchamos el motor de voz para sincronizar el cuadro de texto si se dicta algo
        viewModelScope.launch {
            voiceRecognizerManager.state.collect { voiceState ->
                if (voiceState is VoiceRecognizerState.Success) {
                    _searchQuery.value = voiceState.text
                }
            }
        }
    }

    /**
     * Actualiza el query cuando el usuario escribe manualmente en el teclado.
     */
    fun onSearchQueryChanged(newQuery: String) {
        // Si el motor de voz estaba en modo éxito o error, lo reseteamos al escribir
        if (voiceRecognizerManager.state.value !is VoiceRecognizerState.Idle) {
            voiceRecognizerManager.stopListening()
        }
        _collectionId.value = null // Reset filter when searching
        _searchQuery.value = newQuery
    }

    fun filterByCollection(collectionId: String?) {
        _collectionId.value = collectionId
    }

    fun exportToCsv(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val csv = repository.generateCsvReport()
            onResult(csv)
        }
    }

    // ========================================================================
    // Gestión del ciclo de vida del reconocimiento de voz
    // ========================================================================

    fun toggleVoiceSearch() {
        when (voiceRecognizerManager.state.value) {
            is VoiceRecognizerState.Listening -> voiceRecognizerManager.stopListening()
            else -> voiceRecognizerManager.startListening()
        }
    }

    override fun onCleared() {
        super.onCleared()
        // CRÍTICO TFM: Liberamos el micrófono del sistema operativo para evitar fugas
        voiceRecognizerManager.destroy()
    }
}

 `  

## CatalogScreen.kt (Features)

` kotlin
package com.fasby.bibliomobil.catalog.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fasby.bibliomobil.catalog.presentation.CatalogViewModel
import com.fasby.bibliomobil.domain.voice.VoiceRecognizerState
import com.fasby.bibliomobil.presentation.catalog.VolumeCard
import android.content.Intent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel,
    onVolumeClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var csvDataToSave by remember { mutableStateOf<String?>(null) }

    // Launcher para crear el archivo CSV físicamente en el dispositivo
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let { targetUri ->
            csvDataToSave?.let { data ->
                context.contentResolver.openOutputStream(targetUri)?.use { outputStream ->
                    outputStream.write(data.toByteArray())
                }
                csvDataToSave = null // Limpiamos tras guardar
            }
        }
    }

    // Animación de color adaptativa para el botón de dictado (Rojo si escucha)
    val micButtonColor by animateColorAsState(
        targetValue = if (uiState.voiceState is VoiceRecognizerState.Listening) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.primaryContainer
        },
        label = "MicColorAnimation"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Mi Biblioteca Inteligente") },
                actions = {
                    // Botón de Exportar (Genera documento físico)
                    IconButton(onClick = {
                        viewModel.exportToCsv { data ->
                            csvDataToSave = data
                            createDocumentLauncher.launch("biblioteca_bibliomobil.csv")
                        }
                    }) {
                        Icon(Icons.Default.Description, contentDescription = "Exportar a CSV")
                    }
                }
            )
        },
        floatingActionButton = {
            // Botón Flotante para activar/desactivar la búsqueda por voz
            FloatingActionButton(
                onClick = { viewModel.toggleVoiceSearch() },
                containerColor = micButtonColor
            ) {
                Icon(
                    imageVector = if (uiState.voiceState is VoiceRecognizerState.Listening) {
                        Icons.Default.MicOff
                    } else {
                        Icons.Default.Mic
                    },
                    contentDescription = "Buscar por voz",
                    tint = if (uiState.voiceState is VoiceRecognizerState.Listening) {
                        Color.White
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // 1. Barra de búsqueda superior
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar título o sinopsis...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            // 2. Feedback visual del estado del micrófono o errores
            Spacer(modifier = Modifier.height(8.dp))
            when (val voice = uiState.voiceState) {
                is VoiceRecognizerState.Listening -> {
                    Text(
                        text = "🎙️ Escuchando... Habla ahora",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is VoiceRecognizerState.Error -> {
                    Text(
                        text = "⚠️ ${voice.message}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is VoiceRecognizerState.Success -> {
                    Text(
                        text = "✓ Entendido: \"${voice.text}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                else -> Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Listado reactivo conectado a Room FTS5
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.volumes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron cómics en el catálogo.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items = uiState.volumes, key = { it.volume.isbn }) { detailedVolume ->
                        VolumeCard(
                            volume = detailedVolume.volume,
                            modifier = Modifier.clickable { onVolumeClick(detailedVolume.volume.isbn) }
                        )
                    }
                }
            }
        }
    }
}

 `  

## VolumeCard.kt (Features)

` kotlin
package com.fasby.bibliomobil.presentation.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.fasby.bibliomobil.data.local.entity.VolumeEntity

@Composable
fun VolumeCard(
    volume: VolumeEntity,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            // RENDERIZADO INTELIGENTE DE PORTADAS MEDIANTE COIL
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(volume.coverPath) // Acepta URLs de internet, URIs locales o archivos en crudo
                    .crossfade(true) // Animación suave de desvanecimiento al cargar la foto
                    .build(),
                contentDescription = "Portada de ${volume.title}",
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop, // Recorta y rellena el espacio sin deformar la portada
                // Recursos visuales opcionales para placeholders de carga
                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                error = painterResource(id = android.R.drawable.ic_dialog_alert)
            )

            // Panel de información textual al lado derecho de la portada
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (volume.number > 0) "${volume.title} #${volume.number}" else volume.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis // Añade "..." si el título es demasiado largo
                    )
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    Text(
                        text = "Año: ${volume.publishedYear}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Fila inferior para pintar la puntuación por estrellas del usuario
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "⭐ ".repeat(volume.rating.coerceIn(1, 5)),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    if (volume.isRead) {
                        SuggestionChip(
                            onClick = { },
                            label = { Text("Leído", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }
        }
    }
}

 `  

## AddVolumeViewModel.kt (Features)

` kotlin
package com.fasby.bibliomobil.add_volume.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasby.bibliomobil.data.local.entity.AuthorEntity
import com.fasby.bibliomobil.data.local.entity.CollectionEntity
import com.fasby.bibliomobil.data.local.entity.VolumeEntity
import com.fasby.bibliomobil.domain.ai.AiRepository
import com.fasby.bibliomobil.domain.repository.VolumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddVolumeUiState(
    val isbn: String = "",
    val title: String = "",
    val authors: String = "",
    val publishedYear: String = "",
    val synopsis: String = "",
    val coverPath: String = "",
    val collectionId: String? = null,
    val collections: List<CollectionEntity> = emptyList(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val isEditMode: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddVolumeViewModel @Inject constructor(
    private val repository: VolumeRepository,
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddVolumeUiState())
    val uiState: StateFlow<AddVolumeUiState> = combine(
        _uiState,
        repository.getAllCollections()
    ) { state, collections ->
        state.copy(collections = collections)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AddVolumeUiState())

    fun onIsbnChanged(isbn: String) {
        _uiState.update { it.copy(isbn = isbn) }
    }

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onAuthorsChanged(authors: String) {
        _uiState.update { it.copy(authors = authors) }
    }

    fun onPublishedYearChanged(year: String) {
        _uiState.update { it.copy(publishedYear = year) }
    }

    fun onSynopsisChanged(synopsis: String) {
        _uiState.update { it.copy(synopsis = synopsis) }
    }

    fun onCoverPathChanged(path: String) {
        _uiState.update { it.copy(coverPath = path) }
    }

    fun generateAiSummary() {
        val title = _uiState.value.title
        val currentSynopsis = _uiState.value.synopsis
        if (title.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val summary = aiRepository.generateSummary(title, currentSynopsis)
            if (summary != null) {
                _uiState.update { it.copy(synopsis = summary, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al conectar con Gemini.") }
            }
        }
    }

    fun onCollectionChanged(id: String?) {
        _uiState.update { it.copy(collectionId = id) }
    }

    fun setInitialIsbn(isbn: String) {
        if (isbn.isNotBlank() && _uiState.value.isbn != isbn) {
            viewModelScope.launch {
                val existingVolume = repository.getVolumeByIsbn(isbn)
                if (existingVolume != null) {
                    _uiState.update { 
                        it.copy(
                            isbn = existingVolume.volume.isbn,
                            title = existingVolume.volume.title,
                            authors = existingVolume.authors.joinToString(", ") { it.name },
                            publishedYear = existingVolume.volume.publishedYear.toString(),
                            synopsis = existingVolume.volume.synopsis,
                            coverPath = existingVolume.volume.coverPath,
                            collectionId = existingVolume.volume.collectionId,
                            isEditMode = true
                        )
                    }
                } else {
                    _uiState.update { it.copy(isbn = isbn, isEditMode = false) }
                    autocomplete()
                }
            }
        }
    }

    /**
     * Inicia la recuperación automática de datos desde Google Books.
     * Realiza una búsqueda por ISBN y, si falla, una búsqueda general.
     */
    fun autocomplete() {
        val rawIsbn = _uiState.value.isbn
        val isbn = rawIsbn.uppercase().replace(Regex("[^0-9X]"), "")
        if (isbn.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isbn = isbn, isLoading = true, error = null) }
            val result = repository.searchRemoteBook("isbn:$isbn")
            if (result != null) {
                val (volume, authors) = result
                _uiState.update { 
                    it.copy(
                        title = volume.title,
                        authors = authors.joinToString(", ") { it.name },
                        publishedYear = volume.publishedYear.toString(),
                        synopsis = volume.synopsis,
                        coverPath = volume.coverPath,
                        isLoading = false
                    )
                }
            } else {
                // Fallback: Si falla por ISBN, intentamos una búsqueda general por el código
                val generalResult = repository.searchRemoteBook(isbn)
                if (generalResult != null) {
                    val (volume, authors) = generalResult
                    _uiState.update { 
                        it.copy(
                            title = volume.title,
                            authors = authors.joinToString(", ") { it.name },
                            publishedYear = volume.publishedYear.toString(),
                            synopsis = volume.synopsis,
                            coverPath = volume.coverPath,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "No se encontraron datos para este ISBN ($isbn).") }
                }
            }
        }
    }

    /**
     * Persiste el volumen y sus autores en la base de datos local.
     */
    fun saveVolume() {
        viewModelScope.launch {
            val state = _uiState.value
            
            // Si es modo edición, podríamos necesitar recuperar el rating y estado de lectura original
            val existing = if (state.isEditMode) repository.getVolumeByIsbn(state.isbn) else null
            
            val volume = VolumeEntity(
                isbn = state.isbn,
                collectionId = state.collectionId,
                title = state.title,
                number = 0,
                publishedYear = state.publishedYear.toIntOrNull() ?: 0,
                synopsis = state.synopsis,
                coverPath = state.coverPath,
                rating = existing?.volume?.rating ?: 0,
                isRead = existing?.volume?.isRead ?: false,
                createdAt = existing?.volume?.createdAt ?: System.currentTimeMillis()
            )
            val authors = state.authors.split(",").map { 
                AuthorEntity(id = java.util.UUID.randomUUID().toString(), name = it.trim(), role = "Autor") 
            }

            repository.saveCompleteVolume(volume, authors)
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}

 `  

## AddVolumeScreen.kt (Features)

` kotlin
package com.fasby.bibliomobil.add_volume.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.fasby.bibliomobil.add_volume.presentation.AddVolumeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVolumeScreen(
    viewModel: AddVolumeViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isSaved) {
        onBack()
    }

    // Launcher para Cropping (Galería o Cámara con Crop)
    val cropImageLauncher = rememberLauncherForActivityResult(
        contract = CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let { viewModel.onCoverPathChanged(it.toString()) }
        }
    }

    var showImageSourceDialog by remember { mutableStateOf(false) }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Seleccionar Portada") },
            text = { Text("¿Desde dónde quieres añadir la imagen?") },
            confirmButton = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { 
                            cropImageLauncher.launch(
                                CropImageContractOptions(
                                    uri = null,
                                    cropImageOptions = CropImageOptions(
                                        imageSourceIncludeCamera = false,
                                        imageSourceIncludeGallery = true
                                    )
                                )
                            )
                            showImageSourceDialog = false 
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Galería")
                    }
                    Button(
                        onClick = { 
                            cropImageLauncher.launch(
                                CropImageContractOptions(
                                    uri = null,
                                    cropImageOptions = CropImageOptions(
                                        imageSourceIncludeCamera = true,
                                        imageSourceIncludeGallery = false
                                    )
                                )
                            )
                            showImageSourceDialog = false 
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Cámara")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showImageSourceDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "Editar Tomo" else "Añadir Nuevo Tomo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.saveVolume() }) {
                Icon(Icons.Default.Save, contentDescription = "Guardar")
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sección de Portada
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { showImageSourceDialog = true },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    if (uiState.coverPath.isNotEmpty()) {
                        AsyncImage(
                            model = uiState.coverPath,
                            contentDescription = "Portada",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(48.dp))
                            Text("Añadir Portada", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.isbn,
                    onValueChange = { viewModel.onIsbnChanged(it) },
                    label = { Text("ISBN / Código de barras") },
                    modifier = Modifier.weight(1f),
                    readOnly = uiState.isEditMode // ISBN suele ser primario, no editarlo si ya existe
                )
                
                if (!uiState.isEditMode) {
                    IconButton(
                        onClick = { viewModel.autocomplete() },
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = "Autocompletar")
                        }
                    }
                }
            }

            if (uiState.error != null) {
                Text(uiState.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.onTitleChanged(it) },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            // Selector de Colección
            var expanded by remember { mutableStateOf(false) }
            val selectedCollection = uiState.collections.find { it.id == uiState.collectionId }
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCollection?.name ?: "Sin colección",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Colección") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Sin colección") },
                        onClick = {
                            viewModel.onCollectionChanged(null)
                            expanded = false
                        }
                    )
                    uiState.collections.forEach { collection ->
                        DropdownMenuItem(
                            text = { Text(collection.name) },
                            onClick = {
                                viewModel.onCollectionChanged(collection.id)
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.authors,
                onValueChange = { viewModel.onAuthorsChanged(it) },
                label = { Text("Autores (separados por coma)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.publishedYear,
                onValueChange = { viewModel.onPublishedYearChanged(it) },
                label = { Text("Año de publicación") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.synopsis,
                onValueChange = { viewModel.onSynopsisChanged(it) },
                label = { Text("Sinopsis") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                trailingIcon = {
                    IconButton(onClick = { viewModel.generateAiSummary() }) {
                        Icon(Icons.Default.Psychology, contentDescription = "Mejorar con IA", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
            
            OutlinedTextField(
                value = uiState.coverPath,
                onValueChange = { viewModel.onCoverPathChanged(it) },
                label = { Text("URL de Portada") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

 `  

## CameraOcrViewModel.kt (Features)

` kotlin
package com.fasby.bibliomobil.camera.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasby.bibliomobil.data.local.model.DetailedVolume
import com.fasby.bibliomobil.domain.usecase.SearchVolumesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import kotlinx.coroutines.delay

/**
 * Representa los diferentes estados de la pantalla de escaneo por cámara.
 */
data class CameraOcrUiState(
    val rawOcrText: String = "",
    val matchedVolumes: List<Pair<DetailedVolume, Double>> = emptyList(),
    val isProcessing: Boolean = false,
    val detectedIsbn: String? = null,
    val lastNavigatedIsbn: String? = null,
    val confirmingIsbn: String? = null,
    val confirmationProgress: Float = 0f
)

@HiltViewModel
class CameraOcrViewModel @Inject constructor(
    private val searchVolumesUseCase: SearchVolumesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraOcrUiState())
    val uiState: StateFlow<CameraOcrUiState> = _uiState.asStateFlow()

    private var analysisJob: Job? = null
    private var confirmationJob: Job? = null

    /**
     * Callback de entrada secundario que invocará el OcrImageAnalyzer en cada frame exitoso.
     */
    fun onTextDetectedFromCamera(rawText: String) {
        val sanitizedForIsbn = rawText.uppercase().replace(Regex("[^0-9X]"), "")
        
        val isbnRegex = Regex("(?:\\d{13}|\\d{9}[0-9X])")
        val match = isbnRegex.find(sanitizedForIsbn)
        
        if (match != null) {
            val isbn = match.value
            
            // Si es un ISBN nuevo y no es el último que navegamos
            if (isbn != _uiState.value.lastNavigatedIsbn) {
                if (isbn != _uiState.value.confirmingIsbn) {
                    startIsbnConfirmation(isbn)
                }
                return 
            }
        } else {
            // Si perdemos el foco del ISBN, cancelamos la confirmación pendiente
            if (_uiState.value.confirmingIsbn != null) {
                confirmationJob?.cancel()
                _uiState.update { it.copy(confirmingIsbn = null, confirmationProgress = 0f) }
            }
        }

        // Si el usuario deja de apuntar al libro, permitimos resetear el "lastNavigatedIsbn"
        if (sanitizedForIsbn.isBlank() || (match == null && sanitizedForIsbn.length > 5)) {
             _uiState.update { it.copy(lastNavigatedIsbn = null) }
        }

        // 2. Flujo de Búsqueda Difusa
        val textForFuzzy = rawText.trim()
        if (textForFuzzy == _uiState.value.rawOcrText || textForFuzzy.isBlank()) return

        _uiState.update { it.copy(rawOcrText = textForFuzzy, isProcessing = true) }

        analysisJob?.cancel()
        analysisJob = viewModelScope.launch {
            searchVolumesUseCase.executeOcrFuzzyMatch(ocrRawText = textForFuzzy)
                .collect { results ->
                    _uiState.update { 
                        it.copy(
                            matchedVolumes = results,
                            isProcessing = false
                        )
                    }
                }
        }
    }

    private fun startIsbnConfirmation(isbn: String) {
        confirmationJob?.cancel()
        confirmationJob = viewModelScope.launch {
            _uiState.update { it.copy(confirmingIsbn = isbn, confirmationProgress = 0f) }
            
            val totalTimeMs = 3000L
            val stepMs = 100L
            val totalSteps = (totalTimeMs / stepMs).toInt()
            
            for (step in 1..totalSteps) {
                delay(stepMs)
                _uiState.update { it.copy(confirmationProgress = step.toFloat() / totalSteps) }
            }
            
            // Confirmado tras 3 segundos
            _uiState.update { 
                it.copy(
                    detectedIsbn = isbn, 
                    lastNavigatedIsbn = isbn,
                    confirmingIsbn = null,
                    confirmationProgress = 0f
                )
            }
        }
    }

    fun onNavigatedToAddVolume() {
        _uiState.update { it.copy(detectedIsbn = null, confirmingIsbn = null, confirmationProgress = 0f) }
        confirmationJob?.cancel()
    }
}

 `  

## CameraOcrScreen.kt (Features)

` kotlin
package com.fasby.bibliomobil.camera.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fasby.bibliomobil.camera.OcrImageAnalyzer
import com.fasby.bibliomobil.camera.presentation.CameraOcrViewModel
import com.fasby.bibliomobil.presentation.catalog.VolumeCard

@Composable
fun CameraOcrScreen(
    viewModel: CameraOcrViewModel,
    onIsbnDetected: (String) -> Unit,
    onVolumeClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Escuchamos el estado del ViewModel de forma reactiva a los flujos de la cámara
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 1. Efecto Secundario: Si el OCR detecta un patrón de ISBN, disparamos la navegación
    LaunchedEffect(uiState.detectedIsbn) {
        uiState.detectedIsbn?.let { isbn ->
            onIsbnDetected(isbn)
            viewModel.onNavigatedToAddVolume()
        }
    }

    // Memorizamos el analizador de imágenes pasándole el callback del ViewModel
    val ocrAnalyzer = remember {
        OcrImageAnalyzer { rawText ->
            viewModel.onTextDetectedFromCamera(rawText)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Capa del fondo: El visor de la cámara nativa en tiempo real
        CameraPreviewLayout(ocrImageAnalyzer = ocrAnalyzer)

        // 2. Capa intermedia: Indicador de carga o progreso de ISBN
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 32.dp)
        ) {
            if (uiState.isProcessing) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            
            uiState.confirmingIsbn?.let { isbn ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "Confirmando ISBN: $isbn",
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    LinearProgressIndicator(
                        progress = { uiState.confirmationProgress },
                        modifier = Modifier.width(200.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // 3. Capa superior: Panel inferior con los resultados de la coincidencia difusa
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.70f)) // Fondo oscuro translúcido para legibilidad
                .padding(vertical = 16.dp)
                .heightIn(max = 300.dp) // Limitamos el alto máximo para no tapar toda la cámara
        ) {
            Text(
                text = if (uiState.matchedVolumes.isEmpty()) "Apunta a la portada de un cómic..." else "Coincidencias locales encontradas:",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            )

            // Lista perezosa reactiva a los aciertos del motor de búsqueda
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(items = uiState.matchedVolumes, key = { it.first.volume.isbn }) { (detailedVolume, similarity) ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Pintamos la tarjeta del volumen
                        VolumeCard(
                            volume = detailedVolume.volume,
                            modifier = Modifier.clickable { onVolumeClick(detailedVolume.volume.isbn) }
                        )
                        
                        // Superponemos una etiqueta flotante con el porcentaje de precisión del algoritmo
                        Badge(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 16.dp, end = 24.dp)
                        ) {
                            Text(
                                text = "${(similarity * 100).toInt()}% match",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}

 `  

## CameraPreviewLayout.kt (Features)

` kotlin
package com.fasby.bibliomobil.camera.ui

import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.fasby.bibliomobil.camera.OcrImageAnalyzer
import java.util.concurrent.Executors

@Composable
fun CameraPreviewLayout(
    ocrImageAnalyzer: OcrImageAnalyzer,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Ejecutor dedicado en un hilo secundario para el análisis de frames
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }

    // Liberamos el ejecutor cuando el Composable sale de la composición
    DisposableEffect(Unit) {
        onDispose {
            analysisExecutor.shutdown()
        }
    }

    // El puente de conexión entre las vistas clásicas de Android y Jetpack Compose
    AndroidView(
        factory = { ctx ->
            // 1. Inicializamos el contenedor físico donde se proyectará la lente
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = modifier.fillMaxSize(),
        update = { previewView ->
            // 2. Obtenemos la instancia del proveedor de la cámara atada al ciclo de vida
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // Caso de uso A: La previsualización fluida en pantalla
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                // Caso de uso B: El pipeline de análisis computacional
                val imageAnalysis = ImageAnalysis.Builder()
                    // Estrategia: Si el hilo se satura, descarta el frame viejo y analiza el más reciente
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build().also {
                        it.setAnalyzer(analysisExecutor, ocrImageAnalyzer)
                    }

                // Selector de lente física (Cámara trasera)
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // 3. Desvinculamos cualquier caso de uso previo antes de rebindear
                    cameraProvider.unbindAll()

                    // 4. Vinculamos de forma atómica el ciclo de vida de la Activity/Fragment 
                    // con los casos de uso de previsualización y análisis de IA
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (exc: Exception) {
                    println("BiblioMobil CameraX Error al vincular ciclo de vida: ${exc.localizedMessage}")
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

 `  

## OcrImageAnalyzer.kt (Features)

` kotlin
package com.fasby.bibliomobil.camera

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class OcrImageAnalyzer(
    private val onTextRecognized: (String) -> Unit
) : ImageAnalysis.Analyzer {

    // Inicializamos el cliente de reconocimiento de texto en local (Latín/Español)
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        // 1. Extraemos la imagen nativa del buffer de la cámara
        val mediaImage = imageProxy.image
        
        if (mediaImage != null) {
            // 2. Convertimos el formato multimedia (YUV_420_888) al objeto InputImage de ML Kit,
            // pasando los grados de rotación física del dispositivo para que procese el texto al derecho.
            val image = InputImage.fromMediaImage(
                mediaImage, 
                imageProxy.imageInfo.rotationDegrees
            )

            // 3. Lanzamos el procesamiento asíncrono sobre la NPU/GPU local
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val detectedText = visionText.text
                    if (detectedText.isNotBlank()) {
                        // Notificamos al callback del hilo de ejecución del ViewModel
                        onTextRecognized(detectedText)
                    }
                }
                .addOnFailureListener { e ->
                    println("BiblioMobil OCR Error: ${e.localizedMessage}")
                }
                .addOnCompleteListener {
                    // 4. CRÍTICO: Cerramos el ImageProxy obligatoriamente.
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}

 `  

## CollectionViewModel.kt (Features)

` kotlin
package com.fasby.bibliomobil.collections.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasby.bibliomobil.data.local.entity.CollectionEntity
import com.fasby.bibliomobil.domain.repository.VolumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CollectionUiState(
    val collections: List<CollectionEntity> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val repository: VolumeRepository
) : ViewModel() {

    val uiState: StateFlow<CollectionUiState> = repository.getAllCollections()
        .map { CollectionUiState(collections = it, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CollectionUiState(isLoading = true))

    fun addCollection(name: String, publisher: String) {
        viewModelScope.launch {
            val collection = CollectionEntity(
                id = UUID.randomUUID().toString(),
                name = name,
                publisher = publisher
            )
            repository.saveCollection(collection)
        }
    }
}

 `  

## CollectionScreen.kt (Features)

` kotlin
package com.fasby.bibliomobil.collections.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fasby.bibliomobil.collections.presentation.CollectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(
    viewModel: CollectionViewModel,
    onCollectionClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis Colecciones") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Colección")
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.collections) { collection ->
                    Card(
                        onClick = { onCollectionClick(collection.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = collection.name, style = MaterialTheme.typography.titleLarge)
                            Text(text = "Editorial: ${collection.publisher}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        var name by remember { mutableStateOf("") }
        var publisher by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nueva Colección") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                    OutlinedTextField(value = publisher, onValueChange = { publisher = it }, label = { Text("Editorial") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.addCollection(name, publisher)
                    showDialog = false
                }) { Text("Crear") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

 `  

## VolumeDetailViewModel.kt (Features)

` kotlin
package com.fasby.bibliomobil.volume_detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasby.bibliomobil.data.local.model.DetailedVolume
import com.fasby.bibliomobil.data.local.entity.CollectionEntity
import com.fasby.bibliomobil.domain.repository.VolumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VolumeDetailUiState(
    val volume: DetailedVolume? = null,
    val collections: List<CollectionEntity> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class VolumeDetailViewModel @Inject constructor(
    private val repository: VolumeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VolumeDetailUiState())
    val uiState: StateFlow<VolumeDetailUiState> = combine(
        _uiState,
        repository.getAllCollections()
    ) { state, collections ->
        state.copy(collections = collections)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VolumeDetailUiState())

    fun loadVolume(isbn: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val detailedVolume = repository.getVolumeByIsbn(isbn)
            _uiState.update { it.copy(volume = detailedVolume, isLoading = false) }
        }
    }

    fun updateCollection(collectionId: String?) {
        val currentVolume = _uiState.value.volume?.volume ?: return
        viewModelScope.launch {
            repository.updateVolume(currentVolume.copy(collectionId = collectionId))
            loadVolume(currentVolume.isbn)
        }
    }

    fun toggleReadStatus() {
        val currentVolume = _uiState.value.volume?.volume ?: return
        viewModelScope.launch {
            val updatedVolume = currentVolume.copy(isRead = !currentVolume.isRead)
            repository.updateVolume(updatedVolume)
            loadVolume(updatedVolume.isbn)
        }
    }

    fun updateRating(rating: Int) {
        val currentVolume = _uiState.value.volume?.volume ?: return
        viewModelScope.launch {
            repository.updateVolume(currentVolume.copy(rating = rating))
            loadVolume(currentVolume.isbn)
        }
    }

    fun updateReview(review: String) {
        val currentVolume = _uiState.value.volume?.volume ?: return
        viewModelScope.launch {
            repository.updateVolume(currentVolume.copy(personalReview = review))
            loadVolume(currentVolume.isbn)
        }
    }

    fun registerLoan(lentTo: String) {
        val isbn = _uiState.value.volume?.volume?.isbn ?: return
        viewModelScope.launch {
            repository.registerLoan(isbn, lentTo)
            loadVolume(isbn)
        }
    }

    fun markAsReturned(loanId: String) {
        val isbn = _uiState.value.volume?.volume?.isbn ?: return
        viewModelScope.launch {
            repository.markAsReturned(loanId)
            loadVolume(isbn)
        }
    }
}

 `  

## VolumeDetailScreen.kt (Features)

` kotlin
package com.fasby.bibliomobil.volume_detail.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.fasby.bibliomobil.volume_detail.presentation.VolumeDetailViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolumeDetailScreen(
    isbn: String,
    viewModel: VolumeDetailViewModel,
    onBack: () -> Unit,
    onEditClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    LaunchedEffect(isbn) {
        viewModel.loadVolume(isbn)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Tomo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { onEditClick(isbn) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            uiState.volume?.let { detailed ->
                val volume = detailed.volume
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = volume.coverPath,
                        contentDescription = null,
                        modifier = Modifier
                            .height(250.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = volume.title, style = MaterialTheme.typography.headlineMedium)
                    
                    if (detailed.authors.isNotEmpty()) {
                        Text(
                            text = "Por ${detailed.authors.joinToString { it.name }}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Valoración interactiva
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        (1..5).forEach { index ->
                            IconButton(onClick = { viewModel.updateRating(index) }) {
                                Icon(
                                    imageVector = if (index <= volume.rating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = if (index <= volume.rating) Color(0xFFFFD700) else MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }

                    Text(text = "Año: ${volume.publishedYear}", style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.toggleReadStatus() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (volume.isRead) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(
                            imageVector = if (volume.isRead) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = if (volume.isRead) "Leído" else "Marcar como leído")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selector de Colección
                    var expanded by remember { mutableStateOf(false) }
                    val currentCollection = uiState.collections.find { it.id == volume.collectionId }
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = currentCollection?.name ?: "Sin colección",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Colección") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sin colección") },
                                onClick = {
                                    viewModel.updateCollection(null)
                                    expanded = false
                                }
                            )
                            uiState.collections.forEach { collection ->
                                DropdownMenuItem(
                                    text = { Text(collection.name) },
                                    onClick = {
                                        viewModel.updateCollection(collection.id)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // SECCIÓN DE PRÉSTAMOS
                    val activeLoan = detailed.loanHistory.find { it.returnDate == null }
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (activeLoan != null) MaterialTheme.colorScheme.errorContainer 
                                             else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Estado del Libro", style = MaterialTheme.typography.titleMedium)
                            if (activeLoan != null) {
                                Text("Prestado a: ${activeLoan.lentTo}", style = MaterialTheme.typography.bodyLarge)
                                Text("Desde: ${dateFormat.format(Date(activeLoan.loanDate))}", style = MaterialTheme.typography.bodySmall)
                                Button(
                                    onClick = { viewModel.markAsReturned(activeLoan.id) },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Marcar como devuelto")
                                }
                            } else {
                                Text("Disponible en estantería")
                                var showLoanDialog by remember { mutableStateOf(false) }
                                if (showLoanDialog) {
                                    var lentToName by remember { mutableStateOf("") }
                                    AlertDialog(
                                        onDismissRequest = { showLoanDialog = false },
                                        title = { Text("Registrar Préstamo") },
                                        text = {
                                            OutlinedTextField(
                                                value = lentToName,
                                                onValueChange = { lentToName = it },
                                                label = { Text("Nombre de la persona") }
                                            )
                                        },
                                        confirmButton = {
                                            TextButton(onClick = {
                                                viewModel.registerLoan(lentToName)
                                                showLoanDialog = false
                                            }) { Text("Prestar") }
                                        }
                                    )
                                }
                                Button(
                                    onClick = { showLoanDialog = true },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Registrar Préstamo")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // RESEÑA PERSONAL
                    Text(
                        text = "Mi Reseña",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    var reviewText by remember(volume.personalReview) { mutableStateOf(volume.personalReview) }
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Escribe tu opinión personal...") },
                        trailingIcon = {
                            IconButton(onClick = { viewModel.updateReview(reviewText) }) {
                                Icon(Icons.Default.Save, contentDescription = "Guardar reseña")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Sinopsis Oficial",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = volume.synopsis,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // HISTÓRICO DE PRÉSTAMOS
                    if (detailed.loanHistory.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Historial de Préstamos",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        detailed.loanHistory.forEach { loan ->
                            ListItem(
                                headlineContent = { Text(loan.lentTo) },
                                supportingContent = { 
                                    val returnTxt = if (loan.returnDate != null) "Devuelto el ${dateFormat.format(Date(loan.returnDate))}" else "Aún prestado"
                                    Text("Prestado: ${dateFormat.format(Date(loan.loanDate))} - $returnTxt") 
                                },
                                leadingContent = { Icon(Icons.Default.History, contentDescription = null) }
                            )
                        }
                    }
                }
            } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No se encontró el tomo.")
            }
        }
    }
}

 `  

## SettingsViewModel.kt (Features)

` kotlin
package com.fasby.bibliomobil.settings.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasby.bibliomobil.domain.repository.VolumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val showRestartWarning: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: VolumeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    fun clearDatabase() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.clearDatabase()
            _uiState.update { it.copy(isLoading = false, message = "Base de datos eliminada.") }
        }
    }

    fun createBackup(onBackupReady: (Uri) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.createBackup { uri ->
                _uiState.update { it.copy(isLoading = false, message = "Backup creado.") }
                onBackupReady(uri)
            }
        }
    }

    fun restoreBackup(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.restoreBackup(uri)
            if (success) {
                _uiState.update { it.copy(isLoading = false, showRestartWarning = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, message = "Error al restaurar.") }
            }
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(message = null) }
    }
}

 `  

## SettingsScreen.kt (Features)

` kotlin
package com.fasby.bibliomobil.settings.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fasby.bibliomobil.settings.presentation.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Launcher para elegir archivo de backup
    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.restoreBackup(it) }
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissMessage()
        }
    }

    if (uiState.showRestartWarning) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Restauración Completada") },
            text = { Text("Para aplicar los cambios de la base de datos, la aplicación debe reiniciarse.") },
            confirmButton = {
                Button(onClick = { 
                    // Una forma sencilla de forzar el reinicio o cierre
                    android.os.Process.killProcess(android.os.Process.myPid())
                }) {
                    Text("Reiniciar Ahora")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Configuración y Backup") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Gestión de Datos", style = MaterialTheme.typography.titleLarge)
            
            // Backup Local / Compartir
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Copia de Seguridad", style = MaterialTheme.typography.titleMedium)
                    Text("Crea un archivo con toda tu biblioteca. Podrás guardarlo en tu dispositivo o subirlo a Google Drive.", 
                        style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { 
                            viewModel.createBackup { uri ->
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/octet-stream"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(intent, "Guardar Backup en..."))
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Backup, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Crear y Compartir Backup")
                    }
                }
            }

            // Restaurar
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Restaurar Datos", style = MaterialTheme.typography.titleMedium)
                    Text("Selecciona un archivo de backup (.db) anterior para recuperar tu biblioteca.", 
                        style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { restoreLauncher.launch(arrayOf("application/octet-stream", "*/*")) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Seleccionar Archivo de Backup")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Peligro: Borrar todo
            Divider()
            Text("Zona de Peligro", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
            Button(
                onClick = { viewModel.clearDatabase() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Eliminar Toda la Biblioteca")
            }
        }
    }
}

 `  
