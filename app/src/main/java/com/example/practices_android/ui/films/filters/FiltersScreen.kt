package com.example.practices_android.ui.films.filters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.practices_android.R
import com.example.practices_android.domain.model.FilmFilters
import com.example.practices_android.ui.common.LoadingView
import com.example.practices_android.ui.theme.PracticesandroidTheme
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

private const val RATING_STEP = 5

@Composable
fun FiltersScreen(
    onBack: () -> Unit,
    viewModel: FiltersViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    FiltersContent(
        state = state,
        onBack = onBack,
        onTitleChange = viewModel::onTitleChange,
        onDirectorSelect = viewModel::onDirectorSelect,
        onMinRatingChange = viewModel::onMinRatingChange,
        onReset = viewModel::reset,
        onApply = { viewModel.apply(onDone = onBack) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltersContent(
    state: FiltersUiState,
    onBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDirectorSelect: (String?) -> Unit,
    onMinRatingChange: (Int) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.filters_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
            )
        },
        bottomBar = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                OutlinedButton(
                    onClick = onReset,
                    enabled = !state.filters.isDefault,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.filters_reset))
                }
                Button(
                    onClick = onApply,
                    enabled = state.isLoaded,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.filters_apply))
                }
            }
        },
        contentWindowInsets = WindowInsets(0),
    ) { padding ->
        if (!state.isLoaded) {
            LoadingView(Modifier.padding(padding))
            return@Scaffold
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            OutlinedTextField(
                value = state.filters.title,
                onValueChange = onTitleChange,
                label = { Text(stringResource(R.string.filters_title_label)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier.fillMaxWidth(),
            )
            RatingSection(
                rating = state.filters.minRating,
                onChange = onMinRatingChange,
            )
            DirectorSection(
                selected = state.filters.director,
                directors = state.directors,
                onSelect = onDirectorSelect,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DirectorSection(
    selected: String?,
    directors: List<String>,
    onSelect: (String?) -> Unit,
) {
    val options = if (selected != null && selected !in directors) directors + selected else directors
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.filters_director_label), style = MaterialTheme.typography.titleMedium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selected == null,
                onClick = { onSelect(null) },
                label = { Text(stringResource(R.string.filters_director_any)) },
            )
            options.forEach { director ->
                FilterChip(
                    selected = selected == director,
                    onClick = { onSelect(director) },
                    label = { Text(director) },
                )
            }
        }
        if (directors.isEmpty()) {
            Text(
                stringResource(R.string.filters_directors_unavailable),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun RatingSection(rating: Int, onChange: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            stringResource(R.string.filters_min_rating_label, rating),
            style = MaterialTheme.typography.titleMedium,
        )
        Slider(
            value = rating.toFloat(),
            onValueChange = { onChange((it / RATING_STEP).roundToInt() * RATING_STEP) },
            valueRange = 0f..100f,
            steps = 100 / RATING_STEP - 1,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FiltersPreview() {
    PracticesandroidTheme {
        FiltersContent(
            state = FiltersUiState(
                filters = FilmFilters(title = "castle", director = "Hayao Miyazaki", minRating = 80),
                directors = listOf("Hayao Miyazaki", "Isao Takahata", "Gorō Miyazaki"),
                isLoaded = true,
            ),
            onBack = {},
            onTitleChange = {},
            onDirectorSelect = {},
            onMinRatingChange = {},
            onReset = {},
            onApply = {},
        )
    }
}
