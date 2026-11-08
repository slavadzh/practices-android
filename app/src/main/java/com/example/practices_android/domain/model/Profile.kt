package com.example.practices_android.domain.model

data class Profile(
    val fullName: String = "",
    val position: String = "",
    val avatarUri: String? = null,
    val resumeUrl: String = "",
) {
    val isEmpty: Boolean get() = this == Profile()
}

data class DownloadedFile(
    val uri: String,
    val mimeType: String,
)
