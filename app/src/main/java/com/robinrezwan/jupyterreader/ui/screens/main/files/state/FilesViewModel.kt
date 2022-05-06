package com.robinrezwan.jupyterreader.ui.screens.main.files.state

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.robinrezwan.jupyterreader.data.repository.AllFilesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class FilesViewModel(
    private val allFilesRepository: AllFilesRepository
) : ViewModel() {
    private val _uiState = mutableStateOf(FilesUiState())
    val uiState: State<FilesUiState> = _uiState

    private var getAllFilesJob: Job? = null

    init {
        getAllFiles()
    }

    private fun getAllFiles() {
        getAllFilesJob?.cancel()

        getAllFilesJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(files = allFilesRepository.getAllFiles())
        }
    }

    fun startLoading() {
        _uiState.value = _uiState.value.copy(loading = true)
    }

    fun finishLoading() {
        _uiState.value = _uiState.value.copy(loading = false)
    }
}

class FilesViewModelFactory(private val externalFilesDirs: Array<File>) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FilesViewModel(
            allFilesRepository = AllFilesRepository(externalFilesDirs)
        ) as T
    }
}
