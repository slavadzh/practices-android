package com.example.practices_android.domain.usecase

import com.example.practices_android.domain.model.Film
import com.example.practices_android.domain.repository.FilmsRepository
import kotlinx.coroutines.flow.Flow

class GetFilmDetailsUseCase(private val repository: FilmsRepository) {
    operator fun invoke(id: String, forceRefresh: Boolean = false): Flow<Film> =
        repository.getFilm(id, forceRefresh)
}
