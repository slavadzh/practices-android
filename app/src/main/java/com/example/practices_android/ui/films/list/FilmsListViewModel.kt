package com.example.practices_android.ui.films.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.practices_android.data.FilmsRepository
import com.example.practices_android.data.ServiceLocator
import com.example.practices_android.data.model.FilmShort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface FilmsListUiState {
    data object Loading : FilmsListUiState
    data class Content(val films: List<FilmShort>) : FilmsListUiState
    data class Error(val message: String) : FilmsListUiState
}

class FilmsListViewModel(
    private val repository: FilmsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<FilmsListUiState>(FilmsListUiState.Loading)
    val state: StateFlow<FilmsListUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.value = FilmsListUiState.Loading
        viewModelScope.launch {
            _state.value = runCatching { repository.getFilms() }
                .fold(
                    onSuccess = { FilmsListUiState.Content(it) },
                    onFailure = { FilmsListUiState.Error(it.message ?: "Unknown error") },
                )
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { FilmsListViewModel(ServiceLocator.filmsRepository) }
        }
    }
}
