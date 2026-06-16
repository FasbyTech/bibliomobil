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
