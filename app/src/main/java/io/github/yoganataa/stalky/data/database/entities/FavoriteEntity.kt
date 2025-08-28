package io.github.yoganataa.stalky.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val mangaId: String,
    val dateAdded: Long
)