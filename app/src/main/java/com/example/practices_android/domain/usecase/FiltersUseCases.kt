package com.example.practices_android.domain.usecase

import com.example.practices_android.domain.model.FilmFilters
import com.example.practices_android.domain.repository.FiltersRepository
import kotlinx.coroutines.flow.Flow

class ObserveFiltersUseCase(private val repository: FiltersRepository) {
    operator fun invoke(): Flow<FilmFilters> = repository.filters
}

class SaveFiltersUseCase(private val repository: FiltersRepository) {
    suspend operator fun invoke(filters: FilmFilters) = repository.save(filters)
}
