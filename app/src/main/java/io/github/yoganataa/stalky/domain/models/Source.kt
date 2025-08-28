package io.github.yoganataa.stalky.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Source(
    val id: String,
    val name: String,
    val baseUrl: String,
    val lang: String = "all",
    val isNsfw: Boolean = false,
    val supportsLatest: Boolean = true,
    val scriptVersion: String = "1.0",
    val scriptContent: String = "",
    val isEnabled: Boolean = true
)