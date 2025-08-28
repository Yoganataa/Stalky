package io.github.yoganataa.stalky.data.database.dao

import androidx.room.*
import io.github.yoganataa.stalky.data.database.entities.MangaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaDao {
    @Query("SELECT * FROM manga WHERE sourceId = :sourceId ORDER BY title ASC")
    fun getMangaBySource(sourceId: String): Flow<List<MangaEntity>>
    
    @Query("SELECT * FROM manga WHERE id = :id")
    suspend fun getMangaById(id: String): MangaEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManga(manga: MangaEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMangaList(mangaList: List<MangaEntity>)
    
    @Update
    suspend fun updateManga(manga: MangaEntity)
    
    @Query("DELETE FROM manga WHERE id = :id")
    suspend fun deleteManga(id: String)
    
    @Query("SELECT * FROM manga WHERE favorite = 1 ORDER BY title ASC")
    fun getFavoriteManga(): Flow<List<MangaEntity>>
    
    @Query("UPDATE manga SET favorite = :favorite WHERE id = :mangaId")
    suspend fun updateFavoriteStatus(mangaId: String, favorite: Boolean)
}