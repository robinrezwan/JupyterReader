package com.robinrezwan.jupyterreader.ui.screens.main.home.state

import com.robinrezwan.jupyterreader.data.model.RecentFile
import com.robinrezwan.jupyterreader.data.model.StarredFile

data class HomeUiState(
    var recentFiles: List<RecentFile> = emptyList(),
    var starredFiles: List<StarredFile> = emptyList(),
    var menuExpanded: Boolean = false,
    var loading: Boolean = false,
)
