package com.robinrezwan.jupyterreader.ui.screens.notebook

import android.annotation.SuppressLint
import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Base64
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.robinrezwan.jupyterreader.ui.screens.notebook.state.NotebookViewModel
import com.robinrezwan.jupyterreader.ui.screens.notebook.state.NotebookViewModelFactory
import com.robinrezwan.jupyterreader.util.NotebookConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookScreen(
    navController: NavHostController,
    notebookPathEncoded: String,
) {
    val context = LocalContext.current

    val viewModel: NotebookViewModel = viewModel(
        factory = NotebookViewModelFactory(
            notebookPathEncoded = notebookPathEncoded,
            cacheDir = context.cacheDir,
            systemUiController = rememberSystemUiController(),
        )
    )

    var webView by remember { mutableStateOf<WebView?>(null) }
    var webViewPrint by remember { mutableStateOf<WebView?>(null) }

    Scaffold { innerPadding ->
        if (viewModel.uiState.value.notebookHtmlString != null) {
            NotebookView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                notebookHtmlString = viewModel.uiState.value.notebookHtmlString!!,
                onFinishedLoading = {
                    viewModel.finishLoading()
                    webView = it
                },
                onTapPage = {
                    viewModel.toggleFullScreen()
                },
                onFind = { activeMatchOrdinal, numberOfMatches, isDoneCounting ->
                    if (isDoneCounting) {
                        viewModel.setFindMatches(activeMatchOrdinal, numberOfMatches)
                    }
                }
            )
        }

        if (viewModel.uiState.value.notebookFile != null) {
            if (viewModel.uiState.value.findBarVisible) {
                NotebookTopFindBar(
                    onValueChange = { webView?.findAllAsync(it) },
                    onClickPrevious = { webView?.findNext(false) },
                    onClickNext = { webView?.findNext(true) },
                    onCancel = {
                        viewModel.hideFindBar()
                        webView?.findAllAsync("")
                    },
                    activeMatch = viewModel.uiState.value.activeFindMatch,
                    numberOfMatches = viewModel.uiState.value.numberOfFindMatches,
                )
            } else {
                NotebookTopAppBar(
                    navController = navController,
                    viewModel = viewModel,
                )
            }
        }

        if (viewModel.uiState.value.loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        if (viewModel.uiState.value.deleteDialogOpened) {
            DeleteNotebookAlertDialog(
                viewModel = viewModel,
                onConfirm = { isSuccessful ->
                    viewModel.closeDeleteDialog()
                    if (isSuccessful) {
//                            navController.popBackStack()
                    }
                },
                onDismiss = { viewModel.closeDeleteDialog() },
            )
        }

        if (viewModel.uiState.value.printDialogOpened) {
            PrintNotebookAlertDialog(
                onConfirm = { scaling ->
                    viewModel.closePrintDialog()
                    viewModel.startLoading()

                    webViewPrint = WebView(context)

                    printWebView(
                        context = context,
                        webView = webViewPrint!!,
                        scaling = scaling,
                        notebookFile = viewModel.uiState.value.notebookFile!!,
                        onFinishedLoading = {
                            viewModel.finishLoading()
                            webViewPrint = null
                        }
                    )
                },
                onDismiss = { viewModel.closePrintDialog() },
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
private fun printWebView(
    context: Context,
    webView: WebView,
    scaling: Int,
    notebookFile: File,
    onFinishedLoading: () -> Unit,
) {
    webView.apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )

        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) =
                false

            override fun onPageFinished(view: WebView, url: String) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager

                val printJobName =
                    notebookFile.name.substringBeforeLast(".${notebookFile.extension}")
                val documentAdapter = webView.createPrintDocumentAdapter(printJobName)

                printManager?.print(
                    printJobName,
                    documentAdapter,
                    PrintAttributes.Builder().build(),
                )

                onFinishedLoading()
            }
        }

        settings.setNeedInitialFocus(false)
        settings.setGeolocationEnabled(false)

        settings.textZoom = scaling

        settings.javaScriptEnabled = true
    }

    CoroutineScope(Dispatchers.Main).launch {
        val notebookString = notebookFile.readText()
        val notebookHtmlString = NotebookConverter.convertToHtmlPrint(context, notebookString)

        webView.loadData(
            Base64.encodeToString(notebookHtmlString.toByteArray(), Base64.NO_PADDING),
            "text/html",
            "base64",
        )
    }
}
