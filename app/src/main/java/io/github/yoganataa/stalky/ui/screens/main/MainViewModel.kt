package io.github.yoganataa.stalky.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.yoganataa.stalky.domain.models.Manga
import io.github.yoganataa.stalky.domain.models.Source
import io.github.yoganataa.stalky.domain.repository.MangaRepository
import io.github.yoganataa.stalky.domain.repository.SourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sourceRepository: SourceRepository,
    private val mangaRepository: MangaRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    init {
        loadSources()
    }
    
    private fun loadSources() {
        viewModelScope.launch {
            sourceRepository.getEnabledSources().collect { sources ->
                _uiState.update { 
                    it.copy(
                        sources = sources,
                        selectedSourceIndex = if (sources.isNotEmpty()) {
                            // Keep the current index if it's valid, otherwise select the first source
                            if (it.selectedSourceIndex in sources.indices) {
                                it.selectedSourceIndex
                            } else {
                                0
                            }
                        } else {
                            0 // Reset to 0 when no sources
                        }
                    )
                }
                
                // Load manga only if there are sources available
                if (sources.isNotEmpty()) {
                    val selectedIndex = _uiState.value.selectedSourceIndex
                    val sourceToLoad = if (selectedIndex in sources.indices) {
                        sources[selectedIndex]
                    } else {
                        sources[0].also {
                            // Update the selected index if we're falling back to the first source
                            _uiState.update { state -> state.copy(selectedSourceIndex = 0) }
                        }
                    }
                    loadMangaFromSource(sourceToLoad)
                } else {
                    // Clear manga list when no sources are available
                    _uiState.update { it.copy(manga = emptyList(), isLoading = false) }
                }
            }
        }
    }
    
    fun selectSource(index: Int) {
        val sources = _uiState.value.sources
        if (index in sources.indices) {
            _uiState.update { it.copy(selectedSourceIndex = index) }
            loadMangaFromSource(sources[index])
        }
    }
    
    private fun loadMangaFromSource(source: Source) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val manga = mangaRepository.getPopularManga(source.id, 1)
                _uiState.update { 
                    it.copy(
                        manga = manga, 
                        isLoading = false
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = e.message, 
                        isLoading = false
                    ) 
                }
            }
        }
    }
    
    fun refreshContent() {
        val currentSource = _uiState.value.sources.getOrNull(_uiState.value.selectedSourceIndex)
        currentSource?.let { loadMangaFromSource(it) }
    }
}

data class MainUiState(
    val sources: List<Source> = emptyList(),
    val selectedSourceIndex: Int = 0,
    val manga: List<Manga> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)