package com.robinrezwan.jupyterreader.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.robinrezwan.jupyterreader.R
import com.robinrezwan.jupyterreader.util.isSameDate
import com.robinrezwan.jupyterreader.util.isSameYear
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotebookListItem(
    fileName: String,
    fileLength: Long,
    dateTime: Long,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        icon = {
            Image(
                painter = painterResource(R.drawable.jupyter_file),
                modifier = Modifier.size(40.dp),
                contentDescription = null
            )
        },
        text = {
            Text(
                text = fileName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        secondaryText = {
            val lastModified = formatDateTime(dateTime)
            val fileSize = formatFileSize(fileLength)

            Text(
                text = "$lastModified • $fileSize • IPYNB file",
                style = MaterialTheme.typography.bodyMedium.copy(
                    MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
    )
}

// TODO: Add relative time formatting for recent files and starred files
private fun formatDateTime(dateTimeMilli: Long): String {
    val dateTime = Instant.ofEpochMilli(dateTimeMilli)
        .atZone(ZoneId.systemDefault()).toLocalDateTime()
    val now = LocalDateTime.now()

    return when {
        dateTime.isSameDate(now) -> dateTime.format(DateTimeFormatter.ofPattern("h:mm a"))
        dateTime.isSameYear(now) -> dateTime.format(DateTimeFormatter.ofPattern("MMM d"))
        else -> dateTime.format(DateTimeFormatter.ofPattern("MMM d, y"))
    }
}

private fun formatFileSize(fileSize: Long): String {
    var fileSizeString: String

    val b = fileSize.toDouble()
    fileSizeString = "%.2f".format(b) + " B"

    if (b >= 900) {
        val kb = b / 1000
        fileSizeString = "%.2f".format(kb) + " KB"

        if (kb >= 900) {
            val mb = kb / 1000
            fileSizeString = "%.2f".format(mb) + " MB"

            if (mb >= 900) {
                val gb = mb / 1000
                fileSizeString = "%.2f".format(gb) + " GB"
            }
        }
    }

    return fileSizeString.replace(".00", "")
}
