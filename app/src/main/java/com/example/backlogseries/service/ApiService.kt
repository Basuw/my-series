package com.example.backlogseries.service

import com.example.backlogseries.model.Serie
import com.example.backlogseries.model.SerieResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("tv/{id}")
    suspend fun getSerieDetails(
        @Header("Authorization") authorization: String,
        @Path("id") id: Int,
        @Query("language") language: String = "en-US"
    ): Serie

    @GET("tv/top_rated")
    suspend fun getTopRatedSeries(
        @Header("Authorization") authorization: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): SerieResponse
}

