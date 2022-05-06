package com.robinrezwan.jupyterreader.ui.screens.main.home

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.extension
import com.anggrayudi.storage.file.getAbsolutePath
import com.robinrezwan.jupyterreader.util.CustomIcons
import java.io.File

@Composable
fun FilePickerFAB(
    onFilePicked: (notebookFile: File) -> Unit,
) {
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        val notebookFile = getNotebookFile(context, activityResult)

        if (notebookFile != null) {
            onFilePicked(notebookFile)
        }
    }

    ExtendedFloatingActionButton(
        onClick = {
            val filePickerIntent = Intent().apply {
                action = Intent.ACTION_GET_CONTENT
                type = "*/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }

            filePickerLauncher.launch(filePickerIntent)
        },
        text = { Text("Open") },
        icon = { Icon(CustomIcons.folderOpen(), null) },
    )
}

private fun getNotebookFile(
    context: Context,
    activityResult: ActivityResult,
): File? {
    val fileUri = activityResult.data?.data ?: return null

    val documentFile = DocumentFileCompat.fromUri(context, fileUri)

    if (documentFile == null) {
        Toast.makeText(
            context,
            "File could not be accessed",
            Toast.LENGTH_SHORT,
        ).show()

        return null
    }

    if (!documentFile.extension.equals("ipynb", ignoreCase = true)) {
        Toast.makeText(
            context,
            "Unsupported file format",
            Toast.LENGTH_SHORT,
        ).show()

        return null
    }

    return File(documentFile.getAbsolutePath(context))
}
