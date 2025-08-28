package io.github.yoganataa.stalky.domain.models

data class Manga(
    val id: String,
    val sourceId: String,
    val url: String,
    val title: String,
    val author: String? = null,
    val artist: String? = null,
    val description: String? = null,
    val genre: String? = null,
    val status: Int = UNKNOWN,
    val thumbnailUrl: String? = null,
    val favorite: Boolean = false,
    val lastUpdated: Long = 0L,
    val initialized: Boolean = false
) {
    companion object {
        const val UNKNOWN = 0
        const val ONGOING = 1
        const val COMPLETED = 2
        const val LICENSED = 3
        const val PUBLISHING_FINISHED = 4
        const val CANCELLED = 5
        const val ON_HIATUS = 6
    }
}