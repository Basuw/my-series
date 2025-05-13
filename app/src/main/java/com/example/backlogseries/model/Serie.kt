package com.example.backlogseries.model

import java.io.Serializable

data class Serie(
    val id: Int,
    val name: String,
    val originalName: String,
    val overview: String,
    val first_air_date: String,
    val last_air_date: String?,
    val lastEpisodeToAir: Episode?,
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

fun Serie.getTotalRuntime(): Int? {
    val runtimePerEpisode = episode_run_time?.firstOrNull() ?: lastEpisodeToAir?.runtime
    return runtimePerEpisode?.let { it * number_of_episodes }
}

data class SerieResponse(
    val page: Int,
    val results: List<Serie>,
    val totalPages: Int,
    val totalResults: Int
)

