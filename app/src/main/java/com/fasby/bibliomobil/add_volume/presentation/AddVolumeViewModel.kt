package com.fasby.bibliomobil.add_volume.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasby.bibliomobil.data.local.entity.AuthorEntity
import com.fasby.bibliomobil.data.local.entity.CollectionEntity
import com.fasby.bibliomobil.data.local.entity.VolumeEntity
import com.fasby.bibliomobil.domain.ai.AiRepository
import com.fasby.bibliomobil.domain.repository.VolumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddVolumeUiState(
    val isbn: String = "",
    val title: String = "",
    val authors: String = "",
    val publishedYear: String = "",
    val synopsis: String = "",
    val coverPath: String = "",
    val collectionId: String? = null,
    val collections: List<CollectionEntity> = emptyList(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val isEditMode: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddVolumeViewModel @Inject constructor(
    private val repository: VolumeRepository,
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddVolumeUiState())
    val uiState: StateFlow<AddVolumeUiState> = combine(
        _uiState,
        repository.getAllCollections()
    ) { state, collections ->
        state.copy(collections = collections)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AddVolumeUiState())

    fun onIsbnChanged(isbn: String) {
        _uiState.update { it.copy(isbn = isbn) }
    }

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onAuthorsChanged(authors: String) {
        _uiState.update { it.copy(authors = authors) }
    }

    fun onPublishedYearChanged(year: String) {
        _uiState.update { it.copy(publishedYear = year) }
    }

    fun onSynopsisChanged(synopsis: String) {
        _uiState.update { it.copy(synopsis = synopsis) }
    }

    fun onCoverPathChanged(path: String) {
        _uiState.update { it.copy(coverPath = path) }
    }

    fun generateAiSummary() {
        val title = _uiState.value.title
        val currentSynopsis = _uiState.value.synopsis
        if (title.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val summary = aiRepository.generateSummary(title, currentSynopsis)
            if (summary != null) {
                _uiState.update { it.copy(synopsis = summary, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Error al conectar con Gemini.") }
            }
        }
    }

    fun onCollectionChanged(id: String?) {
        _uiState.update { it.copy(collectionId = id) }
    }

    fun setInitialIsbn(isbn: String) {
        if (isbn.isNotBlank() && _uiState.value.isbn != isbn) {
            viewModelScope.launch {
                val existingVolume = repository.getVolumeByIsbn(isbn)
                if (existingVolume != null) {
                    _uiState.update { 
                        it.copy(
                            isbn = existingVolume.volume.isbn,
                            title = existingVolume.volume.title,
                            authors = existingVolume.authors.joinToString(", ") { it.name },
                            publishedYear = existingVolume.volume.publishedYear.toString(),
                            synopsis = existingVolume.volume.synopsis,
                            coverPath = existingVolume.volume.coverPath,
                            collectionId = existingVolume.volume.collectionId,
                            isEditMode = true
                        )
                    }
                } else {
                    _uiState.update { it.copy(isbn = isbn, isEditMode = false) }
                    autocomplete()
                }
            }
        }
    }

    fun autocomplete() {
        val rawIsbn = _uiState.value.isbn
        val isbn = rawIsbn.uppercase().replace(Regex("[^0-9X]"), "")
        if (isbn.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isbn = isbn, isLoading = true, error = null) }
            val result = repository.searchRemoteBook("isbn:$isbn")
            if (result != null) {
                val (volume, authors) = result
                _uiState.update { 
                    it.copy(
                        title = volume.title,
                        authors = authors.joinToString(", ") { it.name },
                        publishedYear = volume.publishedYear.toString(),
                        synopsis = volume.synopsis,
                        coverPath = volume.coverPath,
                        isLoading = false
                    )
                }
            } else {
                // Si falla por ISBN, intentamos una búsqueda general por el código (a veces Google no lo tiene marcado como ISBN)
                val generalResult = repository.searchRemoteBook(isbn)
                if (generalResult != null) {
                    val (volume, authors) = generalResult
                    _uiState.update { 
                        it.copy(
                            title = volume.title,
                            authors = authors.joinToString(", ") { it.name },
                            publishedYear = volume.publishedYear.toString(),
                            synopsis = volume.synopsis,
                            coverPath = volume.coverPath,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "No se encontraron datos para este ISBN ($isbn).") }
                }
            }
        }
    }

    fun saveVolume() {
        viewModelScope.launch {
            val state = _uiState.value
            
            // Si es modo edición, podríamos necesitar recuperar el rating y estado de lectura original
            val existing = if (state.isEditMode) repository.getVolumeByIsbn(state.isbn) else null
            
            val volume = VolumeEntity(
                isbn = state.isbn,
                collectionId = state.collectionId,
                title = state.title,
                number = 0,
                publishedYear = state.publishedYear.toIntOrNull() ?: 0,
                synopsis = state.synopsis,
                coverPath = state.coverPath,
                rating = existing?.volume?.rating ?: 0,
                isRead = existing?.volume?.isRead ?: false,
                createdAt = existing?.volume?.createdAt ?: System.currentTimeMillis()
            )
            val authors = state.authors.split(",").map { 
                AuthorEntity(id = java.util.UUID.randomUUID().toString(), name = it.trim(), role = "Autor") 
            }

            repository.saveCompleteVolume(volume, authors)
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
