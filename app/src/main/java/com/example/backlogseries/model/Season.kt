package com.example.backlogseries.model

data class Season(
    val id: Int,
    val name: String,
    val overview: String,
    val airDate: String?,
    val episodeCount: Int,
    val seasonNumber: Int,
    val posterPath: String?,
    val voteAverage: Double,
)
