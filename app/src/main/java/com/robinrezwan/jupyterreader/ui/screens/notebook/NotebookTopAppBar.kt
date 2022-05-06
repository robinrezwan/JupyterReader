package com.robinrezwan.jupyterreader.ui.screens.notebook

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.robinrezwan.jupyterreader.BuildConfig
import com.robinrezwan.jupyterreader.ui.screens.notebook.state.NotebookViewModel
import com.robinrezwan.jupyterreader.util.CustomIcons
import com.robinrezwan.jupyterreader.util.NotebookConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun NotebookTopAppBar(
    navController: NavHostController,
    viewModel: NotebookViewModel,
) {
    val context = LocalContext.current

    val notebookFile = viewModel.uiState.value.notebookFile!!

    var notebookPythonString by remember { mutableStateOf<String?>(null) }

    val fileCreatorLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        val fileUri: Uri? = activityResult.data?.data

        if (fileUri != null) {
            try {
                val outputStream = context.contentResolver.openOutputStream(fileUri)
                outputStream!!.write(notebookPythonString!!.toByteArray())
                outputStream.close()
                notebookPythonString = null
            } catch (exception: Exception) {
                Toast.makeText(
                    context,
                    "Could not save notebook as Python Code",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    AnimatedVisibility(
        visible = viewModel.uiState.value.appBarVisible,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        Surface(
            modifier = Modifier.wrapContentHeight(),
            shadowElevation = 4.dp,
        ) {
            SmallTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = {
                    Text(
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    Toast.makeText(
                                        context,
                                        notebookFile.name,
                                        Toast.LENGTH_LONG,
                                    ).show()
                                }
                            )
                        },
                        text = notebookFile.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(CustomIcons.back(), null)
                    }
                },
                actions = {
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
                                leadingIcon = {
                                    Icon(CustomIcons.fileFind(), null)
                                },
                                text = { Text("Find...") },
                                onClick = {
                                    viewModel.collapseMenu()
                                    viewModel.showFindBar()
                                },
                            )
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(CustomIcons.delete(), null)
                                },
                                text = { Text("Delete") },
                                onClick = {
                                    // TODO: Implement file deleting
                                    viewModel.collapseMenu()
                                    viewModel.openDeleteDialog()
                                },
                            )
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(CustomIcons.share(), null)
                                },
                                text = { Text("Share") },
                                onClick = {
                                    viewModel.collapseMenu()

                                    try {
                                        shareNotebook(
                                            context,
                                            viewModel.getNotebookShareCacheFile()
                                        )
                                    } catch (exception: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Could not share notebook",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                },
                            )
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(CustomIcons.printer(), null)
                                },
                                text = { Text("Print or Save as PDF") },
                                onClick = {
                                    viewModel.collapseMenu()
                                    viewModel.openPrintDialog()
                                },
                            )
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(CustomIcons.python(), null)
                                },
                                text = { Text("Save as Python Code") },
                                onClick = {
                                    viewModel.collapseMenu()
                                    viewModel.startLoading()

                                    try {
                                        saveAsPython(
                                            context = context,
                                            notebookFile = notebookFile,
                                            onFinishedLoading = {
                                                notebookPythonString = it
                                                viewModel.finishLoading()
                                            },
                                            fileCreatorLauncher = fileCreatorLauncher,
                                        )
                                    } catch (exception: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Could not save notebook as Python Code",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                },
                            )
                        }
                    }
                },
            )
        }
    }
}

private fun shareNotebook(
    context: Context,
    notebookShareCacheFile: File,
) {
    val contentUri = FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.FileProvider",
        notebookShareCacheFile,
    )

    val shareIntent = Intent.createChooser(
        Intent().apply {
            action = Intent.ACTION_SEND
            type = "*/*"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        },
        null
    )

    ContextCompat.startActivity(context, shareIntent, null)
}

private fun saveAsPython(
    context: Context,
    notebookFile: File,
    onFinishedLoading: (notebookPythonString: String) -> Unit,
    fileCreatorLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    CoroutineScope(Dispatchers.Main).launch {
        val notebookString = notebookFile.readText()
        val notebookPythonString = NotebookConverter.convertToPython(context, notebookString)

        onFinishedLoading(notebookPythonString)

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/x-python"
            putExtra(
                Intent.EXTRA_TITLE,
                notebookFile.name.substringBeforeLast(".${notebookFile.extension}")
            )
        }

        fileCreatorLauncher.launch(intent)
    }
}
