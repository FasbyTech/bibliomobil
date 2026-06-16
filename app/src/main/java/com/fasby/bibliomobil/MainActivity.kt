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
