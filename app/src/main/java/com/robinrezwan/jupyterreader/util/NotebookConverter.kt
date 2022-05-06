package com.robinrezwan.jupyterreader.util

import android.content.Context
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.zip.ZipInputStream

object NotebookConverter {
    private lateinit var pythonModule: PyObject

    suspend fun initialize(context: Context) {
        withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
            copyTemplateFiles(context)

            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(context))
            }

            pythonModule = Python.getInstance().getModule("notebook_converter")
        }
    }

    suspend fun convertToHtml(context: Context, notebookString: String): String {
        return withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
            initialize(context)
            pythonModule.callAttr("notebook_to_html", notebookString).toString()
        }
    }

    suspend fun convertToHtmlPrint(context: Context, notebookString: String): String {
        return withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
            initialize(context)
            pythonModule.callAttr("notebook_to_html_print", notebookString).toString()
        }
    }

    suspend fun convertToPython(context: Context, notebookString: String): String {
        return withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
            initialize(context)
            pythonModule.callAttr("notebook_to_python", notebookString).toString()
        }
    }

    private fun copyTemplateFiles(context: Context) {
        val templatesDir =
            File(context.filesDir.canonicalPath + "/.local/share/jupyter/nbconvert/templates")

        if (!templatesDir.exists()) {
            val templatesInStream = context.assets.open("nbconvert_templates.zip")
            FileUnzipper.unzip(ZipInputStream(templatesInStream), context.filesDir)
        }
    }
}

