package com.example.practices_android.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.practices_android.R

object Routes {
    const val FILM_ID_ARG = "filmId"

    const val FILMS_GRAPH = "films_graph"
    const val FILMS_LIST = "films"
    const val FILM_DETAILS = "films/{$FILM_ID_ARG}"
    const val FILTERS = "filters"

    const val FAVORITES_GRAPH = "favorites_graph"
    const val FAVORITES = "favorites"
    const val FAVORITE_DETAILS = "favorites/{$FILM_ID_ARG}"

    const val PROFILE = "profile"

    fun filmDetails(id: String) = "films/$id"

    fun favoriteDetails(id: String) = "favorites/$id"
}

enum class BottomTab(
    val route: String,
    val startRoute: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector,
) {
    FILMS(Routes.FILMS_GRAPH, Routes.FILMS_LIST, R.string.tab_films, Icons.Filled.PlayArrow),
    FAVORITES(Routes.FAVORITES_GRAPH, Routes.FAVORITES, R.string.tab_favorites, Icons.Filled.Favorite),
    PROFILE(Routes.PROFILE, Routes.PROFILE, R.string.tab_profile, Icons.Filled.Person),
}
