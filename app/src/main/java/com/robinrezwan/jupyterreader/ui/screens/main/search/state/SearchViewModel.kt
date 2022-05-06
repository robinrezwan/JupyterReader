package com.robinrezwan.jupyterreader.ui.screens.main.search.state

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.robinrezwan.jupyterreader.data.repository.AllFilesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class SearchViewModel(
    private val allFilesRepository: AllFilesRepository
) : ViewModel() {
    private val _uiState = mutableStateOf(SearchUiState())
    val uiState: State<SearchUiState> = _uiState

    private var getSearchFilesJob: Job? = null

    fun searchFiles(keyword: String) {
        if (keyword.isEmpty()) {
            _uiState.value = _uiState.value.copy(files = emptyList())
            return
        }

        startLoading()

        getSearchFilesJob?.cancel()

        getSearchFilesJob = viewModelScope.launch {
            val searchedFiles = allFilesRepository.getAllFiles().filter {
                it.isFile && it.name.contains(keyword, ignoreCase = true)
            }.sortedWith(
                compareBy(
                    { it.name.indexOf(keyword, ignoreCase = true) },
                    { it.name },
                )
            )

            _uiState.value = _uiState.value.copy(files = searchedFiles)

            finishLoading()
        }
    }

    fun startLoading() {
        _uiState.value = _uiState.value.copy(loading = true)
    }

    fun finishLoading() {
        _uiState.value = _uiState.value.copy(loading = false)
    }
}

class SearchViewModelFactory(private val externalFilesDirs: Array<File>) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchViewModel(
            allFilesRepository = AllFilesRepository(externalFilesDirs)
        ) as T
    }
}
