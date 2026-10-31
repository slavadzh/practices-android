package com.example.practices_android.data.mapper

import com.example.practices_android.domain.model.AppError
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection

fun Throwable.toAppError(): AppError = when (this) {
    is AppError -> this
    is IOException -> AppError.NoConnection(this)
    is HttpException ->
        if (code() == HttpURLConnection.HTTP_NOT_FOUND) AppError.NotFound(this)
        else AppError.Server(code(), this)
    else -> AppError.Unknown(this)
}
