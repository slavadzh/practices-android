package com.example.practices_android.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.practices_android.data.remote.GhibliApi
import com.example.practices_android.data.repository.FilmsRepositoryImpl
import com.example.practices_android.domain.repository.FilmsRepository
import com.example.practices_android.domain.usecase.GetFilmDetailsUseCase
import com.example.practices_android.domain.usecase.GetFilmsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

class AppContainer(context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        .addInterceptor(ChuckerInterceptor.Builder(context).build())
        .build()

    private val api: GhibliApi = Retrofit.Builder()
        .baseUrl(GhibliApi.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(GhibliApi::class.java)

    private val filmsRepository: FilmsRepository = FilmsRepositoryImpl(api, Dispatchers.IO)

    val getFilmsUseCase = GetFilmsUseCase(filmsRepository)
    val getFilmDetailsUseCase = GetFilmDetailsUseCase(filmsRepository)

    private companion object {
        const val TIMEOUT_SECONDS = 15L
    }
}
