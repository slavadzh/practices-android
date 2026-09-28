package com.example.practices_android.data.model

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

fun Film.toShort() = FilmShort(
    id = id,
    title = title,
    posterUrl = posterUrl,
    director = director,
    releaseYear = releaseYear,
    rtScore = rtScore,
)
