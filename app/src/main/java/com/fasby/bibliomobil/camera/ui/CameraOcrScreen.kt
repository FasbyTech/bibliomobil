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
