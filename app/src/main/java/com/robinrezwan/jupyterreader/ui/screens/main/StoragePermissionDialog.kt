package com.robinrezwan.jupyterreader.ui.screens.main

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.robinrezwan.jupyterreader.BuildConfig
import com.robinrezwan.jupyterreader.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StoragePermissionDialog(
    onPermissionGranted: () -> Unit,
) {
    var dialogOpened by remember { mutableStateOf(true) }

    // For Android Q (API 29) and bellow
    val storagePermissionState = rememberPermissionState(
        permission = Manifest.permission.WRITE_EXTERNAL_STORAGE,
        onPermissionResult = { permissionGranted ->
            dialogOpened = !permissionGranted

            if (permissionGranted) {
                onPermissionGranted()
            }
        })

    // For Android R (API 30) and above
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val permissionGranted = Environment.isExternalStorageManager()

                dialogOpened = !permissionGranted

                if (permissionGranted) {
                    onPermissionGranted()
                }
            }
        },
    )

    // Checking if permission is already granted
    dialogOpened =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            !Environment.isExternalStorageManager()
        } else {
            !storagePermissionState.status.isGranted
        }

    // Showing dialog if permission is not granted
    if (dialogOpened) {
        AlertDialog(
            onDismissRequest = {},
            icon = {
                Image(
                    painter = painterResource(R.drawable.storage_folder),
                    modifier = Modifier.size(48.dp),
                    contentDescription = null
                )
            },
            title = {
                Text(text = "Permission")
            },
            text = {
                Text(
                    "Jupyter Notebook Viewer needs storage access permission to " +
                            "work as intended. Please grant the permission.",
                    textAlign = TextAlign.Center,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            val storagePermissionIntent = Intent(
                                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                Uri.fromParts(
                                    "package",
                                    BuildConfig.APPLICATION_ID,
                                    null
                                ),
                            )
                            storagePermissionLauncher.launch(storagePermissionIntent)
                        } else {
                            storagePermissionState.launchPermissionRequest()
                        }
                    }
                ) {
                    Text("Continue")
                }
            },
        )
    }
}
