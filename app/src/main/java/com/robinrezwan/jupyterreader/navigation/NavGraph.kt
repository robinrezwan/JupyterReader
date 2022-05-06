package com.robinrezwan.jupyterreader.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.robinrezwan.jupyterreader.ui.screens.about.AboutScreen
import com.robinrezwan.jupyterreader.ui.screens.main.MainScreen
import com.robinrezwan.jupyterreader.ui.screens.notebook.NotebookScreen

@Composable
fun NavGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Route.Main,
    ) {
        composable(Route.Main) {
            MainScreen(navController = navController)
        }

        composable(Route.Notebook + "/{notebookPathEncoded}") {
            val notebookPathEncoded = it.arguments!!.getString("notebookPathEncoded")!!

            NotebookScreen(
                navController = navController,
                notebookPathEncoded = notebookPathEncoded,
            )
        }

        composable(Route.About) {
            AboutScreen(navController = navController)
        }
    }
}

object Route {
    const val Main = "main"
    const val Notebook = "notebook"
    const val About = "about"
}
