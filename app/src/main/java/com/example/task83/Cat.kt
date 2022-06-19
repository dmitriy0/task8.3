package com.example.task83

import kotlinx.serialization.Serializable

@Serializable
data class Cat(
    val url: String,
    val id: String,
    val breeds: List<Breeds> = listOf(
        Breeds(
            "no information available",
            "no information available"
        )
    )
)

@Serializable
data class Breeds(
    val name: String,
    val temperament: String
)
