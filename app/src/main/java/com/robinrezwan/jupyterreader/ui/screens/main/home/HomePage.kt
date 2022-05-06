package com.robinrezwan.jupyterreader.ui.screens.main.home

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.robinrezwan.jupyterreader.R
import com.robinrezwan.jupyterreader.data.database.JupyterToolDatabase
import com.robinrezwan.jupyterreader.ui.screens.main.home.state.HomeViewModel
import com.robinrezwan.jupyterreader.ui.screens.main.home.state.HomeViewModelFactory
import com.robinrezwan.jupyterreader.util.CustomIcons
import com.robinrezwan.jupyterreader.util.NotebookViewer
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalPagerApi::class
)
@Composable
fun HomePage(
    navController: NavHostController,
    onClickNavigationIcon: () -> Unit,
) {
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec)
    }

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            JupyterToolDatabase.getDatabase(context, coroutineScope)
        )
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = { onClickNavigationIcon() }) {
                        Icon(CustomIcons.menu(), null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FilePickerFAB(
                onFilePicked = { notebookFile ->
                    if (!viewModel.uiState.value.loading) {
                        viewModel.startLoading()

                        NotebookViewer.view(
                            context = context,
                            notebookFile = notebookFile,
                            navController = navController,
                            onFinishedLoading = { viewModel.finishLoading() }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val tabBarItems = listOf(
                    ChipTabBarItem(
                        label = "Recent",
                    ),
                    ChipTabBarItem(
                        label = "Starred",
                    ),
                )

                ChipTabBar(
                    items = tabBarItems,
                    selectedItemIndex = pagerState.currentPage,
                    onSelectItem = { itemIndex ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(itemIndex)
                        }
                    }
                )

                Box {
                    IconButton(
                        onClick = { viewModel.expandMenu() }
                    ) {
                        Icon(CustomIcons.more(), null)
                    }
                    DropdownMenu(
                        expanded = viewModel.uiState.value.menuExpanded,
                        onDismissRequest = { viewModel.collapseMenu() },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Clear all recents") },
                            onClick = {
                                viewModel.collapseMenu()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Clear all stars") },
                            onClick = {
                                viewModel.collapseMenu()
                            },
                        )
                    }
                }
            }

            HorizontalPager(
                count = 2,
                state = pagerState,
                userScrollEnabled = true,
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> RecentTab(
                        items = viewModel.uiState.value.recentFiles,
                        onClickItem = { itemClicked ->
                            if (!viewModel.uiState.value.loading) {
                                viewModel.startLoading()

                                NotebookViewer.view(
                                    context = context,
                                    notebookFile = itemClicked.file,
                                    navController = navController,
                                    onFinishedLoading = { viewModel.finishLoading() }
                                )
                            }
                        }
                    )
                    1 -> StarredTab(
                        items = viewModel.uiState.value.starredFiles,
                        onClickItem = { itemClicked ->
                            if (!viewModel.uiState.value.loading) {
                                viewModel.startLoading()

                                NotebookViewer.view(
                                    context = context,
                                    notebookFile = itemClicked.file,
                                    navController = navController,
                                    onFinishedLoading = { viewModel.finishLoading() }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}
