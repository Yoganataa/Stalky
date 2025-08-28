package io.github.yoganataa.stalky.data.database.dao

import androidx.room.*
import io.github.yoganataa.stalky.data.database.entities.SourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SourceDao {
    @Query("SELECT * FROM sources ORDER BY name ASC")
    fun getAllSources(): Flow<List<SourceEntity>>
    
    @Query("SELECT * FROM sources WHERE isEnabled = 1 ORDER BY name ASC")
    fun getEnabledSources(): Flow<List<SourceEntity>>
    
    @Query("SELECT * FROM sources WHERE id = :id")
    suspend fun getSourceById(id: String): SourceEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSource(source: SourceEntity)
    
    @Update
    suspend fun updateSource(source: SourceEntity)
    
    @Query("DELETE FROM sources WHERE id = :sourceId")
    suspend fun deleteSource(sourceId: String)
    
    @Query("UPDATE sources SET scriptContent = :scriptContent WHERE id = :sourceId")
    suspend fun updateSourceScript(sourceId: String, scriptContent: String)
}