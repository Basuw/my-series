package com.example.backlogseries.service

import com.example.backlogseries.model.Serie
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
}
