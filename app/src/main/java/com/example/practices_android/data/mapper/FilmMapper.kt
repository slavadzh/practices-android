package com.example.practices_android.data.mapper

import com.example.practices_android.data.remote.dto.FilmDto
import com.example.practices_android.domain.model.Film
import com.example.practices_android.domain.model.FilmShort

fun FilmDto.toFilm() = Film(
    id = id,
    title = title,
    originalTitle = originalTitle,
    originalTitleRomanised = originalTitleRomanised,
    posterUrl = image,
    bannerUrl = movieBanner,
    description = description,
    director = director,
    producer = producer,
    releaseYear = releaseDate.toIntOrNull() ?: 0,
    runningTimeMinutes = runningTime.toIntOrNull() ?: 0,
    rtScore = rtScore.toIntOrNull() ?: 0,
)

fun FilmDto.toFilmShort() = FilmShort(
    id = id,
    title = title,
    posterUrl = image,
    director = director,
    releaseYear = releaseDate.toIntOrNull() ?: 0,
    rtScore = rtScore.toIntOrNull() ?: 0,
)
