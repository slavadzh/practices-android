package com.example.practices_android.data.local.profile

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class ProfileDto(
    val fullName: String = "",
    val position: String = "",
    val avatarUri: String? = null,
    val resumeUrl: String = "",
)

object ProfileSerializer : Serializer<ProfileDto> {

    private val json = Json { ignoreUnknownKeys = true }

    override val defaultValue: ProfileDto = ProfileDto()

    override suspend fun readFrom(input: InputStream): ProfileDto = try {
        json.decodeFromString(ProfileDto.serializer(), input.readBytes().decodeToString())
    } catch (e: SerializationException) {
        throw CorruptionException("Cannot read profile", e)
    }

    override suspend fun writeTo(t: ProfileDto, output: OutputStream) {
        output.write(json.encodeToString(ProfileDto.serializer(), t).encodeToByteArray())
    }
}
