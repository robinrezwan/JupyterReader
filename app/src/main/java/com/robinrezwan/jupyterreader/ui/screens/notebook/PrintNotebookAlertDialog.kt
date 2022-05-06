package com.robinrezwan.jupyterreader.ui.screens.notebook

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlin.math.roundToInt

@Composable
fun PrintNotebookAlertDialog(
    onConfirm: (scaling: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var sliderPosition by remember { mutableStateOf(100f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Print or Save as PDF")
        },
        text = {
            Column {
                Text(text = "Scaling: ${sliderPosition.roundToInt()}")
                Slider(
                    modifier = Modifier.fillMaxWidth(),
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    valueRange = 40f..100f,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(sliderPosition.roundToInt()) }
            ) {
                Text("Done")
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
