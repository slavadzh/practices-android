package com.example.practices_android.domain.usecase

import com.example.practices_android.domain.model.Film
import com.example.practices_android.domain.model.FilmShort
import com.example.practices_android.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow

class ObserveFavoritesUseCase(private val repository: FavoritesRepository) {
    operator fun invoke(): Flow<List<FilmShort>> = repository.observeFavorites()
}

class ObserveIsFavoriteUseCase(private val repository: FavoritesRepository) {
    operator fun invoke(id: String): Flow<Boolean> = repository.observeIsFavorite(id)
}

class ToggleFavoriteUseCase(private val repository: FavoritesRepository) {
    suspend operator fun invoke(film: Film, isFavorite: Boolean) {
        if (isFavorite) repository.remove(film.id) else repository.add(film)
    }
}
