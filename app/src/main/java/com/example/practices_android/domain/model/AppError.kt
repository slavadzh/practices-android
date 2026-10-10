package com.example.practices_android.domain.model

sealed class AppError(cause: Throwable? = null) : Exception(cause) {
    class NoConnection(cause: Throwable? = null) : AppError(cause)
    class NotFound(cause: Throwable? = null) : AppError(cause)
    class Server(val code: Int, cause: Throwable? = null) : AppError(cause)
    class Unknown(cause: Throwable? = null) : AppError(cause)
}
