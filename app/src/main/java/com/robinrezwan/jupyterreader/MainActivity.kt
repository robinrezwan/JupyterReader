package com.robinrezwan.jupyterreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.robinrezwan.jupyterreader.navigation.NavGraph
import com.robinrezwan.jupyterreader.ui.theme.JupyterNotebookViewerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JupyterNotebookViewerTheme {
                // System bars
                WindowCompat.setDecorFitsSystemWindows(window, false)

                val systemUiController = rememberSystemUiController()

                val backgroundColor = MaterialTheme.colorScheme.background

                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = backgroundColor,
                        darkIcons = true,
                    )
                }

                // App content
                val navController = rememberNavController()

                NavGraph(navController = navController)
            }
        }
    }
}
