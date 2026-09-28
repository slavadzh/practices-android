package com.example.practices_android.data

import com.example.practices_android.data.model.Film
import com.example.practices_android.data.model.FilmShort
import com.example.practices_android.data.model.toShort
import kotlinx.coroutines.delay

interface FilmsRepository {
    suspend fun getFilms(): List<FilmShort>

    suspend fun getFilm(id: String): Film
}

class MockFilmsRepository : FilmsRepository {

    override suspend fun getFilms(): List<FilmShort> {
        delay(FAKE_NETWORK_DELAY_MS)
        return mockFilms.map { it.toShort() }
    }

    override suspend fun getFilm(id: String): Film {
        delay(FAKE_NETWORK_DELAY_MS)
        return mockFilms.firstOrNull { it.id == id }
            ?: throw NoSuchElementException("Film $id not found")
    }

    private companion object {
        const val FAKE_NETWORK_DELAY_MS = 500L
    }
}

object ServiceLocator {
    val filmsRepository: FilmsRepository by lazy { MockFilmsRepository() }
}
