package com.example.practices_android.data.mapper

import com.example.practices_android.data.local.db.FavoriteFilmEntity
import com.example.practices_android.domain.model.Film
import com.example.practices_android.domain.model.FilmShort

fun Film.toEntity(addedAt: Long) = FavoriteFilmEntity(
    id = id,
    title = title,
    originalTitle = originalTitle,
    originalTitleRomanised = originalTitleRomanised,
    posterUrl = posterUrl,
    bannerUrl = bannerUrl,
    description = description,
    director = director,
    producer = producer,
    releaseYear = releaseYear,
    runningTimeMinutes = runningTimeMinutes,
    rtScore = rtScore,
    addedAt = addedAt,
)

fun FavoriteFilmEntity.toFilm() = Film(
    id = id,
    title = title,
    originalTitle = originalTitle,
    originalTitleRomanised = originalTitleRomanised,
    posterUrl = posterUrl,
    bannerUrl = bannerUrl,
    description = description,
    director = director,
    producer = producer,
    releaseYear = releaseYear,
    runningTimeMinutes = runningTimeMinutes,
    rtScore = rtScore,
)

fun FavoriteFilmEntity.toFilmShort() = FilmShort(
    id = id,
    title = title,
    posterUrl = posterUrl,
    director = director,
    releaseYear = releaseYear,
    rtScore = rtScore,
)
