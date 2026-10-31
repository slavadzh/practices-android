package com.example.practices_android.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_films")
data class FavoriteFilmEntity(
    @PrimaryKey val id: String,
    val title: String,
    val originalTitle: String,
    val originalTitleRomanised: String,
    val posterUrl: String,
    val bannerUrl: String,
    val description: String,
    val director: String,
    val producer: String,
    val releaseYear: Int,
    val runningTimeMinutes: Int,
    val rtScore: Int,
    val addedAt: Long,
)
