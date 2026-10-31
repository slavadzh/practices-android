package com.example.practices_android.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FilmDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String = "",
    @SerialName("original_title") val originalTitle: String = "",
    @SerialName("original_title_romanised") val originalTitleRomanised: String = "",
    @SerialName("image") val image: String = "",
    @SerialName("movie_banner") val movieBanner: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("director") val director: String = "",
    @SerialName("producer") val producer: String = "",
    @SerialName("release_date") val releaseDate: String = "",
    @SerialName("running_time") val runningTime: String = "",
    @SerialName("rt_score") val rtScore: String = "",
)
