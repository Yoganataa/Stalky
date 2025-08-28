package io.github.yoganataa.stalky.data.sources

import io.github.yoganataa.stalky.domain.models.*

abstract class SourceScript(
    val source: Source
) {
    abstract suspend fun getPopularManga(page: Int): List<Manga>
    abstract suspend fun getLatestManga(page: Int): List<Manga>
    abstract suspend fun searchManga(page: Int, query: String, filters: List<Filter>): List<Manga>
    abstract suspend fun getMangaDetails(manga: Manga): Manga
    abstract suspend fun getChapterList(manga: Manga): List<Chapter>
    abstract suspend fun getPageList(chapter: Chapter): List<Page>
    abstract suspend fun getImageUrl(page: Page): String
}