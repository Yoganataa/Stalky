package io.github.yoganataa.stalky.data.database.dao

import androidx.room.*
import io.github.yoganataa.stalky.data.database.entities.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY dateAdded DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(favorite: FavoriteEntity)
    
    @Query("DELETE FROM favorites WHERE mangaId = :mangaId")
    suspend fun removeFromFavorites(mangaId: String)
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE mangaId = :mangaId)")
    suspend fun isFavorite(mangaId: String): Boolean
}