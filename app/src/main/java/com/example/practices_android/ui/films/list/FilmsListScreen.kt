package com.example.practices_android.ui.films.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practices_android.R
import com.example.practices_android.domain.model.FilmShort
import com.example.practices_android.ui.common.ErrorView
import com.example.practices_android.ui.common.FilmImage
import com.example.practices_android.ui.common.LoadingView
import com.example.practices_android.ui.common.previewFilms
import com.example.practices_android.ui.theme.PracticesandroidTheme

@Composable
fun FilmsListScreen(
    onFilmClick: (String) -> Unit,
    viewModel: FilmsListViewModel = viewModel(factory = FilmsListViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    FilmsListContent(state = state, onFilmClick = onFilmClick, onRefresh = viewModel::refresh)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilmsListContent(
    state: FilmsListUiState,
    onFilmClick: (String) -> Unit,
    onRefresh: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.films_title)) },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.refresh))
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (state) {
            FilmsListUiState.Loading -> LoadingView(modifier)
            is FilmsListUiState.Error -> ErrorView(state.error, onRefresh, modifier)
            is FilmsListUiState.Content -> LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.films, key = { it.id }) { film ->
                    FilmItem(film = film, onClick = { onFilmClick(film.id) })
                }
            }
        }
    }
}

@Composable
private fun FilmItem(film: FilmShort, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            FilmImage(
                url = film.posterUrl,
                contentDescription = film.title,
                modifier = Modifier
                    .size(width = 64.dp, height = 96.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            ) {
                Text(
                    film.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${film.releaseYear} · ${film.director}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            RatingBadge(film.rtScore)
        }
    }
}

@Composable
private fun RatingBadge(score: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Filled.Star,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(18.dp),
        )
        Text("$score", style = MaterialTheme.typography.labelLarge)
    }
}

@Preview(showBackground = true)
@Composable
private fun FilmsListPreview() {
    PracticesandroidTheme {
        FilmsListContent(
            state = FilmsListUiState.Content(previewFilms),
            onFilmClick = {},
            onRefresh = {},
        )
    }
}
