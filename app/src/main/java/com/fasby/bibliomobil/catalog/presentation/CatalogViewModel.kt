package com.fasby.bibliomobil.catalog.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasby.bibliomobil.data.local.model.DetailedVolume
import com.fasby.bibliomobil.domain.repository.VolumeRepository
import com.fasby.bibliomobil.domain.voice.VoiceRecognizerManager
import com.fasby.bibliomobil.domain.voice.VoiceRecognizerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Representa el estado de la interfaz de usuario del catálogo general.
 */
data class CatalogUiState(
    val searchQuery: String = "",
    val volumes: List<DetailedVolume> = emptyList(),
    val voiceState: VoiceRecognizerState = VoiceRecognizerState.Idle,
    val isLoading: Boolean = false
)

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val repository: VolumeRepository,
    private val voiceRecognizerManager: VoiceRecognizerManager
) : ViewModel() {

    // Flujo mutable interno para controlar la cadena de búsqueda (texto o voz)
    private val _searchQuery = MutableStateFlow("")
    private val _collectionId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CatalogUiState> = combine(
        _searchQuery,
        _collectionId,
        voiceRecognizerManager.state
    ) { query, collectionId, voiceState ->
        Triple(query, collectionId, voiceState)
    }.flatMapLatest { (currentQuery, collectionId, voiceState) ->
        val flow = if (collectionId != null) {
            repository.getVolumesByCollection(collectionId)
        } else {
            repository.searchVolumes(currentQuery)
        }
        
        flow.map { volumesList ->
            CatalogUiState(
                searchQuery = currentQuery,
                volumes = volumesList,
                voiceState = voiceState,
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CatalogUiState(isLoading = true)
    )

    init {
        // Escuchamos el motor de voz para sincronizar el cuadro de texto si se dicta algo
        viewModelScope.launch {
            voiceRecognizerManager.state.collect { voiceState ->
                if (voiceState is VoiceRecognizerState.Success) {
                    _searchQuery.value = voiceState.text
                }
            }
        }
    }

    /**
     * Actualiza el query cuando el usuario escribe manualmente en el teclado.
     */
    fun onSearchQueryChanged(newQuery: String) {
        // Si el motor de voz estaba en modo éxito o error, lo reseteamos al escribir
        if (voiceRecognizerManager.state.value !is VoiceRecognizerState.Idle) {
            voiceRecognizerManager.stopListening()
        }
        _collectionId.value = null // Reset filter when searching
        _searchQuery.value = newQuery
    }

    fun filterByCollection(collectionId: String?) {
        _collectionId.value = collectionId
    }

    fun exportToCsv(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val csv = repository.generateCsvReport()
            onResult(csv)
        }
    }

    // ========================================================================
    // Gestión del ciclo de vida del reconocimiento de voz
    // ========================================================================

    fun toggleVoiceSearch() {
        when (voiceRecognizerManager.state.value) {
            is VoiceRecognizerState.Listening -> voiceRecognizerManager.stopListening()
            else -> voiceRecognizerManager.startListening()
        }
    }

    override fun onCleared() {
        super.onCleared()
        // CRÍTICO TFM: Liberamos el micrófono del sistema operativo para evitar fugas
        voiceRecognizerManager.destroy()
    }
}
