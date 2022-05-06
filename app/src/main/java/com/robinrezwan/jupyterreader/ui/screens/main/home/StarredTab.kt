package com.robinrezwan.jupyterreader.ui.screens.main.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.robinrezwan.jupyterreader.data.model.StarredFile
import com.robinrezwan.jupyterreader.ui.components.NotebookListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StarredTab(
    items: List<StarredFile>,
    onClickItem: (itemClicked: StarredFile) -> Unit,
) {
    Scaffold { // Without the scaffold, throws an error when changing tabs
        if (items.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                items(count = items.size) { index ->
                    val starredFile = items[index]

                    NotebookListItem(
                        fileName = starredFile.file.name,
                        fileLength = starredFile.file.length(),
                        dateTime = starredFile.timeStarred,
                        onClick = { onClickItem(starredFile) },
                    )
                    if (index < items.size - 1) {
                        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    Text(text = "No starred files")
                }
            }
        }
    }
}
