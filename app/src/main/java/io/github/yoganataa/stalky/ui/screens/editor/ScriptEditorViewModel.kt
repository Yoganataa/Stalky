package io.github.yoganataa.stalky.ui.screens.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.yoganataa.stalky.data.scripting.ScriptEngine
import io.github.yoganataa.stalky.domain.models.Source
import io.github.yoganataa.stalky.domain.repository.SourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScriptEditorViewModel @Inject constructor(
    private val sourceRepository: SourceRepository,
    private val scriptEngine: ScriptEngine
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ScriptEditorUiState())
    val uiState: StateFlow<ScriptEditorUiState> = _uiState.asStateFlow()
    
    fun loadSource(sourceId: String) {
        viewModelScope.launch {
            try {
                val source = sourceRepository.getSource(sourceId)
                _uiState.update {
                    it.copy(
                        source = source,
                        scriptContent = source?.scriptContent ?: ""
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun updateScript(content: String) {
        _uiState.update { 
            it.copy(
                scriptContent = content,
                isValid = false,
                validationError = null
            ) 
        }
    }
    
    fun validateScript() {
        viewModelScope.launch {
            _uiState.update { it.copy(isValidating = true) }
            
            try {
                val result = scriptEngine.validateScript(_uiState.value.scriptContent)
                _uiState.update {
                    it.copy(
                        isValidating = false,
                        isValid = result.isValid,
                        validationError = result.error
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isValidating = false,
                        isValid = false,
                        validationError = e.message
                    )
                }
            }
        }
    }
    
    fun saveScript() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            
            try {
                val source = _uiState.value.source
                if (source != null) {
                    sourceRepository.updateSourceScript(source.id, _uiState.value.scriptContent)
                    _uiState.update { it.copy(isSaving = false) }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSaving = false,
                        error = e.message
                    ) 
                }
            }
        }
    }
}

data class ScriptEditorUiState(
    val source: Source? = null,
    val scriptContent: String = "",
    val isValid: Boolean = false,
    val isValidating: Boolean = false,
    val isSaving: Boolean = false,
    val validationError: String? = null,
    val error: String? = null
)