package com.example.practices_android.ui.films.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.practices_android.data.FilmsRepository
import com.example.practices_android.data.ServiceLocator
import com.example.practices_android.data.model.Film
import com.example.practices_android.ui.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface FilmDetailsUiState {
    data object Loading : FilmDetailsUiState
    data class Content(val film: Film) : FilmDetailsUiState
    data class Error(val message: String) : FilmDetailsUiState
}

class FilmDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: FilmsRepository,
) : ViewModel() {

    private val filmId: String = checkNotNull(savedStateHandle[Routes.FILM_ID_ARG])

    private val _state = MutableStateFlow<FilmDetailsUiState>(FilmDetailsUiState.Loading)
    val state: StateFlow<FilmDetailsUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.value = FilmDetailsUiState.Loading
        viewModelScope.launch {
            _state.value = runCatching { repository.getFilm(filmId) }
                .fold(
                    onSuccess = { FilmDetailsUiState.Content(it) },
                    onFailure = { FilmDetailsUiState.Error(it.message ?: "Unknown error") },
                )
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                FilmDetailsViewModel(createSavedStateHandle(), ServiceLocator.filmsRepository)
            }
        }
    }
}
