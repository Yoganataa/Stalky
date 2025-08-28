package io.github.yoganataa.stalky.data.sources.builtin

import io.github.yoganataa.stalky.data.sources.SourceScript
import io.github.yoganataa.stalky.domain.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject

class EHentaiSourceImpl @Inject constructor(
    private val client: OkHttpClient
) : SourceScript(
    Source(
        id = "ehentai",
        name = "E-Hentai",
        baseUrl = "https://e-hentai.org",
        isNsfw = true,
        supportsLatest = true,
        scriptVersion = "1.0"
    )
) {
    
    override suspend fun getPopularManga(page: Int): List<Manga> = withContext(Dispatchers.IO) {
        try {
            val url = "${source.baseUrl}/?f_search=&f_srdd=5&f_sr=on&page=${page - 1}"
            val response = client.newCall(Request.Builder().url(url).build()).execute()
            val document = Jsoup.parse(response.body?.string() ?: "")
            
            parseMangaList(document)
        } catch (e: Exception) {
            throw Exception("Failed to load popular manga: ${e.message}")
        }
    }
    
    override suspend fun getLatestManga(page: Int): List<Manga> = withContext(Dispatchers.IO) {
        try {
            val url = "${source.baseUrl}/?page=${page - 1}"
            val response = client.newCall(Request.Builder().url(url).build()).execute()
            val document = Jsoup.parse(response.body?.string() ?: "")
            
            parseMangaList(document)
        } catch (e: Exception) {
            throw Exception("Failed to load latest manga: ${e.message}")
        }
    }
    
    override suspend fun searchManga(page: Int, query: String, filters: List<Filter>): List<Manga> = withContext(Dispatchers.IO) {
        try {
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val url = "${source.baseUrl}/?f_search=$encodedQuery&page=${page - 1}"
            val response = client.newCall(Request.Builder().url(url).build()).execute()
            val document = Jsoup.parse(response.body?.string() ?: "")
            
            parseMangaList(document)
        } catch (e: Exception) {
            throw Exception("Failed to search manga: ${e.message}")
        }
    }
    
    override suspend fun getMangaDetails(manga: Manga): Manga = withContext(Dispatchers.IO) {
        try {
            val response = client.newCall(Request.Builder().url("${source.baseUrl}${manga.url}").build()).execute()
            val document = Jsoup.parse(response.body?.string() ?: "")
            
            manga.copy(
                title = document.select("#gn").text().takeIf { it.isNotBlank() } ?: manga.title,
                thumbnailUrl = extractThumbnailUrl(document),
                description = buildDescription(document),
                genre = document.select("#gdc div").text().takeIf { it.isNotBlank() },
                status = Manga.COMPLETED,
                initialized = true
            )
        } catch (e: Exception) {
            throw Exception("Failed to get manga details: ${e.message}")
        }
    }
    
    override suspend fun getChapterList(manga: Manga): List<Chapter> {
        // E-Hentai galleries are single-chapter
        return listOf(
            Chapter(
                id = "${manga.id}_chapter",
                mangaId = manga.id,
                url = manga.url,
                name = "Chapter",
                chapterNumber = 1f,
                dateUpload = System.currentTimeMillis()
            )
        )
    }
    
    override suspend fun getPageList(chapter: Chapter): List<Page> = withContext(Dispatchers.IO) {
        try {
            val pages = mutableListOf<Page>()
            var pageNum = 0
            var hasNextPage = true
            
            while (hasNextPage) {
                val url = if (pageNum == 0) {
                    "${source.baseUrl}${chapter.url}"
                } else {
                    "${source.baseUrl}${chapter.url}?p=$pageNum"
                }
                
                val response = client.newCall(Request.Builder().url(url).build()).execute()
                val document = Jsoup.parse(response.body?.string() ?: "")
                
                // Extract page URLs
                document.select("#gdt a").forEach { element ->
                    pages.add(
                        Page(
                            index = pages.size,
                            url = element.attr("href")
                        )
                    )
                }
                
                // Check for next page
                hasNextPage = document.select("a[onclick=return false]").any { 
                    it.text() == ">" 
                }
                pageNum++
            }
            
            pages
        } catch (e: Exception) {
            throw Exception("Failed to get page list: ${e.message}")
        }
    }
    
    override suspend fun getImageUrl(page: Page): String = withContext(Dispatchers.IO) {
        try {
            val response = client.newCall(Request.Builder().url(page.url).build()).execute()
            val document = Jsoup.parse(response.body?.string() ?: "")
            
            document.select("#img").attr("src").takeIf { it.isNotBlank() }
                ?: throw Exception("Image URL not found")
        } catch (e: Exception) {
            throw Exception("Failed to get image URL: ${e.message}")
        }
    }
    
    private fun parseMangaList(document: Document): List<Manga> {
        return document.select("table.itg td.glname").mapNotNull { element ->
            try {
                val linkElement = element.selectFirst("a")
                val title = linkElement?.selectFirst(".glink")?.text() ?: return@mapNotNull null
                val url = linkElement.attr("href")
                val thumbnailUrl = element.parent()?.selectFirst(".glthumb img")?.let { img ->
                    img.attr("data-src").takeIf { it.isNotBlank() } ?: img.attr("src")
                }
                
                Manga(
                    id = extractGalleryId(url),
                    sourceId = source.id,
                    url = normalizeUrl(url),
                    title = title,
                    thumbnailUrl = thumbnailUrl
                )
            } catch (e: Exception) {
                null
            }
        }
    }
    
    private fun extractThumbnailUrl(document: Document): String? {
        return document.select("#gd1 div").attr("style").let { style ->
            if (style.contains("url(")) {
                style.substring(style.indexOf('(') + 1, style.lastIndexOf(')'))
            } else null
        }
    }
    
    private fun buildDescription(document: Document): String {
        val builder = StringBuilder()
        
        // Add basic info
        document.select("#gdd tr").forEach { row ->
            val label = row.selectFirst(".gdt1")?.text()?.removeSuffix(":")
            val value = row.selectFirst(".gdt2")?.text()
            
            if (label != null && value != null) {
                builder.appendLine("$label: $value")
            }
        }
        
        // Add tags
        val tags = mutableMapOf<String, List<String>>()
        document.select("#taglist tr").forEach { row ->
            val namespace = row.selectFirst(".tc")?.text()?.removeSuffix(":")
            val tagElements = row.select("div").map { it.text().trim() }
            
            if (namespace != null && tagElements.isNotEmpty()) {
                tags[namespace] = tagElements
            }
        }
        
        if (tags.isNotEmpty()) {
            builder.appendLine("\nTags:")
            tags.forEach { (namespace, tagList) ->
                builder.appendLine("▪ $namespace: ${tagList.joinToString(", ")}")
            }
        }
        
        return builder.toString().trim()
    }
    
    private fun extractGalleryId(url: String): String {
        return url.split("/").let { parts ->
            parts.getOrNull(parts.indexOf("g") + 1) ?: url.hashCode().toString()
        }
    }
    
    private fun normalizeUrl(url: String): String {
        return if (url.startsWith("/")) url else "/$url"
    }
}