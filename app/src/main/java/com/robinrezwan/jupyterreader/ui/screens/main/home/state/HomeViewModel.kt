package com.robinrezwan.jupyterreader.ui.screens.main.home.state

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.robinrezwan.jupyterreader.data.database.JupyterToolDatabase
import com.robinrezwan.jupyterreader.data.repository.RecentFilesRepository
import com.robinrezwan.jupyterreader.data.repository.StarredFilesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeViewModel(
    private val recentFilesRepository: RecentFilesRepository,
    private val starredFilesRepository: StarredFilesRepository,
) : ViewModel() {
    private val _uiState = mutableStateOf(HomeUiState())
    val uiState: State<HomeUiState> = _uiState

    private var getRecentFilesJob: Job? = null
    private var getStarredFilesJob: Job? = null

    init {
        getRecentFiles()
        getStarredFiles()
    }

    private fun getRecentFiles() {
        getRecentFilesJob?.cancel()

        getRecentFilesJob = recentFilesRepository.getAllRecentFiles().onEach { recentFiles ->
            _uiState.value = _uiState.value.copy(recentFiles = recentFiles)
        }.launchIn(viewModelScope)
    }

    private fun getStarredFiles() {
        getStarredFilesJob?.cancel()

        getStarredFilesJob = starredFilesRepository.getAllStarredFiles().onEach { starredFiles ->
            _uiState.value = _uiState.value.copy(starredFiles = starredFiles)
        }.launchIn(viewModelScope)
    }

    fun expandMenu() {
        _uiState.value = _uiState.value.copy(menuExpanded = true)
    }

    fun collapseMenu() {
        _uiState.value = _uiState.value.copy(menuExpanded = false)
    }

    fun startLoading() {
        _uiState.value = _uiState.value.copy(loading = true)
    }

    fun finishLoading() {
        _uiState.value = _uiState.value.copy(loading = false)
    }
}

class HomeViewModelFactory(private val jupyterToolDatabase: JupyterToolDatabase) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(
            recentFilesRepository = RecentFilesRepository(jupyterToolDatabase.recentFileDao()),
            starredFilesRepository = StarredFilesRepository(jupyterToolDatabase.starredFileDao()),
        ) as T
    }
}
