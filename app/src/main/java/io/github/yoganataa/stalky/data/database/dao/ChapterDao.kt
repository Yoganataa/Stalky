package io.github.yoganataa.stalky.data.database.dao

import androidx.room.*
import io.github.yoganataa.stalky.data.database.entities.ChapterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapters WHERE mangaId = :mangaId ORDER BY chapterNumber DESC")
    fun getChaptersByManga(mangaId: String): Flow<List<ChapterEntity>>
    
    @Query("SELECT * FROM chapters WHERE id = :id")
    suspend fun getChapterById(id: String): ChapterEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: ChapterEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapterList(chapters: List<ChapterEntity>)
    
    @Update
    suspend fun updateChapter(chapter: ChapterEntity)
    
    @Query("UPDATE chapters SET read = :read WHERE id = :chapterId")
    suspend fun markAsRead(chapterId: String, read: Boolean)
    
    @Query("UPDATE chapters SET lastPageRead = :page WHERE id = :chapterId")
    suspend fun updateLastPageRead(chapterId: String, page: Int)
    
    @Query("DELETE FROM chapters WHERE mangaId = :mangaId")
    suspend fun deleteChaptersByManga(mangaId: String)
}