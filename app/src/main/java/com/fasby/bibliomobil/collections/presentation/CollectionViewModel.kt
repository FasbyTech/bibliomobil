package com.fasby.bibliomobil.collections.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasby.bibliomobil.data.local.entity.CollectionEntity
import com.fasby.bibliomobil.domain.repository.VolumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CollectionUiState(
    val collections: List<CollectionEntity> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val repository: VolumeRepository
) : ViewModel() {

    val uiState: StateFlow<CollectionUiState> = repository.getAllCollections()
        .map { CollectionUiState(collections = it, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CollectionUiState(isLoading = true))

    fun addCollection(name: String, publisher: String) {
        viewModelScope.launch {
            val collection = CollectionEntity(
                id = UUID.randomUUID().toString(),
                name = name,
                publisher = publisher
            )
            repository.saveCollection(collection)
        }
    }
}
