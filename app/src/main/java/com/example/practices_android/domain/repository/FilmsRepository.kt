package com.example.practices_android.domain.repository

import com.example.practices_android.domain.model.Film
import com.example.practices_android.domain.model.FilmShort
import kotlinx.coroutines.flow.Flow

interface FilmsRepository {
    fun getFilms(forceRefresh: Boolean): Flow<List<FilmShort>>

    fun getFilm(id: String, forceRefresh: Boolean): Flow<Film>
}
