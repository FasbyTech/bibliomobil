package com.fasby.bibliomobil.camera.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasby.bibliomobil.data.local.model.DetailedVolume
import com.fasby.bibliomobil.domain.usecase.SearchVolumesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import kotlinx.coroutines.delay

/**
 * Representa los diferentes estados de la pantalla de escaneo por cámara.
 */
data class CameraOcrUiState(
    val rawOcrText: String = "",
    val matchedVolumes: List<Pair<DetailedVolume, Double>> = emptyList(),
    val isProcessing: Boolean = false,
    val detectedIsbn: String? = null,
    val lastNavigatedIsbn: String? = null,
    val confirmingIsbn: String? = null,
    val confirmationProgress: Float = 0f
)

@HiltViewModel
class CameraOcrViewModel @Inject constructor(
    private val searchVolumesUseCase: SearchVolumesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraOcrUiState())
    val uiState: StateFlow<CameraOcrUiState> = _uiState.asStateFlow()

    private var analysisJob: Job? = null
    private var confirmationJob: Job? = null

    /**
     * Callback de entrada secundario que invocará el OcrImageAnalyzer en cada frame exitoso.
     */
    fun onTextDetectedFromCamera(rawText: String) {
        val sanitizedForIsbn = rawText.uppercase().replace(Regex("[^0-9X]"), "")
        
        val isbnRegex = Regex("(?:\\d{13}|\\d{9}[0-9X])")
        val match = isbnRegex.find(sanitizedForIsbn)
        
        if (match != null) {
            val isbn = match.value
            
            // Si es un ISBN nuevo y no es el último que navegamos
            if (isbn != _uiState.value.lastNavigatedIsbn) {
                if (isbn != _uiState.value.confirmingIsbn) {
                    startIsbnConfirmation(isbn)
                }
                return 
            }
        } else {
            // Si perdemos el foco del ISBN, cancelamos la confirmación pendiente
            if (_uiState.value.confirmingIsbn != null) {
                confirmationJob?.cancel()
                _uiState.update { it.copy(confirmingIsbn = null, confirmationProgress = 0f) }
            }
        }

        // Si el usuario deja de apuntar al libro, permitimos resetear el "lastNavigatedIsbn"
        if (sanitizedForIsbn.isBlank() || (match == null && sanitizedForIsbn.length > 5)) {
             _uiState.update { it.copy(lastNavigatedIsbn = null) }
        }

        // 2. Flujo de Búsqueda Difusa
        val textForFuzzy = rawText.trim()
        if (textForFuzzy == _uiState.value.rawOcrText || textForFuzzy.isBlank()) return

        _uiState.update { it.copy(rawOcrText = textForFuzzy, isProcessing = true) }

        analysisJob?.cancel()
        analysisJob = viewModelScope.launch {
            searchVolumesUseCase.executeOcrFuzzyMatch(ocrRawText = textForFuzzy)
                .collect { results ->
                    _uiState.update { 
                        it.copy(
                            matchedVolumes = results,
                            isProcessing = false
                        )
                    }
                }
        }
    }

    private fun startIsbnConfirmation(isbn: String) {
        confirmationJob?.cancel()
        confirmationJob = viewModelScope.launch {
            _uiState.update { it.copy(confirmingIsbn = isbn, confirmationProgress = 0f) }
            
            val totalTimeMs = 3000L
            val stepMs = 100L
            val totalSteps = (totalTimeMs / stepMs).toInt()
            
            for (step in 1..totalSteps) {
                delay(stepMs)
                _uiState.update { it.copy(confirmationProgress = step.toFloat() / totalSteps) }
            }
            
            // Confirmado tras 3 segundos
            _uiState.update { 
                it.copy(
                    detectedIsbn = isbn, 
                    lastNavigatedIsbn = isbn,
                    confirmingIsbn = null,
                    confirmationProgress = 0f
                )
            }
        }
    }

    fun onNavigatedToAddVolume() {
        _uiState.update { it.copy(detectedIsbn = null, confirmingIsbn = null, confirmationProgress = 0f) }
        confirmationJob?.cancel()
    }
}
