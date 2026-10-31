package com.example.practices_android.domain.repository

import com.example.practices_android.domain.model.FilmFilters
import kotlinx.coroutines.flow.Flow

interface FiltersRepository {
    val filters: Flow<FilmFilters>

    suspend fun save(filters: FilmFilters)
}
