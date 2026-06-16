package com.fasby.bibliomobil.settings.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasby.bibliomobil.domain.repository.VolumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val showRestartWarning: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: VolumeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    fun clearDatabase() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.clearDatabase()
            _uiState.update { it.copy(isLoading = false, message = "Base de datos eliminada.") }
        }
    }

    fun createBackup(onBackupReady: (Uri) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.createBackup { uri ->
                _uiState.update { it.copy(isLoading = false, message = "Backup creado.") }
                onBackupReady(uri)
            }
        }
    }

    fun restoreBackup(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = repository.restoreBackup(uri)
            if (success) {
                _uiState.update { it.copy(isLoading = false, showRestartWarning = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, message = "Error al restaurar.") }
            }
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(message = null) }
    }
}
