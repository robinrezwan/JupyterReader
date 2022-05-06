package com.robinrezwan.jupyterreader.ui.screens.main.search.state

import java.io.File

data class SearchUiState(
    var files: List<File> = emptyList(),
    var loading: Boolean = false,
)
