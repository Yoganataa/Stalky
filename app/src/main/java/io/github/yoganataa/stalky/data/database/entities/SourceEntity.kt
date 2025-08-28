package io.github.yoganataa.stalky.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.yoganataa.stalky.domain.models.Source

@Entity(tableName = "sources")
data class SourceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val baseUrl: String,
    val lang: String,
    val isNsfw: Boolean,
    val supportsLatest: Boolean,
    val scriptVersion: String,
    val scriptContent: String,
    val isEnabled: Boolean
) {
    fun toDomainModel(): Source = Source(
        id = id,
        name = name,
        baseUrl = baseUrl,
        lang = lang,
        isNsfw = isNsfw,
        supportsLatest = supportsLatest,
        scriptVersion = scriptVersion,
        scriptContent = scriptContent,
        isEnabled = isEnabled
    )
    
    companion object {
        fun fromDomainModel(source: Source): SourceEntity = SourceEntity(
            id = source.id,
            name = source.name,
            baseUrl = source.baseUrl,
            lang = source.lang,
            isNsfw = source.isNsfw,
            supportsLatest = source.supportsLatest,
            scriptVersion = source.scriptVersion,
            scriptContent = source.scriptContent,
            isEnabled = source.isEnabled
        )
    }
}