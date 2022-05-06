package com.robinrezwan.jupyterreader.ui.screens.notebook.state

import java.io.File

data class NotebookUiState(
    var appBarVisible: Boolean = true,
    var findBarVisible: Boolean = false,
    var menuExpanded: Boolean = false,
    var deleteDialogOpened: Boolean = false,
    var printDialogOpened: Boolean = false,
    var loading: Boolean = true,
    val notebookFile: File? = null,
    var notebookHtmlString: String? = null,
    var activeFindMatch: Int = 0,
    var numberOfFindMatches: Int = 0,
)
