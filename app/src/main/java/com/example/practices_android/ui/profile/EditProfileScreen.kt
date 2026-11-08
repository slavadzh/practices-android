package com.example.practices_android.ui.profile

import android.content.ActivityNotFoundException
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.practices_android.R
import com.example.practices_android.domain.model.Profile
import com.example.practices_android.ui.common.LoadingView
import com.example.practices_android.ui.theme.PracticesandroidTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: EditProfileViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val noCameraMessage = stringResource(R.string.edit_profile_no_camera)

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) viewModel.onGalleryPhotoPicked(uri.toString())
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        viewModel.onCameraResult(success)
    }

    EditProfileContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onFullNameChange = viewModel::onFullNameChange,
        onPositionChange = viewModel::onPositionChange,
        onResumeUrlChange = viewModel::onResumeUrlChange,
        onGalleryClick = {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onCameraClick = {
            try {
                cameraLauncher.launch(Uri.parse(viewModel.prepareCameraUri()))
            } catch (e: ActivityNotFoundException) {
                viewModel.onCameraResult(false)
                scope.launch { snackbarHostState.showSnackbar(noCameraMessage) }
            }
        },
        onDone = { viewModel.save(onDone = onBack) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileContent(
    state: EditProfileUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onFullNameChange: (String) -> Unit,
    onPositionChange: (String) -> Unit,
    onResumeUrlChange: (String) -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onDone: () -> Unit,
) {
    var showSourceDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_profile_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
            )
        },
        bottomBar = {
            Button(
                onClick = onDone,
                enabled = state.isLoaded && state.isResumeUrlValid && !state.isAvatarLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text(stringResource(R.string.filters_apply))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0),
    ) { padding ->
        if (!state.isLoaded) {
            LoadingView(Modifier.padding(padding))
            return@Scaffold
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            EditableAvatar(
                uri = state.profile.avatarUri,
                isLoading = state.isAvatarLoading,
                onClick = { showSourceDialog = true },
            )
            if (state.isAvatarError) {
                Text(
                    stringResource(R.string.edit_profile_avatar_error),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            OutlinedTextField(
                value = state.profile.fullName,
                onValueChange = onFullNameChange,
                label = { Text(stringResource(R.string.edit_profile_full_name)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.profile.position,
                onValueChange = onPositionChange,
                label = { Text(stringResource(R.string.edit_profile_position)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.profile.resumeUrl,
                onValueChange = onResumeUrlChange,
                label = { Text(stringResource(R.string.edit_profile_resume_url)) },
                placeholder = { Text("https://…/resume.pdf") },
                singleLine = true,
                isError = !state.isResumeUrlValid,
                supportingText = if (state.isResumeUrlValid) null else {
                    { Text(stringResource(R.string.error_invalid_url)) }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    if (showSourceDialog) {
        PhotoSourceDialog(
            onDismiss = { showSourceDialog = false },
            onGalleryClick = {
                showSourceDialog = false
                onGalleryClick()
            },
            onCameraClick = {
                showSourceDialog = false
                onCameraClick()
            },
        )
    }
}

@Composable
private fun EditableAvatar(uri: String?, isLoading: Boolean, onClick: () -> Unit) {
    val description = stringResource(R.string.edit_profile_change_photo)
    Box(
        modifier = Modifier
            .size(140.dp)
            .clickable(onClickLabel = description, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
    ) {
        AvatarImage(uri = uri, contentDescription = null, size = 140.dp)
        if (isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
        ) {
            Icon(
                Icons.Filled.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun PhotoSourceDialog(
    onDismiss: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_profile_photo_source)) },
        text = {
            val colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            Column {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.edit_profile_gallery)) },
                    colors = colors,
                    modifier = Modifier.clickable(onClick = onGalleryClick),
                )
                ListItem(
                    headlineContent = { Text(stringResource(R.string.edit_profile_camera)) },
                    colors = colors,
                    modifier = Modifier.clickable(onClick = onCameraClick),
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun EditProfilePreview() {
    PracticesandroidTheme {
        EditProfileContent(
            state = EditProfileUiState(
                profile = Profile(fullName = "Иванов Иван", position = "Android-разработчик", resumeUrl = "ftp://x"),
                isLoaded = true,
            ),
            snackbarHostState = SnackbarHostState(),
            onBack = {},
            onFullNameChange = {},
            onPositionChange = {},
            onResumeUrlChange = {},
            onGalleryClick = {},
            onCameraClick = {},
            onDone = {},
        )
    }
}
