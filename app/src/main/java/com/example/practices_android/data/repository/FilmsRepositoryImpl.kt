package com.example.practices_android.data.repository

import com.example.practices_android.data.mapper.toAppError
import com.example.practices_android.data.mapper.toFilm
import com.example.practices_android.data.mapper.toFilmShort
import com.example.practices_android.data.remote.GhibliApi
import com.example.practices_android.domain.model.Film
import com.example.practices_android.domain.model.FilmShort
import com.example.practices_android.domain.repository.FilmsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.concurrent.ConcurrentHashMap

class FilmsRepositoryImpl(
    private val api: GhibliApi,
    private val ioDispatcher: CoroutineDispatcher,
) : FilmsRepository {

    @Volatile
    private var filmsCache: List<FilmShort>? = null
    private val detailsCache = ConcurrentHashMap<String, Film>()

    override fun getFilms(forceRefresh: Boolean): Flow<List<FilmShort>> = flow {
        val cached = filmsCache
        if (cached != null && !forceRefresh) {
            emit(cached)
            return@flow
        }
        val films = api.getFilms(LIST_FIELDS, LIST_LIMIT).map { it.toFilmShort() }
        filmsCache = films
        emit(films)
    }
        .catch { throw it.toAppError() }
        .flowOn(ioDispatcher)

    override fun getFilm(id: String, forceRefresh: Boolean): Flow<Film> = flow {
        val cached = detailsCache[id]
        if (cached != null && !forceRefresh) {
            emit(cached)
            return@flow
        }
        val film = api.getFilm(id).toFilm()
        detailsCache[id] = film
        emit(film)
    }
        .catch { throw it.toAppError() }
        .flowOn(ioDispatcher)

    private companion object {
        const val LIST_FIELDS = "id,title,image,director,release_date,rt_score"
        const val LIST_LIMIT = 50
    }
}
