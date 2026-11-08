package com.example.practices_android.domain.usecase

import com.example.practices_android.domain.model.DownloadedFile
import com.example.practices_android.domain.model.Profile
import com.example.practices_android.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class ObserveProfileUseCase(private val repository: ProfileRepository) {
    operator fun invoke(): Flow<Profile> = repository.profile
}

class SaveProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(profile: Profile) = repository.save(
        profile.copy(
            fullName = profile.fullName.trim(),
            position = profile.position.trim(),
            resumeUrl = profile.resumeUrl.trim(),
        ),
    )
}

class ImportAvatarUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(sourceUri: String): String = repository.importAvatar(sourceUri)
}

class CreateCameraPhotoUriUseCase(private val repository: ProfileRepository) {
    operator fun invoke(): String = repository.createCameraPhotoUri()
}

class DownloadResumeUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(url: String): DownloadedFile = repository.downloadResume(url)
}
