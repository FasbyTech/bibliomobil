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
