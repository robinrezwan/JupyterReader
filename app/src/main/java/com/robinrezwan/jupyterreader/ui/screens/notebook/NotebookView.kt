package com.robinrezwan.jupyterreader.ui.screens.notebook

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Base64
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun NotebookView(
    modifier: Modifier,
    notebookHtmlString: String,
    onFinishedLoading: (webView: WebView) -> Unit,
    onTapPage: () -> Unit,
    onFind: (
        activeMatchOrdinal: Int,
        numberOfMatches: Int,
        isDoneCounting: Boolean,
    ) -> Unit,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )

                var initialScale: Float? = null
                var currentZoom = 1.0f

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(
                        view: WebView?,
                        url: String?,
                    ) {
                        onFinishedLoading(this@apply)
                        super.onPageFinished(view, url)
                    }

                    override fun onScaleChanged(
                        view: WebView?,
                        oldScale: Float,
                        newScale: Float,
                    ) {
                        if (initialScale == null) {
                            initialScale = oldScale
                        }
                        currentZoom = newScale / initialScale!!
                        super.onScaleChanged(view, oldScale, newScale)
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?,
                    ): Boolean {
                        val url = request?.url ?: return false
                        ContextCompat.startActivity(context, Intent(Intent.ACTION_VIEW, url), null)
                        return true
                    }
                }

                settings.setNeedInitialFocus(false)
                settings.setGeolocationEnabled(false)

                settings.textZoom = 45

                settings.builtInZoomControls = true
                settings.displayZoomControls = false

                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false

                settings.javaScriptEnabled = true

                val gestureDetector =
                    GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                        var isTextSelected = false

                        override fun onSingleTapUp(event: MotionEvent?): Boolean {
                            evaluateJavascript(
                                "(function(){return window.getSelection().toString()})()"
                            ) { selectedText ->
                                isTextSelected = selectedText.substring(1, selectedText.length - 1)
                                    .isNotEmpty()
                            }
                            return super.onSingleTapUp(event)
                        }

                        override fun onSingleTapConfirmed(event: MotionEvent?): Boolean {
                            if (!isTextSelected) {
                                onTapPage()
                            }
                            return super.onSingleTapUp(event)
                        }

                        override fun onDoubleTap(event: MotionEvent): Boolean {
                            when (currentZoom) {
                                1.0f -> zoomBy(2.0f)
                                2.0f -> zoomBy(1.5f)
                                else -> zoomBy(1.0f / currentZoom)
                            }
                            return super.onDoubleTap(event)
                        }
                    })

                setOnTouchListener { view, event ->
                    view.performClick()
                    gestureDetector.onTouchEvent(event)
                }

                setFindListener { activeMatchOrdinal, numberOfMatches, isDoneCounting ->
                    onFind(activeMatchOrdinal, numberOfMatches, isDoneCounting)
                }
            }
        },
    ) { webView ->
        webView.loadData(
            Base64.encodeToString(notebookHtmlString.toByteArray(), Base64.NO_PADDING),
            "text/html",
            "base64",
        )
    }
}
