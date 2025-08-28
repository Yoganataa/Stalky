package io.github.yoganataa.stalky.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.yoganataa.stalky.domain.models.Chapter

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey val id: String,
    val mangaId: String,
    val url: String,
    val name: String,
    val dateUpload: Long,
    val chapterNumber: Float,
    val scanlator: String?,
    val read: Boolean,
    val bookmark: Boolean,
    val lastPageRead: Int
) {
    fun toDomainModel(): Chapter = Chapter(
        id = id,
        mangaId = mangaId,
        url = url,
        name = name,
        dateUpload = dateUpload,
        chapterNumber = chapterNumber,
        scanlator = scanlator,
        read = read,
        bookmark = bookmark,
        lastPageRead = lastPageRead
    )
    
    companion object {
        fun fromDomainModel(chapter: Chapter): ChapterEntity = ChapterEntity(
            id = chapter.id,
            mangaId = chapter.mangaId,
            url = chapter.url,
            name = chapter.name,
            dateUpload = chapter.dateUpload,
            chapterNumber = chapter.chapterNumber,
            scanlator = chapter.scanlator,
            read = chapter.read,
            bookmark = chapter.bookmark,
            lastPageRead = chapter.lastPageRead
        )
    }
}