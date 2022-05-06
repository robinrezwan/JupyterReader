package com.robinrezwan.jupyterreader.ui.screens.notebook.state

import android.util.Base64
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.accompanist.systemuicontroller.SystemUiController
import kotlinx.coroutines.launch
import java.io.File

class NotebookViewModel(
    private val notebookPathEncoded: String,
    private val cacheDir: File,
    private val systemUiController: SystemUiController,
) : ViewModel() {
    private val _uiState = mutableStateOf(NotebookUiState())
    val uiState: State<NotebookUiState> = _uiState

    init {
        viewModelScope.launch {
            getNotebookFile()
            getNotebookHtmlString()
        }
    }

    private fun getNotebookFile() {
        val notebookPath = Base64.decode(notebookPathEncoded, Base64.URL_SAFE).decodeToString()
        val notebookFile = File(notebookPath)

        _uiState.value = _uiState.value.copy(notebookFile = notebookFile)
    }

    private fun getNotebookHtmlString() {
        val notebookHtmlCachePath = cacheDir.canonicalPath + "/notebooks" +
                "/${notebookPathEncoded}" + _uiState.value.notebookFile!!.lastModified() + ".html"
        val notebookHtmlCacheFile = File(notebookHtmlCachePath)

        _uiState.value = _uiState.value.copy(
            notebookHtmlString = notebookHtmlCacheFile.readText()
        )
    }

    fun getNotebookShareCacheFile(): File {
        val notebookShareCacheDir = File(cacheDir.canonicalPath + "/share")

        if (!notebookShareCacheDir.exists()) {
            notebookShareCacheDir.mkdir()
        } else {
            notebookShareCacheDir.listFiles()?.forEach { file -> file.delete() }
        }

        val notebookShareCachePath =
            notebookShareCacheDir.canonicalPath + "/" + _uiState.value.notebookFile!!.name
        val notebookShareCacheFile = File(notebookShareCachePath)

        _uiState.value.notebookFile!!.copyTo(notebookShareCacheFile, true)

        return notebookShareCacheFile
    }

    fun deleteNotebookFile() {

    }

    fun toggleFullScreen() {
        if (!_uiState.value.findBarVisible) {
            _uiState.value = _uiState.value.copy(appBarVisible = !_uiState.value.appBarVisible)
            systemUiController.isSystemBarsVisible = _uiState.value.appBarVisible
        }
    }

    fun showFindBar() {
        _uiState.value = _uiState.value.copy(findBarVisible = true)
    }

    fun hideFindBar() {
        _uiState.value = _uiState.value.copy(findBarVisible = false)
    }

    fun expandMenu() {
        _uiState.value = _uiState.value.copy(menuExpanded = true)
    }

    fun collapseMenu() {
        _uiState.value = _uiState.value.copy(menuExpanded = false)
    }

    fun openDeleteDialog() {
        _uiState.value = _uiState.value.copy(deleteDialogOpened = true)
    }

    fun closeDeleteDialog() {
        _uiState.value = _uiState.value.copy(deleteDialogOpened = false)
    }

    fun openPrintDialog() {
        _uiState.value = _uiState.value.copy(printDialogOpened = true)
    }

    fun closePrintDialog() {
        _uiState.value = _uiState.value.copy(printDialogOpened = false)
    }

    fun startLoading() {
        _uiState.value = _uiState.value.copy(loading = true)
    }

    fun finishLoading() {
        _uiState.value = _uiState.value.copy(loading = false)
    }

    fun setFindMatches(activeMatch: Int, numberOfMatches: Int) {
        _uiState.value = _uiState.value.copy(
            activeFindMatch = activeMatch,
            numberOfFindMatches = numberOfMatches,
        )
    }
}

class NotebookViewModelFactory(
    private val notebookPathEncoded: String,
    private val cacheDir: File,
    private val systemUiController: SystemUiController,
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NotebookViewModel(
            notebookPathEncoded = notebookPathEncoded,
            cacheDir = cacheDir,
            systemUiController = systemUiController,
        ) as T
    }
}
