package com.example.backlogseries.model

import java.io.Serializable

data class Creator(
    val id: Int,
    val name: String,
    val gender: Int?,
    val profilePath: String?
) : Serializable