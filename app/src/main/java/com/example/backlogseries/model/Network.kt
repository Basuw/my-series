package com.example.backlogseries.model

import java.io.Serializable

data class Network(
    val id: Int,
    val name: String,
    val logoPath: String?,
    val originCountry: String
) : Serializable