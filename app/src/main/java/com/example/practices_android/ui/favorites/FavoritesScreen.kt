package com.example.practices_android.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.practices_android.R
import com.example.practices_android.domain.model.FilmShort
import com.example.practices_android.ui.common.EmptyView
import com.example.practices_android.ui.common.FilmItem
import com.example.practices_android.ui.common.LoadingView
import com.example.practices_android.ui.common.previewFilms
import com.example.practices_android.ui.theme.PracticesandroidTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesScreen(
    onFilmClick: (String) -> Unit,
    viewModel: FavoritesViewModel = koinViewModel(),
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    FavoritesContent(favorites = favorites, onFilmClick = onFilmClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesContent(
    favorites: List<FilmShort>?,
    onFilmClick: (String) -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.tab_favorites)) }) },
        contentWindowInsets = WindowInsets(0),
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when {
            favorites == null -> LoadingView(modifier)
            favorites.isEmpty() -> EmptyView(stringResource(R.string.favorites_empty), modifier)
            else -> LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(favorites, key = { it.id }) { film ->
                    FilmItem(film = film, onClick = { onFilmClick(film.id) })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesPreview() {
    PracticesandroidTheme {
        FavoritesContent(favorites = previewFilms, onFilmClick = {})
    }
}
