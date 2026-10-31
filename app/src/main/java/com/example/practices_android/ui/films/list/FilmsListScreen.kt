package com.example.practices_android.ui.films.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.practices_android.ui.common.EmptyView
import com.example.practices_android.ui.common.ErrorView
import com.example.practices_android.ui.common.FilmItem
import com.example.practices_android.ui.common.LoadingView
import com.example.practices_android.ui.common.previewFilms
import com.example.practices_android.ui.theme.PracticesandroidTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun FilmsListScreen(
    onFilmClick: (String) -> Unit,
    onFiltersClick: () -> Unit,
    viewModel: FilmsListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showFiltersBadge by viewModel.showFiltersBadge.collectAsStateWithLifecycle()
    FilmsListContent(
        state = state,
        showFiltersBadge = showFiltersBadge,
        onFilmClick = onFilmClick,
        onFiltersClick = onFiltersClick,
        onRefresh = viewModel::refresh,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilmsListContent(
    state: FilmsListUiState,
    showFiltersBadge: Boolean,
    onFilmClick: (String) -> Unit,
    onFiltersClick: () -> Unit,
    onRefresh: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.films_title)) },
                actions = {
                    IconButton(onClick = onFiltersClick) {
                        BadgedBox(badge = { if (showFiltersBadge) Badge() }) {
                            Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.filters_title))
                        }
                    }
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
            is FilmsListUiState.Content ->
                if (state.films.isEmpty()) {
                    EmptyView(stringResource(R.string.films_empty), modifier)
                } else {
                    LazyColumn(
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
}

@Preview(showBackground = true)
@Composable
private fun FilmsListPreview() {
    PracticesandroidTheme {
        FilmsListContent(
            state = FilmsListUiState.Content(previewFilms),
            showFiltersBadge = true,
            onFilmClick = {},
            onFiltersClick = {},
            onRefresh = {},
        )
    }
}
