package com.example.practices_android.data.local.profile

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class AvatarStorage(private val context: Context) {

    private val directory: File
        get() = File(context.filesDir, DIRECTORY).apply { mkdirs() }

    fun importFrom(source: Uri): Uri {
        val target = newFile(prefix = "gallery")
        context.contentResolver.openInputStream(source).use { input ->
            requireNotNull(input) { "Cannot open $source" }
            target.outputStream().use { input.copyTo(it) }
        }
        return target.toContentUri()
    }

    fun createCameraTarget(): Uri = newFile(prefix = "camera").toContentUri()

    fun deleteAllExcept(keepUri: String?) {
        val keepName = keepUri?.let { Uri.parse(it).lastPathSegment }
        directory.listFiles()?.filter { it.name != keepName }?.forEach { it.delete() }
    }

    private fun newFile(prefix: String) = File(directory, "${prefix}_${System.currentTimeMillis()}.jpg")

    private fun File.toContentUri(): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", this)

    private companion object {
        const val DIRECTORY = "avatars"
    }
}
