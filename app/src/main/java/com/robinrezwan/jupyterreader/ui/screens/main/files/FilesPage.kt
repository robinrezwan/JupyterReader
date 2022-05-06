package com.robinrezwan.jupyterreader.ui.screens.main.files

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.robinrezwan.jupyterreader.ui.components.NotebookListItem
import com.robinrezwan.jupyterreader.ui.screens.main.files.state.FilesViewModel
import com.robinrezwan.jupyterreader.ui.screens.main.files.state.FilesViewModelFactory
import com.robinrezwan.jupyterreader.util.CustomIcons
import com.robinrezwan.jupyterreader.util.NotebookViewer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesPage(
    navController: NavHostController,
    onClickNavigationIcon: () -> Unit,
) {
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec)
    }

    val context = LocalContext.current

    val viewModel: FilesViewModel = viewModel(
        factory = FilesViewModelFactory(
            context.getExternalFilesDirs(null)
        )
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Files") },
                navigationIcon = {
                    IconButton(onClick = { onClickNavigationIcon() }) {
                        Icon(CustomIcons.menu(), null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (viewModel.uiState.value.loading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            val filesList = viewModel.uiState.value.files

            if (filesList.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    items(count = filesList.size) { index ->
                        val notebookFile = filesList[index]

                        if (notebookFile.isDirectory) {
                            Text(
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp,
                                ),
                                text = notebookFile.name,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        } else {
                            NotebookListItem(
                                fileName = notebookFile.name,
                                fileLength = notebookFile.length(),
                                dateTime = notebookFile.lastModified(),
                                onClick = {
                                    if (!viewModel.uiState.value.loading) {
                                        viewModel.startLoading()

                                        NotebookViewer.view(
                                            context = context,
                                            notebookFile = notebookFile,
                                            navController = navController,
                                            onFinishedLoading = { viewModel.finishLoading() }
                                        )
                                    }
                                },
                            )
                        }
                        if (index < filesList.size - 1) {
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
                        Text(text = "No files")
                    }
                }
            }
        }
    }
}
