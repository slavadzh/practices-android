package com.example.practices_android.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practices_android.domain.model.AppError
import com.example.practices_android.domain.model.DownloadedFile
import com.example.practices_android.domain.model.Profile
import com.example.practices_android.domain.usecase.DownloadResumeUseCase
import com.example.practices_android.domain.usecase.ObserveProfileUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface ProfileEvent {
    data class OpenFile(val file: DownloadedFile) : ProfileEvent
    data class ShowError(val error: AppError) : ProfileEvent
}

class ProfileViewModel(
    observeProfile: ObserveProfileUseCase,
    private val downloadResume: DownloadResumeUseCase,
) : ViewModel() {

    val profile: StateFlow<Profile?> = observeProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading: StateFlow<Boolean> = _isDownloading.asStateFlow()

    private val _events = Channel<ProfileEvent>(Channel.BUFFERED)
    val events: Flow<ProfileEvent> = _events.receiveAsFlow()

    fun onResumeClick() {
        val url = profile.value?.resumeUrl?.takeIf { it.isNotBlank() } ?: return
        if (_isDownloading.value) return
        viewModelScope.launch {
            _isDownloading.value = true
            try {
                _events.send(ProfileEvent.OpenFile(downloadResume(url)))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.send(ProfileEvent.ShowError(e as? AppError ?: AppError.Unknown(e)))
            } finally {
                _isDownloading.value = false
            }
        }
    }
}
