package com.fasby.bibliomobil.volume_detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasby.bibliomobil.data.local.model.DetailedVolume
import com.fasby.bibliomobil.data.local.entity.CollectionEntity
import com.fasby.bibliomobil.domain.repository.VolumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VolumeDetailUiState(
    val volume: DetailedVolume? = null,
    val collections: List<CollectionEntity> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class VolumeDetailViewModel @Inject constructor(
    private val repository: VolumeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VolumeDetailUiState())
    val uiState: StateFlow<VolumeDetailUiState> = combine(
        _uiState,
        repository.getAllCollections()
    ) { state, collections ->
        state.copy(collections = collections)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VolumeDetailUiState())

    fun loadVolume(isbn: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val detailedVolume = repository.getVolumeByIsbn(isbn)
            _uiState.update { it.copy(volume = detailedVolume, isLoading = false) }
        }
    }

    fun updateCollection(collectionId: String?) {
        val currentVolume = _uiState.value.volume?.volume ?: return
        viewModelScope.launch {
            repository.updateVolume(currentVolume.copy(collectionId = collectionId))
            loadVolume(currentVolume.isbn)
        }
    }

    fun toggleReadStatus() {
        val currentVolume = _uiState.value.volume?.volume ?: return
        viewModelScope.launch {
            val updatedVolume = currentVolume.copy(isRead = !currentVolume.isRead)
            repository.updateVolume(updatedVolume)
            loadVolume(updatedVolume.isbn)
        }
    }

    fun updateRating(rating: Int) {
        val currentVolume = _uiState.value.volume?.volume ?: return
        viewModelScope.launch {
            repository.updateVolume(currentVolume.copy(rating = rating))
            loadVolume(currentVolume.isbn)
        }
    }

    fun updateReview(review: String) {
        val currentVolume = _uiState.value.volume?.volume ?: return
        viewModelScope.launch {
            repository.updateVolume(currentVolume.copy(personalReview = review))
            loadVolume(currentVolume.isbn)
        }
    }

    fun registerLoan(lentTo: String) {
        val isbn = _uiState.value.volume?.volume?.isbn ?: return
        viewModelScope.launch {
            repository.registerLoan(isbn, lentTo)
            loadVolume(isbn)
        }
    }

    fun markAsReturned(loanId: String) {
        val isbn = _uiState.value.volume?.volume?.isbn ?: return
        viewModelScope.launch {
            repository.markAsReturned(loanId)
            loadVolume(isbn)
        }
    }
}
