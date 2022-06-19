package com.example.task83

import kotlinx.serialization.Serializable

@Serializable
data class Vote(
    val image_id: String,
    val sub_id: String,
    val value: Int,
)