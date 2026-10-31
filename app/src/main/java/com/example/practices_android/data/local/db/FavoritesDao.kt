package com.example.practices_android.data.local.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    @Query("SELECT * FROM favorite_films ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<FavoriteFilmEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_films WHERE id = :id)")
    fun observeIsFavorite(id: String): Flow<Boolean>

    @Query("SELECT * FROM favorite_films WHERE id = :id")
    suspend fun getById(id: String): FavoriteFilmEntity?

    @Upsert
    suspend fun upsert(film: FavoriteFilmEntity)

    @Query("DELETE FROM favorite_films WHERE id = :id")
    suspend fun deleteById(id: String)
}
