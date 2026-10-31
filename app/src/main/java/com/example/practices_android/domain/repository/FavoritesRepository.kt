package com.example.practices_android.domain.repository

import com.example.practices_android.domain.model.Film
import com.example.practices_android.domain.model.FilmShort
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeFavorites(): Flow<List<FilmShort>>

    fun observeIsFavorite(id: String): Flow<Boolean>

    suspend fun add(film: Film)

    suspend fun remove(id: String)
}
