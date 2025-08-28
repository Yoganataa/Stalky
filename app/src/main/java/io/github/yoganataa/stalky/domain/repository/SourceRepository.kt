package io.github.yoganataa.stalky.domain.repository

import io.github.yoganataa.stalky.domain.models.Source
import kotlinx.coroutines.flow.Flow

interface SourceRepository {
    fun getAllSources(): Flow<List<Source>>
    fun getEnabledSources(): Flow<List<Source>>
    suspend fun getSource(id: String): Source?
    suspend fun updateSource(source: Source)
    suspend fun installSource(source: Source)
    suspend fun uninstallSource(sourceId: String)
    suspend fun updateSourceScript(sourceId: String, scriptContent: String)
}