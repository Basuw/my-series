package com.example.backlogseries.model

import java.io.Serializable

data class Serie(
    val id: Int,
    val name: String,
    val originalName: String,
    val overview: String,
    val firstAirDate: String,
    val lastAirDate: String?,
    val lastEpisodeToAir: Episode?,
    val episodeRunTime: List<Int>?,
    val numberOfSeasons: Int,
    val numberOfEpisodes: Int,
    val genres: List<Genre>,
    val createdBy: List<Creator>,
    val networks: List<Network>,
    val productionCompanies: List<ProductionCompany>,
    val seasons: List<Season>,
    val spokenLanguages: List<SpokenLanguage>,
    val status: String,
    val type: String,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    val posterPath: String?,
    val backdropPath: String?,
    val homepage: String?,
    val inProduction: Boolean
) : Serializable

fun Serie.getTotalRuntime(): Int? {
    val runtimePerEpisode = episodeRunTime?.firstOrNull() ?: lastEpisodeToAir?.runtime
    return runtimePerEpisode?.let { it * numberOfEpisodes }
}

data class SerieResponse(
    val page: Int,
    val results: List<Serie>,
    val totalPages: Int,
    val totalResults: Int
)

