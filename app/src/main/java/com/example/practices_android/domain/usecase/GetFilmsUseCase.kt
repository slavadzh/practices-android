package com.example.practices_android.domain.usecase

import com.example.practices_android.domain.model.FilmFilters
import com.example.practices_android.domain.model.FilmShort
import com.example.practices_android.domain.repository.FilmsRepository
import kotlinx.coroutines.flow.Flow

class GetFilmsUseCase(private val repository: FilmsRepository) {
    operator fun invoke(filters: FilmFilters, forceRefresh: Boolean = false): Flow<List<FilmShort>> =
        repository.getFilms(filters, forceRefresh)
}
