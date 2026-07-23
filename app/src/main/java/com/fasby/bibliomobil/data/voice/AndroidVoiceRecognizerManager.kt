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

    override fun reset() {
        _state.value = VoiceRecognizerState.Idle
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
