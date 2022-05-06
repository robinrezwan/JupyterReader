package com.robinrezwan.jupyterreader.ui.screens.main.search

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.robinrezwan.jupyterreader.ui.components.NotebookListItem
import com.robinrezwan.jupyterreader.ui.screens.main.search.state.SearchViewModel
import com.robinrezwan.jupyterreader.ui.screens.main.search.state.SearchViewModelFactory
import com.robinrezwan.jupyterreader.util.CustomIcons
import com.robinrezwan.jupyterreader.util.NotebookViewer

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun SearchPage(
    navController: NavHostController,
    onClickNavigationIcon: () -> Unit,
) {
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec)
    }

    var text by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current

    val context = LocalContext.current

    val viewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(
            context.getExternalFilesDirs(null)
        )
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Search") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            keyboardController?.hide()
                            onClickNavigationIcon()
                        }
                    ) {
                        Icon(CustomIcons.menu(), null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(52.dp),
                leadingIcon = {
                    Icon(
                        modifier = Modifier.padding(start = 8.dp),
                        imageVector = CustomIcons.search(),
                        contentDescription = null,
                    )
                },
                placeholder = {
                    Text(
                        text = "Search files",
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
                textStyle = MaterialTheme.typography.labelLarge,
                value = text,
                onValueChange = {
                    text = it
                    viewModel.searchFiles(text.trim())
                },
                singleLine = true,
                shape = CircleShape,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { keyboardController?.hide() }
                ),
                trailingIcon = {
                    if (text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                text = ""
                                viewModel.searchFiles(text.trim())
                            }
                        ) {
                            Icon(CustomIcons.close(), null)
                        }
                    }
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                )
            )

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
