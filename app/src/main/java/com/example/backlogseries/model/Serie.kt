package com.example.backlogseries.model

import android.util.Log
import androidx.compose.runtime.MutableState
import java.io.Serializable

data class Serie(
    val id: Int,
    val name: String,
    val originalName: String,
    val overview: String,
    val first_air_date: String,
    val last_air_date: String?,
    val last_episode_to_air: Episode?,
    val episode_run_time: List<Int>?,
    val number_of_seasons: Int,
    val number_of_episodes: Int,
    val genres: List<Genre>,
    val createdBy: List<Creator>,
    val networks: List<Network>,
    val productionCompanies: List<ProductionCompany>,
    val seasons: List<Season>,
    val spokenLanguages: List<SpokenLanguage>,
    val status: String,
    val type: String,
    val vote_average: Double,
    val vote_count: Int,
    val popularity: Double,
    val poster_path: String?,
    val backdrop_path: String?,
    val homepage: String?,
    val inProduction: Boolean
) : Serializable

fun Serie.getTotalRuntime(calculated: MutableState<Boolean>): Int? {
    if (episode_run_time.isNullOrEmpty()){
        calculated.value = true ;
        val runtimePerEpisode = episode_run_time?.firstOrNull() ?: last_episode_to_air?.runtime
        Log.d("runtime calculated", "Runtime per episode: $runtimePerEpisode")
        return runtimePerEpisode?.let { it * number_of_episodes }
    }else{
        Log.d("runtime fetched", "Runtime per episode: ")
        return this.episode_run_time[0]
    }
}

data class SerieResponse(
    val page: Int,
    val results: List<Serie>,
    val totalPages: Int,
    val totalResults: Int
)

