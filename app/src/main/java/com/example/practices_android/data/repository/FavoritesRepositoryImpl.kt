package com.example.practices_android.data.repository

import com.example.practices_android.data.local.db.FavoritesDao
import com.example.practices_android.data.mapper.toEntity
import com.example.practices_android.data.mapper.toFilmShort
import com.example.practices_android.domain.model.Film
import com.example.practices_android.domain.model.FilmShort
import com.example.practices_android.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val dao: FavoritesDao,
) : FavoritesRepository {

    override fun observeFavorites(): Flow<List<FilmShort>> =
        dao.observeAll().map { list -> list.map { it.toFilmShort() } }

    override fun observeIsFavorite(id: String): Flow<Boolean> = dao.observeIsFavorite(id)

    override suspend fun add(film: Film) = dao.upsert(film.toEntity(addedAt = System.currentTimeMillis()))

    override suspend fun remove(id: String) = dao.deleteById(id)
}
