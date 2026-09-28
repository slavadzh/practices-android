package com.example.practices_android.ui.films.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practices_android.R
import com.example.practices_android.data.mockFilms
import com.example.practices_android.data.model.Film
import com.example.practices_android.ui.common.ErrorView
import com.example.practices_android.ui.common.FilmImage
import com.example.practices_android.ui.common.LoadingView
import com.example.practices_android.ui.theme.PracticesandroidTheme

@Composable
fun FilmDetailsScreen(
    onBack: () -> Unit,
    viewModel: FilmDetailsViewModel = viewModel(factory = FilmDetailsViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    FilmDetailsContent(state = state, onBack = onBack, onRetry = viewModel::load)
}

@Composable
private fun FilmDetailsContent(
    state: FilmDetailsUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        when (state) {
            FilmDetailsUiState.Loading -> LoadingView()
            is FilmDetailsUiState.Error -> ErrorView(state.message, onRetry)
            is FilmDetailsUiState.Content -> FilmDetails(state.film)
        }
        IconButton(
            onClick = onBack,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Black.copy(alpha = 0.4f),
                contentColor = Color.White,
            ),
            modifier = Modifier
                .statusBarsPadding()
                .padding(8.dp),
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
        }
    }
}

@Composable
private fun FilmDetails(film: Film) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        ConstraintLayout(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
        ) {
            val (banner, poster, title, originalTitle, rating, stats, divider, description) = createRefs()
            val contentStart = createGuidelineFromStart(16.dp)
            val contentEnd = createGuidelineFromEnd(16.dp)

            Box(
                Modifier.constrainAs(banner) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(240.dp)
                },
            ) {
                FilmImage(
                    url = film.bannerUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0.5f to Color.Transparent,
                                1f to MaterialTheme.colorScheme.background,
                            ),
                        ),
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 8.dp,
                modifier = Modifier.constrainAs(poster) {
                    start.linkTo(contentStart)
                    top.linkTo(banner.bottom)
                    bottom.linkTo(banner.bottom)
                    width = Dimension.value(120.dp)
                    height = Dimension.value(180.dp)
                },
            ) {
                FilmImage(url = film.posterUrl, contentDescription = film.title)
            }

            RatingCircle(
                score = film.rtScore,
                modifier = Modifier.constrainAs(rating) {
                    end.linkTo(contentEnd)
                    top.linkTo(banner.bottom, margin = 12.dp)
                },
            )

            Text(
                film.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(title) {
                    start.linkTo(poster.end, margin = 16.dp)
                    end.linkTo(rating.start, margin = 8.dp)
                    top.linkTo(banner.bottom, margin = 12.dp)
                    width = Dimension.fillToConstraints
                },
            )

            Text(
                "${film.originalTitle}\n${film.originalTitleRomanised}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.constrainAs(originalTitle) {
                    start.linkTo(title.start)
                    end.linkTo(contentEnd)
                    top.linkTo(title.bottom, margin = 4.dp)
                    width = Dimension.fillToConstraints
                },
            )

            val headerBottom = createBottomBarrier(poster, originalTitle, rating)

            FilmStats(
                film = film,
                modifier = Modifier.constrainAs(stats) {
                    top.linkTo(headerBottom, margin = 24.dp)
                    start.linkTo(contentStart)
                    end.linkTo(contentEnd)
                    width = Dimension.fillToConstraints
                },
            )

            HorizontalDivider(
                Modifier.constrainAs(divider) {
                    top.linkTo(stats.bottom, margin = 16.dp)
                    start.linkTo(contentStart)
                    end.linkTo(contentEnd)
                    width = Dimension.fillToConstraints
                },
            )

            Text(
                film.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.constrainAs(description) {
                    top.linkTo(divider.bottom, margin = 16.dp)
                    start.linkTo(contentStart)
                    end.linkTo(contentEnd)
                    width = Dimension.fillToConstraints
                },
            )
        }
    }
}

@Composable
private fun FilmStats(film: Film, modifier: Modifier = Modifier) {
    ConstraintLayout(modifier) {
        val (year, duration, director, producer) = createRefs()
        createHorizontalChain(year, duration)
        createHorizontalChain(director, producer)

        Stat(
            label = stringResource(R.string.film_year),
            value = film.releaseYear.toString(),
            modifier = Modifier.constrainAs(year) {
                top.linkTo(parent.top)
                width = Dimension.fillToConstraints
            },
        )
        Stat(
            label = stringResource(R.string.film_duration),
            value = stringResource(R.string.film_duration_value, film.runningTimeMinutes),
            modifier = Modifier.constrainAs(duration) {
                top.linkTo(parent.top)
                width = Dimension.fillToConstraints
            },
        )
        Stat(
            label = stringResource(R.string.film_director),
            value = film.director,
            modifier = Modifier.constrainAs(director) {
                top.linkTo(year.bottom, margin = 12.dp)
                width = Dimension.fillToConstraints
            },
        )
        Stat(
            label = stringResource(R.string.film_producer),
            value = film.producer,
            modifier = Modifier.constrainAs(producer) {
                top.linkTo(duration.bottom, margin = 12.dp)
                width = Dimension.fillToConstraints
            },
        )
    }
}

@Composable
private fun Stat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier.padding(end = 8.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(value, style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
private fun RatingCircle(score: Int, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        CircularProgressIndicator(
            progress = { score / 100f },
            color = if (score >= 80) Color(0xFF2E7D32) else Color(0xFFF9A825),
            trackColor = Color.Transparent,
            strokeWidth = 4.dp,
            modifier = Modifier.size(48.dp),
        )
        Text(
            "$score%",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun FilmDetailsPreview() {
    PracticesandroidTheme {
        FilmDetailsContent(
            state = FilmDetailsUiState.Content(mockFilms.first()),
            onBack = {},
            onRetry = {},
        )
    }
}
