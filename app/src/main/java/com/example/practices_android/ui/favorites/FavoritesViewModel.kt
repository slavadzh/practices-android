package com.example.practices_android.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practices_android.domain.model.FilmShort
import com.example.practices_android.domain.usecase.ObserveFavoritesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FavoritesViewModel(
    observeFavorites: ObserveFavoritesUseCase,
) : ViewModel() {

    val favorites: StateFlow<List<FilmShort>?> = observeFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
