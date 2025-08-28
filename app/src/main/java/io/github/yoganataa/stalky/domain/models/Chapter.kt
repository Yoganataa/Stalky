package io.github.yoganataa.stalky.domain.models

data class Chapter(
    val id: String,
    val mangaId: String,
    val url: String,
    val name: String,
    val dateUpload: Long = 0L,
    val chapterNumber: Float = -1f,
    val scanlator: String? = null,
    val read: Boolean = false,
    val bookmark: Boolean = false,
    val lastPageRead: Int = 0
)