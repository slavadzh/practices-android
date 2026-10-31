package com.example.practices_android.ui.films.filters

import com.example.practices_android.domain.model.FilmFilters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FiltersBadgeCache {

    private val _showBadge = MutableStateFlow(false)
    val showBadge: StateFlow<Boolean> = _showBadge.asStateFlow()

    fun update(filters: FilmFilters) {
        _showBadge.value = !filters.isDefault
    }
}
