package com.example.practices_android.domain.repository

import com.example.practices_android.domain.model.DownloadedFile
import com.example.practices_android.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    val profile: Flow<Profile>

    suspend fun save(profile: Profile)

    suspend fun importAvatar(sourceUri: String): String

    fun createCameraPhotoUri(): String

    suspend fun downloadResume(url: String): DownloadedFile
}
