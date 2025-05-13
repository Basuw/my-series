package com.example.backlogseries.model

import java.io.Serializable

data class ProductionCompany(
    val id: Int,
    val name: String,
    val logoPath: String?,
    val originCountry: String
) : Serializable