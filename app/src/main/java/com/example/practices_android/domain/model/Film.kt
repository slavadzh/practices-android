package com.example.practices_android.domain.model

data class Film(
    val id: String,
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
)

data class FilmShort(
    val id: String,
    val title: String,
    val posterUrl: String,
    val director: String,
    val releaseYear: Int,
    val rtScore: Int,
)
