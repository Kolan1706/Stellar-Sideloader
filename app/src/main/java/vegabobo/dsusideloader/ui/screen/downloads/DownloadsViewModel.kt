package vegabobo.dsusideloader.ui.screen.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vegabobo.dsusideloader.model.StellarGsiFile
import vegabobo.dsusideloader.util.SourceForgeRepository
import javax.inject.Inject

data class DownloadsUiState(
    val files: List<StellarGsiFile> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedFsType: String? = null,
)

@HiltViewModel
class DownloadsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DownloadsUiState())
    val uiState: StateFlow<DownloadsUiState> = _uiState.asStateFlow()

    private var allFiles: List<StellarGsiFile> = emptyList()

    init {
        loadFiles()
    }

    fun loadFiles() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val files = SourceForgeRepository.fetchGsiFiles()
                allFiles = files
                _uiState.value = _uiState.value.copy(
                    files = filterFiles(files),
                    isLoading = false,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load: ${e.message}",
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        updateFilteredList()
    }

    fun toggleFsTypeFilter(fsType: String) {
        val current = _uiState.value.selectedFsType
        _uiState.value = _uiState.value.copy(
            selectedFsType = if (current == fsType) null else fsType,
        )
        updateFilteredList()
    }

    private fun updateFilteredList() {
        _uiState.value = _uiState.value.copy(
            files = filterFiles(allFiles),
        )
    }

    private fun filterFiles(files: List<StellarGsiFile>): List<StellarGsiFile> {
        val state = _uiState.value
        var result = files

        state.selectedFsType?.let { fs ->
            result = result.filter { it.fsType.equals(fs, ignoreCase = true) }
        }

        if (state.searchQuery.isNotBlank()) {
            result = result.filter {
                it.gsiName.contains(state.searchQuery, ignoreCase = true)
            }
        }

        return result
    }
}
