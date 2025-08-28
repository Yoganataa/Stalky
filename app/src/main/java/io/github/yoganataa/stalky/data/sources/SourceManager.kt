package io.github.yoganataa.stalky.data.sources

import io.github.yoganataa.stalky.data.scripting.ScriptEngine
import io.github.yoganataa.stalky.data.sources.builtin.*
import io.github.yoganataa.stalky.domain.models.Source
import io.github.yoganataa.stalky.domain.repository.SourceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SourceManager @Inject constructor(
    private val sourceRepository: SourceRepository,
    private val scriptEngine: ScriptEngine,
    private val eHentaiSource: EHentaiSourceImpl,
    private val kemonoSource: KemonoSourceImpl,
    private val coomerSource: CoomerSourceImpl
) {
    
    private val builtInSources = mapOf(
        "ehentai" to eHentaiSource,
        "kemono" to kemonoSource,
        "coomer" to coomerSource
    )
    
    suspend fun getSourceScript(sourceId: String): SourceScript {
        return builtInSources[sourceId] 
            ?: createDynamicSource(sourceId)
            ?: throw Exception("Source not found: $sourceId")
    }
    
    private suspend fun createDynamicSource(sourceId: String): SourceScript? {
        val source = sourceRepository.getSource(sourceId) ?: return null
        
        if (source.scriptContent.isBlank()) return null
        
        return DynamicSourceScript(source, scriptEngine)
    }
}