package io.github.yoganataa.stalky.data.sources.builtin

import io.github.yoganataa.stalky.data.sources.SourceScript
import io.github.yoganataa.stalky.domain.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class KemonoSourceImpl @Inject constructor(
    private val client: OkHttpClient
) : SourceScript(
    Source(
        id = "kemono",
        name = "Kemono Party",
        baseUrl = "https://kemono.party",
        isNsfw = true,
        supportsLatest = true,
        scriptVersion = "1.0"
    )
) {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    override suspend fun getPopularManga(page: Int): List<Manga> = withContext(Dispatchers.IO) {
        try {
            getCreators(sortBy = "favorited", page = page)
        } catch (e: Exception) {
            throw Exception("Failed to load popular creators: ${e.message}")
        }
    }
    
    override suspend fun getLatestManga(page: Int): List<Manga> = withContext(Dispatchers.IO) {
        try {
            getCreators(sortBy = "updated", page = page)
        } catch (e: Exception) {
            throw Exception("Failed to load latest creators: ${e.message}")
        }
    }
    
    override suspend fun searchManga(page: Int, query: String, filters: List<Filter>): List<Manga> = withContext(Dispatchers.IO) {
        try {
            getCreators(query = query, page = page)
        } catch (e: Exception) {
            throw Exception("Failed to search creators: ${e.message}")
        }
    }
    
    override suspend fun getMangaDetails(manga: Manga): Manga = withContext(Dispatchers.IO) {
        // Kemono creators don't have additional details to fetch
        manga.copy(initialized = true)
    }
    
    override suspend fun getChapterList(manga: Manga): List<Chapter> = withContext(Dispatchers.IO) {
        try {
            val url = "${source.baseUrl}/api/v1${manga.url}/posts"
            val response = client.newCall(Request.Builder().url(url).build()).execute()
            val postsJson = response.body?.string() ?: "[]"
            
            // Parse JSON and convert to chapters
            val posts = parseKemonoPosts(postsJson)
            
            posts.mapIndexed { index, post ->
                Chapter(
                    id = post.id,
                    mangaId = manga.id,
                    url = "${manga.url}/post/${post.id}",
                    name = post.title.ifEmpty { "Post ${index + 1}" },
                    dateUpload = post.published,
                    chapterNumber = (posts.size - index).toFloat()
                )
            }
        } catch (e: Exception) {
            throw Exception("Failed to get chapter list: ${e.message}")
        }
    }
    
    override suspend fun getPageList(chapter: Chapter): List<Page> = withContext(Dispatchers.IO) {
        try {
            val url = "${source.baseUrl}/api/v1${chapter.url}"
            val response = client.newCall(Request.Builder().url(url).build()).execute()
            val postJson = response.body?.string() ?: "{}"
            
            val post = parseKemonoPost(postJson)
            val images = post.attachments.filter { it.isImage() }
            
            images.mapIndexed { index, attachment ->
                Page(
                    index = index,
                    imageUrl = "${source.baseUrl}/data${attachment.path}"
                )
            }
        } catch (e: Exception) {
            throw Exception("Failed to get page list: ${e.message}")
        }
    }
    
    override suspend fun getImageUrl(page: Page): String {
        return page.imageUrl ?: throw Exception("Image URL not available")
    }
    
    private suspend fun getCreators(sortBy: String = "updated", query: String = "", page: Int = 1): List<Manga> {
        val url = "${source.baseUrl}/api/v1/creators"
        val response = client.newCall(Request.Builder().url(url).build()).execute()
        val creatorsJson = response.body?.string() ?: "[]"
        
        val creators = parseKemonoCreators(creatorsJson)
        
        return creators.filter { creator ->
            if (query.isNotEmpty()) {
                creator.name.contains(query, ignoreCase = true)
            } else {
                true
            }
        }.map { creator ->
            Manga(
                id = creator.id,
                sourceId = source.id,
                url = "/${creator.service}/user/${creator.id}",
                title = creator.name,
                author = creator.service.serviceName(),
                thumbnailUrl = "${source.baseUrl.replace("//", "//img.")}/icons/${creator.service}/${creator.id}",
                description = "Creator from ${creator.service.serviceName()}"
            )
        }
    }
    
    // Parse JSON for creators
    private fun parseKemonoCreators(json: String): List<KemonoCreator> {
        return try {
            val jsonArray = json.trimStart().let { 
                if (it.startsWith("[")) Json.parseToJsonElement(it).jsonArray
                else Json.parseToJsonElement("[]").jsonArray
            }
            
            jsonArray.mapNotNull { element ->
                try {
                    val obj = element.jsonObject
                    KemonoCreator(
                        id = obj["id"]?.jsonPrimitive?.content ?: "",
                        name = obj["name"]?.jsonPrimitive?.content ?: "Unknown Creator",
                        service = obj["service"]?.jsonPrimitive?.content ?: "unknown"
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // Parse JSON for posts
    private fun parseKemonoPosts(json: String): List<KemonoPost> {
        return try {
            val jsonArray = json.trimStart().let { 
                if (it.startsWith("[")) Json.parseToJsonElement(it).jsonArray
                else Json.parseToJsonElement("[]").jsonArray
            }
            
            jsonArray.mapNotNull { element ->
                try {
                    val obj = element.jsonObject
                    KemonoPost(
                        id = obj["id"]?.jsonPrimitive?.content ?: "",
                        title = obj["title"]?.jsonPrimitive?.content ?: "Untitled Post",
                        content = obj["content"]?.jsonPrimitive?.content ?: "",
                        service = obj["service"]?.jsonPrimitive?.content ?: "unknown",
                        published = obj["published"]?.jsonPrimitive?.content?.toLongOrNull() ?: System.currentTimeMillis(),
                        attachments = parseAttachments(obj["attachments"]?.jsonArray ?: JsonArray(emptyList()))
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // Parse attachments from JSON
    private fun parseAttachments(jsonArray: JsonArray): List<KemonoAttachment> {
        return jsonArray.mapNotNull { element ->
            try {
                val obj = element.jsonObject
                KemonoAttachment(
                    name = obj["name"]?.jsonPrimitive?.content,
                    path = obj["path"]?.jsonPrimitive?.content ?: ""
                )
            } catch (e: Exception) {
                null
            }
        }
    }
    
    // Parse a single post
    private fun parseKemonoPost(json: String): KemonoPost {
        return try {
            val obj = Json.parseToJsonElement(json).jsonObject
            KemonoPost(
                id = obj["id"]?.jsonPrimitive?.content ?: "",
                title = obj["title"]?.jsonPrimitive?.content ?: "Untitled Post",
                content = obj["content"]?.jsonPrimitive?.content ?: "",
                service = obj["service"]?.jsonPrimitive?.content ?: "unknown",
                published = obj["published"]?.jsonPrimitive?.content?.toLongOrNull() ?: System.currentTimeMillis(),
                attachments = parseAttachments(obj["attachments"]?.jsonArray ?: JsonArray(emptyList()))
            )
        } catch (e: Exception) {
            KemonoPost("", "", "", "", 0L, emptyList())
        }
    }
    
    private fun String.serviceName(): String = when (this) {
        "fanbox" -> "Pixiv Fanbox"
        "subscribestar" -> "SubscribeStar"
        "dlsite" -> "DLsite"
        "onlyfans" -> "OnlyFans"
        else -> this.replaceFirstChar { it.uppercase() }
    }
}

// Data classes for Kemono API
@Serializable
data class KemonoCreator(
    val id: String,
    val name: String,
    val service: String
)

@Serializable
data class KemonoPost(
    val id: String,
    val title: String,
    val content: String,
    val service: String,
    val published: Long,
    val attachments: List<KemonoAttachment>
)

@Serializable
data class KemonoAttachment(
    val name: String?,
    val path: String
) {
    fun isImage(): Boolean {
        return path.substringAfterLast('.').lowercase() in listOf("png", "jpg", "jpeg", "gif", "webp")
    }
}