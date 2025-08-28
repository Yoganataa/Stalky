package io.github.yoganataa.stalky.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.yoganataa.stalky.domain.models.Chapter
import io.github.yoganataa.stalky.domain.models.Manga
import io.github.yoganataa.stalky.domain.repository.MangaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangaDetailViewModel @Inject constructor(
    private val mangaRepository: MangaRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MangaDetailUiState())
    val uiState: StateFlow<MangaDetailUiState> = _uiState.asStateFlow()
    
    fun loadMangaDetail(mangaId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Load manga details and chapters
                // This would be implemented based on your repository
                
                _uiState.update { 
                    it.copy(
                        isLoading = false
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message
                    ) 
                }
            }
        }
    }
    
    fun toggleFavorite() {
        val manga = _uiState.value.manga ?: return
        
        viewModelScope.launch {
            try {
                if (manga.favorite) {
                    mangaRepository.removeFromFavorites(manga.id)
                } else {
                    mangaRepository.addToFavorites(manga)
                }
                
                _uiState.update { 
                    it.copy(manga = manga.copy(favorite = !manga.favorite))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}

data class MangaDetailUiState(
    val manga: Manga? = null,
    val chapters: List<Chapter> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)