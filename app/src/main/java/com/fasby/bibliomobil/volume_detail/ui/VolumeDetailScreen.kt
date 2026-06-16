package com.fasby.bibliomobil.volume_detail.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.fasby.bibliomobil.volume_detail.presentation.VolumeDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolumeDetailScreen(
    isbn: String,
    viewModel: VolumeDetailViewModel,
    onBack: () -> Unit,
    onEditClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(isbn) {
        viewModel.loadVolume(isbn)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Tomo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
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

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⭐ ".repeat(volume.rating.coerceIn(0, 5)))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = "Año: ${volume.publishedYear}", style = MaterialTheme.typography.bodyMedium)
                    }

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

                    Text(
                        text = "Sinopsis",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = volume.synopsis,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No se encontró el tomo.")
            }
        }
    }
}
