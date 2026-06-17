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
