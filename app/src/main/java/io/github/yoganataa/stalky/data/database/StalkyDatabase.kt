package io.github.yoganataa.stalky.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import io.github.yoganataa.stalky.data.database.dao.*
import io.github.yoganataa.stalky.data.database.entities.*

@Database(
    entities = [
        SourceEntity::class,
        MangaEntity::class,
        ChapterEntity::class,
        FavoriteEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class StalkyDatabase : RoomDatabase() {
    abstract fun sourceDao(): SourceDao
    abstract fun mangaDao(): MangaDao
    abstract fun chapterDao(): ChapterDao
    abstract fun favoriteDao(): FavoriteDao
    
    companion object {
        fun create(context: Context): StalkyDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                StalkyDatabase::class.java,
                "stalky_database"
            ).build()
        }
    }
}