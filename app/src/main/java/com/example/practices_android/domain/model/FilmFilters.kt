package com.example.practices_android.domain.model

data class FilmFilters(
    val title: String = "",
    val director: String? = null,
    val minRating: Int = 0,
) {
    val isDefault: Boolean get() = this == FilmFilters()
}
