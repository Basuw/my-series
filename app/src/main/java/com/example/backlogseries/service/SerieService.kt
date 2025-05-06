package com.example.backlogseries.service

import android.content.Context
import com.example.backlogseries.R
import com.example.backlogseries.model.Serie
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SerieService(context: Context) {
    private val apiService: ApiService
    private val bearerToken: String = "Bearer ${context.getString(R.string.bearerTMDB)}"

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    suspend fun getSerieDetails(id: Int): Serie {
        return apiService.getSerieDetails(bearerToken, id)
    }
}
