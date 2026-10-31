package com.example.practices_android.ui.films.filters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practices_android.domain.model.FilmFilters
import com.example.practices_android.domain.usecase.GetDirectorsUseCase
import com.example.practices_android.domain.usecase.ObserveFiltersUseCase
import com.example.practices_android.domain.usecase.SaveFiltersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FiltersUiState(
    val filters: FilmFilters = FilmFilters(),
    val directors: List<String> = emptyList(),
    val isLoaded: Boolean = false,
)

class FiltersViewModel(
    observeFilters: ObserveFiltersUseCase,
    getDirectors: GetDirectorsUseCase,
    private val saveFilters: SaveFiltersUseCase,
    private val badgeCache: FiltersBadgeCache,
) : ViewModel() {

    private val _state = MutableStateFlow(FiltersUiState())
    val state: StateFlow<FiltersUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val saved = observeFilters().first()
            _state.update { it.copy(filters = saved, isLoaded = true) }
        }
        getDirectors()
            .onEach { directors -> _state.update { it.copy(directors = directors) } }
            .catch { }
            .launchIn(viewModelScope)
    }

    fun onTitleChange(title: String) = updateFilters { it.copy(title = title) }

    fun onDirectorSelect(director: String?) = updateFilters { it.copy(director = director) }

    fun onMinRatingChange(rating: Int) = updateFilters { it.copy(minRating = rating) }

    fun reset() = updateFilters { FilmFilters() }

    fun apply(onDone: () -> Unit) {
        val filters = _state.value.filters.let { it.copy(title = it.title.trim()) }
        viewModelScope.launch {
            saveFilters(filters)
            badgeCache.update(filters)
            onDone()
        }
    }

    private fun updateFilters(transform: (FilmFilters) -> FilmFilters) {
        _state.update { it.copy(filters = transform(it.filters)) }
    }
}
