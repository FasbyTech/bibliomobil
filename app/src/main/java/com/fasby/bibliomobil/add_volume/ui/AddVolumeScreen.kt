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
