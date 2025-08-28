package io.github.yoganataa.stalky.di

import io.github.yoganataa.stalky.data.repository.MangaRepositoryImpl
import io.github.yoganataa.stalky.data.repository.SourceRepositoryImpl
import io.github.yoganataa.stalky.domain.repository.MangaRepository
import io.github.yoganataa.stalky.domain.repository.SourceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    abstract fun bindSourceRepository(
        sourceRepositoryImpl: SourceRepositoryImpl
    ): SourceRepository
    
    @Binds
    abstract fun bindMangaRepository(
        mangaRepositoryImpl: MangaRepositoryImpl
    ): MangaRepository
}