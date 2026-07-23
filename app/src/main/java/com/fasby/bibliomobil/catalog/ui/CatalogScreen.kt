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
import androidx.compose.material.icons.filled.Clear
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
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Borrar búsqueda")
                        }
                    }
                },
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
