package com.robinrezwan.jupyterreader.ui.screens.main.files.state

import java.io.File

data class FilesUiState(
    var files: List<File> = emptyList(),
    var loading: Boolean = false,
)
