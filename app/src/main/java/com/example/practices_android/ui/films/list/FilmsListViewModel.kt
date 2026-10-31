package com.example.practices_android.ui.films.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practices_android.domain.model.AppError
import com.example.practices_android.domain.model.FilmFilters
import com.example.practices_android.domain.model.FilmShort
import com.example.practices_android.domain.usecase.GetFilmsUseCase
import com.example.practices_android.domain.usecase.ObserveFiltersUseCase
import com.example.practices_android.ui.films.filters.FiltersBadgeCache
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
    observeFilters: ObserveFiltersUseCase,
    badgeCache: FiltersBadgeCache,
) : ViewModel() {

    private val _state = MutableStateFlow<FilmsListUiState>(FilmsListUiState.Loading)
    val state: StateFlow<FilmsListUiState> = _state.asStateFlow()

    val showFiltersBadge: StateFlow<Boolean> = badgeCache.showBadge

    private var filters = FilmFilters()
    private var loadJob: Job? = null

    init {
        observeFilters()
            .onEach {
                filters = it
                badgeCache.update(it)
                load(forceRefresh = false)
            }
            .launchIn(viewModelScope)
    }

    fun refresh() = load(forceRefresh = true)

    private fun load(forceRefresh: Boolean) {
        loadJob?.cancel()
        loadJob = getFilms(filters, forceRefresh)
            .onStart { _state.value = FilmsListUiState.Loading }
            .onEach { _state.value = FilmsListUiState.Content(it) }
            .catch { _state.value = FilmsListUiState.Error(it as? AppError ?: AppError.Unknown(it)) }
            .launchIn(viewModelScope)
    }
}
