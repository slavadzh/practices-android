package com.example.practices_android.ui.profile

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.practices_android.R
import com.example.practices_android.domain.model.AppError
import com.example.practices_android.domain.model.DownloadedFile
import com.example.practices_android.domain.model.Profile
import com.example.practices_android.ui.common.LoadingView
import com.example.practices_android.ui.common.message
import com.example.practices_android.ui.theme.PracticesandroidTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    onEditClick: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val isDownloading by viewModel.isDownloading.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val noViewerMessage = stringResource(R.string.profile_no_viewer)
    var shownError by remember { mutableStateOf<AppError?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is ProfileEvent.OpenFile ->
                    if (!context.openFile(event.file)) snackbarHostState.showSnackbar(noViewerMessage)
                is ProfileEvent.ShowError -> shownError = event.error
            }
        }
    }

    shownError?.let { error ->
        val message = error.message()
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message)
            shownError = null
        }
    }

    ProfileContent(
        profile = profile,
        isDownloading = isDownloading,
        snackbarHostState = snackbarHostState,
        onEditClick = onEditClick,
        onResumeClick = viewModel::onResumeClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    profile: Profile?,
    isDownloading: Boolean,
    snackbarHostState: SnackbarHostState,
    onEditClick: () -> Unit,
    onResumeClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tab_profile)) },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.profile_edit))
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0),
    ) { padding ->
        if (profile == null) {
            LoadingView(Modifier.padding(padding))
            return@Scaffold
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            AvatarImage(
                uri = profile.avatarUri,
                contentDescription = stringResource(R.string.profile_avatar),
                size = 140.dp,
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = profile.fullName.ifBlank { stringResource(R.string.profile_name_empty) },
                style = MaterialTheme.typography.headlineSmall,
                color = if (profile.fullName.isBlank()) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                textAlign = TextAlign.Center,
            )
            if (profile.position.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = profile.position,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
            if (profile.isEmpty) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.profile_empty_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onResumeClick,
                enabled = profile.resumeUrl.isNotBlank() && !isDownloading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (isDownloading) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(8.dp))
                    Text(stringResource(R.string.profile_resume_downloading))
                } else {
                    Text(stringResource(R.string.profile_resume))
                }
            }
            if (profile.resumeUrl.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = profile.resumeUrl,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

private fun Context.openFile(file: DownloadedFile): Boolean {
    val intent = Intent(Intent.ACTION_VIEW)
        .setDataAndType(Uri.parse(file.uri), file.mimeType)
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    return try {
        startActivity(Intent.createChooser(intent, getString(R.string.profile_open_resume)))
        true
    } catch (e: ActivityNotFoundException) {
        false
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfilePreview() {
    PracticesandroidTheme {
        ProfileContent(
            profile = Profile(
                fullName = "Иванов Иван Иванович",
                position = "Android-разработчик",
                resumeUrl = "https://example.com/resume.pdf",
            ),
            isDownloading = false,
            snackbarHostState = SnackbarHostState(),
            onEditClick = {},
            onResumeClick = {},
        )
    }
}
