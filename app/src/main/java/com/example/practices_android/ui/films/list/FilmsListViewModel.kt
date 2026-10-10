package com.example.practices_android.ui.films.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.practices_android.App
import com.example.practices_android.domain.model.AppError
import com.example.practices_android.domain.model.FilmShort
import com.example.practices_android.domain.usecase.GetFilmsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

sealed interface FilmsListUiState {
    data object Loading : FilmsListUiState
    data class Content(val films: List<FilmShort>) : FilmsListUiState
    data class Error(val error: AppError) : FilmsListUiState
}

class FilmsListViewModel(
    private val getFilms: GetFilmsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<FilmsListUiState>(FilmsListUiState.Loading)
    val state: StateFlow<FilmsListUiState> = _state.asStateFlow()

    private var loadJob: Job? = null

    init {
        load(forceRefresh = false)
    }

    fun refresh() = load(forceRefresh = true)

    private fun load(forceRefresh: Boolean) {
        loadJob?.cancel()
        loadJob = getFilms(forceRefresh)
            .onStart { _state.value = FilmsListUiState.Loading }
            .onEach { _state.value = FilmsListUiState.Content(it) }
            .catch { _state.value = FilmsListUiState.Error(it as? AppError ?: AppError.Unknown(it)) }
            .launchIn(viewModelScope)
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val container = (this[APPLICATION_KEY] as App).container
                FilmsListViewModel(container.getFilmsUseCase)
            }
        }
    }
}
