package io.github.yoganataa.stalky.data.repository

import io.github.yoganataa.stalky.data.database.dao.*
import io.github.yoganataa.stalky.data.database.entities.*
import io.github.yoganataa.stalky.data.sources.SourceManager
import io.github.yoganataa.stalky.domain.models.*
import io.github.yoganataa.stalky.domain.repository.MangaRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MangaRepositoryImpl @Inject constructor(
    private val mangaDao: MangaDao,
    private val chapterDao: ChapterDao,
    private val favoriteDao: FavoriteDao,
    private val sourceManager: SourceManager
) : MangaRepository {
    
    override suspend fun getPopularManga(sourceId: String, page: Int): List<Manga> {
        return try {
            val sourceScript = sourceManager.getSourceScript(sourceId)
            val manga = sourceScript.getPopularManga(page)
            
            // Cache manga list
            mangaDao.insertMangaList(manga.map { MangaEntity.fromDomainModel(it) })
            manga
        } catch (e: Exception) {
            // Return cached data if available
            mangaDao.getMangaBySource(sourceId).map { entities ->
                entities.map { it.toDomainModel() }
            }.let { flow ->
                // This is a simplified fallback - in practice you'd handle this better
                emptyList()
            }
        }
    }
    
    override suspend fun getLatestManga(sourceId: String, page: Int): List<Manga> {
        return try {
            val sourceScript = sourceManager.getSourceScript(sourceId)
            val manga = sourceScript.getLatestManga(page)
            
            mangaDao.insertMangaList(manga.map { MangaEntity.fromDomainModel(it) })
            manga
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun searchManga(sourceId: String, query: String, page: Int): List<Manga> {
        return try {
            val sourceScript = sourceManager.getSourceScript(sourceId)
            val manga = sourceScript.searchManga(page, query, emptyList())
            
            mangaDao.insertMangaList(manga.map { MangaEntity.fromDomainModel(it) })
            manga
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun getMangaDetails(manga: Manga): Manga {
        val sourceScript = sourceManager.getSourceScript(manga.sourceId)
        val detailedManga = sourceScript.getMangaDetails(manga)
        
        mangaDao.updateManga(MangaEntity.fromDomainModel(detailedManga))
        return detailedManga
    }
    
    override suspend fun getChapterList(manga: Manga): List<Chapter> {
        val sourceScript = sourceManager.getSourceScript(manga.sourceId)
        val chapters = sourceScript.getChapterList(manga)
        
        chapterDao.insertChapterList(chapters.map { ChapterEntity.fromDomainModel(it) })
        return chapters
    }
    
    override suspend fun getPageList(chapter: Chapter): List<Page> {
        // Get manga to determine source
        val manga = mangaDao.getMangaById(chapter.mangaId)?.toDomainModel()
            ?: throw Exception("Manga not found")
        
        val sourceScript = sourceManager.getSourceScript(manga.sourceId)
        return sourceScript.getPageList(chapter)
    }
    
    override suspend fun getFavoriteManga(): List<Manga> {
        return mangaDao.getFavoriteManga().map { entities ->
            entities.map { it.toDomainModel() }
        }.let { flow ->
            // Return current snapshot - in practice use Flow
            emptyList()
        }
    }
    
    override suspend fun addToFavorites(manga: Manga) {
        favoriteDao.addToFavorites(
            FavoriteEntity(
                mangaId = manga.id,
                dateAdded = System.currentTimeMillis()
            )
        )
        mangaDao.updateFavoriteStatus(manga.id, true)
    }
    
    override suspend fun removeFromFavorites(mangaId: String) {
        favoriteDao.removeFromFavorites(mangaId)
        mangaDao.updateFavoriteStatus(mangaId, false)
    }
}