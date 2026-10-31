package com.example.practices_android.data.remote

import com.example.practices_android.data.remote.dto.FilmDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GhibliApi {

    @GET("films")
    suspend fun getFilms(
        @Query("fields") fields: String,
        @Query("limit") limit: Int,
        @Query("title_like") title: String? = null,
        @Query("director") director: String? = null,
        @Query("rt_score_gte") minRating: Int? = null,
    ): List<FilmDto>

    @GET("films/{id}")
    suspend fun getFilm(@Path("id") id: String): FilmDto

    companion object {
        const val BASE_URL = "https://ghibliapi.vercel.app/"
    }
}
