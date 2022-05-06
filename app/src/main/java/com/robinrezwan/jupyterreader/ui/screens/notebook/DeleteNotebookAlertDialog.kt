package com.robinrezwan.jupyterreader.ui.screens.notebook

import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.robinrezwan.jupyterreader.ui.screens.notebook.state.NotebookViewModel

@Composable
fun DeleteNotebookAlertDialog(
    viewModel: NotebookViewModel,
    onConfirm: (isSuccessful: Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current

    val notebookFile = viewModel.uiState.value.notebookFile!!

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Delete notebook?")
        },
        text = {
            Text(text = "This notebook will be permanently deleted from the device.")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (notebookFile.delete()) {
                        Toast.makeText(
                            context,
                            "Notebook deleted",
                            Toast.LENGTH_LONG
                        ).show()

                        onConfirm(true)
                    } else {
                        Toast.makeText(
                            context,
                            "Could not delete notebook",
                            Toast.LENGTH_LONG
                        ).show()

                        onConfirm(false)
                    }
                }
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}
