package com.example.practices_android.ui.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practices_android.domain.model.Profile
import com.example.practices_android.domain.usecase.CreateCameraPhotoUriUseCase
import com.example.practices_android.domain.usecase.ImportAvatarUseCase
import com.example.practices_android.domain.usecase.ObserveProfileUseCase
import com.example.practices_android.domain.usecase.SaveProfileUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val profile: Profile = Profile(),
    val isLoaded: Boolean = false,
    val isAvatarLoading: Boolean = false,
    val isAvatarError: Boolean = false,
) {
    val isResumeUrlValid: Boolean
        get() = profile.resumeUrl.isBlank() ||
            RESUME_URL_REGEX.matches(profile.resumeUrl.trim())

    private companion object {
        val RESUME_URL_REGEX = Regex("^https?://\\S+$", RegexOption.IGNORE_CASE)
    }
}

class EditProfileViewModel(
    private val savedStateHandle: SavedStateHandle,
    observeProfile: ObserveProfileUseCase,
    private val saveProfile: SaveProfileUseCase,
    private val importAvatar: ImportAvatarUseCase,
    private val createCameraPhotoUri: CreateCameraPhotoUriUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileUiState())
    val state: StateFlow<EditProfileUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val saved = observeProfile().first()
            _state.update { it.copy(profile = saved, isLoaded = true) }
        }
    }

    fun onFullNameChange(value: String) = updateProfile { it.copy(fullName = value) }

    fun onPositionChange(value: String) = updateProfile { it.copy(position = value) }

    fun onResumeUrlChange(value: String) = updateProfile { it.copy(resumeUrl = value) }

    fun onGalleryPhotoPicked(uri: String) {
        viewModelScope.launch {
            _state.update { it.copy(isAvatarLoading = true, isAvatarError = false) }
            try {
                val imported = importAvatar(uri)
                _state.update { it.copy(profile = it.profile.copy(avatarUri = imported)) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isAvatarError = true) }
            } finally {
                _state.update { it.copy(isAvatarLoading = false) }
            }
        }
    }

    fun prepareCameraUri(): String = createCameraPhotoUri().also { savedStateHandle[PENDING_CAMERA_URI] = it }

    fun onCameraResult(success: Boolean) {
        val uri = savedStateHandle.remove<String>(PENDING_CAMERA_URI) ?: return
        if (success) updateProfile { it.copy(avatarUri = uri) }
    }

    fun save(onDone: () -> Unit) {
        val current = _state.value
        if (!current.isLoaded || !current.isResumeUrlValid) return
        viewModelScope.launch {
            saveProfile(current.profile)
            onDone()
        }
    }

    private fun updateProfile(transform: (Profile) -> Profile) {
        _state.update { it.copy(profile = transform(it.profile), isAvatarError = false) }
    }

    private companion object {
        const val PENDING_CAMERA_URI = "pending_camera_uri"
    }
}
