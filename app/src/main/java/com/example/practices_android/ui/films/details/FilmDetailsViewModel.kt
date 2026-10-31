package com.example.practices_android.ui.films.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practices_android.domain.model.AppError
import com.example.practices_android.domain.model.Film
import com.example.practices_android.domain.usecase.GetFilmDetailsUseCase
import com.example.practices_android.domain.usecase.ObserveIsFavoriteUseCase
import com.example.practices_android.domain.usecase.ToggleFavoriteUseCase
import com.example.practices_android.ui.navigation.Routes
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface FilmDetailsUiState {
    data object Loading : FilmDetailsUiState
    data class Content(val film: Film) : FilmDetailsUiState
    data class Error(val error: AppError) : FilmDetailsUiState
}

class FilmDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val getFilmDetails: GetFilmDetailsUseCase,
    observeIsFavorite: ObserveIsFavoriteUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : ViewModel() {

    private val filmId: String = checkNotNull(savedStateHandle[Routes.FILM_ID_ARG])

    private val _state = MutableStateFlow<FilmDetailsUiState>(FilmDetailsUiState.Loading)
    val state: StateFlow<FilmDetailsUiState> = _state.asStateFlow()

    val isFavorite: StateFlow<Boolean> = observeIsFavorite(filmId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private var loadJob: Job? = null

    init {
        load()
    }

    fun load() {
        loadJob?.cancel()
        loadJob = getFilmDetails(filmId)
            .onStart { _state.value = FilmDetailsUiState.Loading }
            .onEach { _state.value = FilmDetailsUiState.Content(it) }
            .catch { _state.value = FilmDetailsUiState.Error(it as? AppError ?: AppError.Unknown(it)) }
            .launchIn(viewModelScope)
    }

    fun onFavoriteClick() {
        val film = (_state.value as? FilmDetailsUiState.Content)?.film ?: return
        viewModelScope.launch { toggleFavorite(film, isFavorite.value) }
    }
}
