package com.example.practices_android.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteFilmEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao

    companion object {
        const val NAME = "ghibli.db"
    }
}
