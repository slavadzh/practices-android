package com.example.practices_android.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.practices_android.domain.model.FilmFilters
import com.example.practices_android.domain.repository.FiltersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException

class FiltersRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : FiltersRepository {

    override val filters: Flow<FilmFilters> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            FilmFilters(
                title = prefs[TITLE].orEmpty(),
                director = prefs[DIRECTOR],
                minRating = prefs[MIN_RATING] ?: 0,
            )
        }
        .distinctUntilChanged()

    override suspend fun save(filters: FilmFilters) {
        dataStore.edit { prefs ->
            prefs[TITLE] = filters.title
            if (filters.director != null) prefs[DIRECTOR] = filters.director else prefs.remove(DIRECTOR)
            prefs[MIN_RATING] = filters.minRating
        }
    }

    private companion object {
        val TITLE = stringPreferencesKey("filter_title")
        val DIRECTOR = stringPreferencesKey("filter_director")
        val MIN_RATING = intPreferencesKey("filter_min_rating")
    }
}
