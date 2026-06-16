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
