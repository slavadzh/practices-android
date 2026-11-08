package com.example.practices_android.data.repository

import android.net.Uri
import androidx.datastore.core.DataStore
import com.example.practices_android.data.local.profile.AvatarStorage
import com.example.practices_android.data.local.profile.ProfileDto
import com.example.practices_android.data.remote.ResumeDownloader
import com.example.practices_android.domain.model.DownloadedFile
import com.example.practices_android.domain.model.Profile
import com.example.practices_android.domain.repository.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ProfileRepositoryImpl(
    private val dataStore: DataStore<ProfileDto>,
    private val avatarStorage: AvatarStorage,
    private val resumeDownloader: ResumeDownloader,
    private val ioDispatcher: CoroutineDispatcher,
) : ProfileRepository {

    override val profile: Flow<Profile> = dataStore.data.map { it.toDomain() }

    override suspend fun save(profile: Profile) {
        dataStore.updateData { profile.toDto() }
        withContext(ioDispatcher) { avatarStorage.deleteAllExcept(profile.avatarUri) }
    }

    override suspend fun importAvatar(sourceUri: String): String = withContext(ioDispatcher) {
        avatarStorage.importFrom(Uri.parse(sourceUri)).toString()
    }

    override fun createCameraPhotoUri(): String = avatarStorage.createCameraTarget().toString()

    override suspend fun downloadResume(url: String): DownloadedFile = withContext(ioDispatcher) {
        resumeDownloader.download(url)
    }

    private fun ProfileDto.toDomain() = Profile(
        fullName = fullName,
        position = position,
        avatarUri = avatarUri,
        resumeUrl = resumeUrl,
    )

    private fun Profile.toDto() = ProfileDto(
        fullName = fullName,
        position = position,
        avatarUri = avatarUri,
        resumeUrl = resumeUrl,
    )
}
