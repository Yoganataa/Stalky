package io.github.yoganataa.stalky.domain.repository

import io.github.yoganataa.stalky.domain.models.Manga
import io.github.yoganataa.stalky.domain.models.Chapter
import io.github.yoganataa.stalky.domain.models.Page

interface MangaRepository {
    suspend fun getPopularManga(sourceId: String, page: Int): List<Manga>
    suspend fun getLatestManga(sourceId: String, page: Int): List<Manga>
    suspend fun searchManga(sourceId: String, query: String, page: Int): List<Manga>
    suspend fun getMangaDetails(manga: Manga): Manga
    suspend fun getChapterList(manga: Manga): List<Chapter>
    suspend fun getPageList(chapter: Chapter): List<Page>
    suspend fun getFavoriteManga(): List<Manga>
    suspend fun addToFavorites(manga: Manga)
    suspend fun removeFromFavorites(mangaId: String)
}