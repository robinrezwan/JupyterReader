package com.robinrezwan.jupyterreader.util

import android.content.Context
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavHostController
import com.robinrezwan.jupyterreader.navigation.Route
import com.robinrezwan.jupyterreader.util.Constants.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object NotebookViewer {
    fun view(
        context: Context,
        notebookFile: File,
        navController: NavHostController,
        onFinishedLoading: () -> Unit,
    ) {
        val notebookPathEncoded =
            Base64.encodeToString(notebookFile.canonicalPath.toByteArray(), Base64.URL_SAFE)
        val notebookHtmlCacheDir = File(context.cacheDir.canonicalPath + "/notebooks")

        if (!notebookHtmlCacheDir.exists()) {
            notebookHtmlCacheDir.mkdir()
        }

        val notebookHtmlCachePath = notebookHtmlCacheDir.canonicalPath + "/${notebookPathEncoded}" +
                notebookFile.lastModified() + ".html"
        val notebookHtmlCacheFile = File(notebookHtmlCachePath)

        if (!notebookHtmlCacheFile.exists()) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val notebookString = notebookFile.readText()
                    val notebookHtmlString =
                        NotebookConverter.convertToHtml(context, notebookString)
                    notebookHtmlCacheFile.writeText(notebookHtmlString)

                    onFinishedLoading()

                    navController.navigate(Route.Notebook + "/$notebookPathEncoded") {
                        launchSingleTop = true
                    }
                } catch (exception: Exception) {
                    Log.e(TAG, "view: $exception")
                    onFinishedLoading()

                    Toast.makeText(
                        context,
                        "Notebook conversion failed",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
        } else {
            onFinishedLoading()

            navController.navigate(Route.Notebook + "/$notebookPathEncoded") {
                launchSingleTop = true
            }
        }

    }
}
