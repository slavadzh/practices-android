package com.example.practices_android.ui.common

import com.example.practices_android.domain.model.Film
import com.example.practices_android.domain.model.FilmShort

internal val previewFilm = Film(
    id = "2baf70d1-42bb-4437-b551-e5fed5a87abe",
    title = "Castle in the Sky",
    originalTitle = "天空の城ラピュタ",
    originalTitleRomanised = "Tenkū no shiro Rapyuta",
    posterUrl = "",
    bannerUrl = "",
    description = "The orphan Sheeta inherited a mysterious crystal that links her to the mythical sky-kingdom of Laputa.",
    director = "Hayao Miyazaki",
    producer = "Isao Takahata",
    releaseYear = 1986,
    runningTimeMinutes = 124,
    rtScore = 95,
)

internal val previewFilms = List(5) { index ->
    FilmShort(
        id = index.toString(),
        title = previewFilm.title,
        posterUrl = "",
        director = previewFilm.director,
        releaseYear = previewFilm.releaseYear,
        rtScore = previewFilm.rtScore,
    )
}
