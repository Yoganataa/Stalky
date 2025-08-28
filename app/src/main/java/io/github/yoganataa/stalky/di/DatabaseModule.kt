package io.github.yoganataa.stalky.di

import android.content.Context
import io.github.yoganataa.stalky.data.database.StalkyDatabase
import io.github.yoganataa.stalky.data.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StalkyDatabase {
        return StalkyDatabase.create(context)
    }
    
    @Provides
    fun provideSourceDao(database: StalkyDatabase): SourceDao {
        return database.sourceDao()
    }
    
    @Provides
    fun provideMangaDao(database: StalkyDatabase): MangaDao {
        return database.mangaDao()
    }
    
    @Provides
    fun provideChapterDao(database: StalkyDatabase): ChapterDao {
        return database.chapterDao()
    }
    
    @Provides
    fun provideFavoriteDao(database: StalkyDatabase): FavoriteDao {
        return database.favoriteDao()
    }
}