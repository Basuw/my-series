package com.example.backlogseries.model

import java.io.Serializable

data class SpokenLanguage(
    val iso6391: String,
    val name: String
) : Serializable
