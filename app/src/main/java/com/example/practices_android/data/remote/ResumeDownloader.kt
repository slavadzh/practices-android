package com.example.practices_android.data.remote

import android.app.DownloadManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.example.practices_android.domain.model.AppError
import com.example.practices_android.domain.model.DownloadedFile
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File

class ResumeDownloader(private val context: Context) {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

    suspend fun download(url: String): DownloadedFile {
        val uri = Uri.parse(url)
        if (uri.scheme != "https" && uri.scheme != "http") throw AppError.InvalidUrl()
        if (!isOnline()) throw AppError.NoConnection()

        val extension = MimeTypeMap.getFileExtensionFromUrl(url).ifEmpty { DEFAULT_EXTENSION }
        val fileName = "resume_${System.currentTimeMillis()}.$extension"
        val request = DownloadManager.Request(uri)
            .setTitle(uri.lastPathSegment ?: fileName)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
        val id = downloadManager.enqueue(request)

        try {
            val result = withTimeoutOrNull(TIMEOUT_MS) { awaitCompletion(id) }
            if (result == null) {
                downloadManager.remove(id)
                throw AppError.NoConnection()
            }
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
            val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val mimeType = downloadManager.getMimeTypeForDownloadedFile(id)
                ?.substringBefore(';')?.trim()?.takeIf { it.isNotEmpty() && it != GENERIC_MIME }
                ?: MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                ?: DEFAULT_MIME
            return DownloadedFile(uri = contentUri.toString(), mimeType = mimeType)
        } catch (e: CancellationException) {
            downloadManager.remove(id)
            throw e
        }
    }

    private suspend fun awaitCompletion(id: Long) {
        while (true) {
            downloadManager.query(DownloadManager.Query().setFilterById(id)).use { cursor ->
                if (!cursor.moveToFirst()) throw AppError.Unknown()
                val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                val reason = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
                when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> return
                    DownloadManager.STATUS_FAILED -> {
                        downloadManager.remove(id)
                        throw if (reason in 400..599) AppError.Server(reason) else AppError.NoConnection()
                    }
                    DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_PENDING -> if (!isOnline()) {
                        downloadManager.remove(id)
                        throw AppError.NoConnection()
                    }
                }
            }
            delay(POLL_INTERVAL_MS)
        }
    }

    private fun isOnline(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        return connectivityManager.getNetworkCapabilities(network)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private companion object {
        const val TIMEOUT_MS = 60_000L
        const val POLL_INTERVAL_MS = 300L
        const val DEFAULT_EXTENSION = "pdf"
        const val DEFAULT_MIME = "application/pdf"
        const val GENERIC_MIME = "application/octet-stream"
    }
}
