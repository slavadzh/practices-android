package com.example.practices_android.data.repository

import com.example.practices_android.data.local.db.FavoritesDao
import com.example.practices_android.data.mapper.toAppError
import com.example.practices_android.data.mapper.toFilm
import com.example.practices_android.data.mapper.toFilmShort
import com.example.practices_android.data.remote.GhibliApi
import com.example.practices_android.domain.model.AppError
import com.example.practices_android.domain.model.Film
import com.example.practices_android.domain.model.FilmFilters
import com.example.practices_android.domain.model.FilmShort
import com.example.practices_android.domain.repository.FilmsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.concurrent.ConcurrentHashMap

class FilmsRepositoryImpl(
    private val api: GhibliApi,
    private val favoritesDao: FavoritesDao,
    private val ioDispatcher: CoroutineDispatcher,
) : FilmsRepository {

    private val filmsCache = ConcurrentHashMap<FilmFilters, List<FilmShort>>()
    private val detailsCache = ConcurrentHashMap<String, Film>()

    @Volatile
    private var directorsCache: List<String>? = null

    override fun getFilms(filters: FilmFilters, forceRefresh: Boolean): Flow<List<FilmShort>> = flow {
        val cached = filmsCache[filters]
        if (cached != null && !forceRefresh) {
            emit(cached)
            return@flow
        }
        val films = api.getFilms(
            fields = LIST_FIELDS,
            limit = LIST_LIMIT,
            title = filters.title.trim().ifEmpty { null },
            director = filters.director,
            minRating = filters.minRating.takeIf { it > 0 },
        ).map { it.toFilmShort() }
        filmsCache[filters] = films
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
        val film = try {
            api.getFilm(id).toFilm()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val error = e.toAppError()
            if (error !is AppError.NoConnection) throw error
            favoritesDao.getById(id)?.toFilm() ?: throw error
        }
        detailsCache[id] = film
        emit(film)
    }
        .catch { throw it.toAppError() }
        .flowOn(ioDispatcher)

    override fun getDirectors(): Flow<List<String>> = flow {
        val cached = directorsCache
        if (cached != null) {
            emit(cached)
            return@flow
        }
        val directors = api.getFilms(fields = DIRECTORS_FIELDS, limit = LIST_LIMIT)
            .map { it.director }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
        directorsCache = directors
        emit(directors)
    }
        .catch { throw it.toAppError() }
        .flowOn(ioDispatcher)

    private companion object {
        const val LIST_FIELDS = "id,title,image,director,release_date,rt_score"
        const val DIRECTORS_FIELDS = "id,director"
        const val LIST_LIMIT = 50
    }
}
