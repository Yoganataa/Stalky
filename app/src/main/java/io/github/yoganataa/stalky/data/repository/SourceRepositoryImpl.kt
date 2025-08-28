package io.github.yoganataa.stalky.data.repository

import io.github.yoganataa.stalky.data.database.dao.SourceDao
import io.github.yoganataa.stalky.data.database.entities.SourceEntity
import io.github.yoganataa.stalky.domain.models.Source
import io.github.yoganataa.stalky.domain.repository.SourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SourceRepositoryImpl @Inject constructor(
    private val sourceDao: SourceDao
) : SourceRepository {
    
    override fun getAllSources(): Flow<List<Source>> {
        return sourceDao.getAllSources().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override fun getEnabledSources(): Flow<List<Source>> {
        return sourceDao.getEnabledSources().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override suspend fun getSource(id: String): Source? {
        return sourceDao.getSourceById(id)?.toDomainModel()
    }
    
    override suspend fun updateSource(source: Source) {
        sourceDao.updateSource(SourceEntity.fromDomainModel(source))
    }
    
    override suspend fun installSource(source: Source) {
        sourceDao.insertSource(SourceEntity.fromDomainModel(source))
    }
    
    override suspend fun uninstallSource(sourceId: String) {
        sourceDao.deleteSource(sourceId)
    }
    
    override suspend fun updateSourceScript(sourceId: String, scriptContent: String) {
        sourceDao.updateSourceScript(sourceId, scriptContent)
    }
}