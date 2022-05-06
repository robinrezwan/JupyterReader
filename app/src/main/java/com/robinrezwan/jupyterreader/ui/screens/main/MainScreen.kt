package com.robinrezwan.jupyterreader.ui.screens.main

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.robinrezwan.jupyterreader.ui.screens.main.files.FilesPage
import com.robinrezwan.jupyterreader.ui.screens.main.home.HomePage
import com.robinrezwan.jupyterreader.ui.screens.main.search.SearchPage
import com.robinrezwan.jupyterreader.util.CustomIcons
import com.robinrezwan.jupyterreader.util.NotebookConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalPagerApi::class
)
@Composable
fun MainScreen(
    navController: NavHostController,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    AppNavDrawer(
        drawerState = drawerState,
        navController = navController,
    ) {
        Scaffold(
            modifier = Modifier.systemBarsPadding(),
            bottomBar = {
                val navBarItems = listOf(
                    BottomNavBarItem(
                        icon = CustomIcons.home(),
                        label = "Home",
                    ),
                    BottomNavBarItem(
                        icon = CustomIcons.files(),
                        label = "Files",
                    ),
                    BottomNavBarItem(
                        icon = CustomIcons.search(),
                        label = "Search",
                    ),
                )

                BottomNavBar(
                    items = navBarItems,
                    selectedItemIndex = pagerState.currentPage,
                    onSelectItem = { itemIndex ->
                        coroutineScope.launch {
                            pagerState.scrollToPage(itemIndex)
                        }
                    },
                )
            },
        ) { paddingValues ->
            HorizontalPager(
                modifier = Modifier.padding(paddingValues),
                count = 3,
                state = pagerState,
                userScrollEnabled = false,
            ) { pageIndex ->
                when (pageIndex) {
                    0 -> HomePage(
                        navController = navController,
                        onClickNavigationIcon = {
                            coroutineScope.launch { drawerState.open() }
                        }
                    )
                    1 -> FilesPage(
                        navController = navController,
                        onClickNavigationIcon = {
                            coroutineScope.launch { drawerState.open() }
                        }
                    )
                    2 -> SearchPage(
                        navController = navController,
                        onClickNavigationIcon = {
                            coroutineScope.launch { drawerState.open() }
                        }
                    )
                }
            }
        }
    }

    // Checking storage access permission
    StoragePermissionDialog(
        onPermissionGranted = {
            Toast.makeText(
                context,
                "Permission granted",
                Toast.LENGTH_SHORT,
            ).show()

            // TODO: Refresh app to load files
        }
    )

    // Initializing notebook converter
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            NotebookConverter.initialize(context)
        }
    }
}
