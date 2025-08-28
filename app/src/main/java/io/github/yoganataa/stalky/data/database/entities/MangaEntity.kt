package io.github.yoganataa.stalky.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.yoganataa.stalky.domain.models.Manga

@Entity(tableName = "manga")
data class MangaEntity(
    @PrimaryKey val id: String,
    val sourceId: String,
    val url: String,
    val title: String,
    val author: String?,
    val artist: String?,
    val description: String?,
    val genre: String?,
    val status: Int,
    val thumbnailUrl: String?,
    val favorite: Boolean,
    val lastUpdated: Long,
    val initialized: Boolean
) {
    fun toDomainModel(): Manga = Manga(
        id = id,
        sourceId = sourceId,
        url = url,
        title = title,
        author = author,
        artist = artist,
        description = description,
        genre = genre,
        status = status,
        thumbnailUrl = thumbnailUrl,
        favorite = favorite,
        lastUpdated = lastUpdated,
        initialized = initialized
    )
    
    companion object {
        fun fromDomainModel(manga: Manga): MangaEntity = MangaEntity(
            id = manga.id,
            sourceId = manga.sourceId,
            url = manga.url,
            title = manga.title,
            author = manga.author,
            artist = manga.artist,
            description = manga.description,
            genre = manga.genre,
            status = manga.status,
            thumbnailUrl = manga.thumbnailUrl,
            favorite = manga.favorite,
            lastUpdated = manga.lastUpdated,
            initialized = manga.initialized
        )
    }
}