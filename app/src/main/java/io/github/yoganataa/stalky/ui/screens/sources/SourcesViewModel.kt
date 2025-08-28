package io.github.yoganataa.stalky.ui.screens.sources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.yoganataa.stalky.domain.models.Source
import io.github.yoganataa.stalky.domain.repository.SourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SourcesViewModel @Inject constructor(
    private val sourceRepository: SourceRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SourcesUiState())
    val uiState: StateFlow<SourcesUiState> = _uiState.asStateFlow()
    
    init {
        loadSources()
    }
    
    private fun loadSources() {
        viewModelScope.launch {
            sourceRepository.getAllSources().collect { sources ->
                _uiState.update { it.copy(sources = sources) }
            }
        }
    }
    
    fun toggleSource(sourceId: String) {
        viewModelScope.launch {
            val source = sourceRepository.getSource(sourceId)
            source?.let {
                val updated = it.copy(isEnabled = !it.isEnabled)
                sourceRepository.updateSource(updated)
            }
        }
    }
    
    fun addSource(source: Source) {
        viewModelScope.launch {
            try {
                sourceRepository.installSource(source)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun deleteSource(sourceId: String) {
        viewModelScope.launch {
            try {
                sourceRepository.uninstallSource(sourceId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}

data class SourcesUiState(
    val sources: List<Source> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)