package com.robinrezwan.jupyterreader.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.robinrezwan.jupyterreader.R

object CustomIcons {
    @Composable
    fun home(): ImageVector = Icons.Outlined.Home

    @Composable
    fun search(): ImageVector = Icons.Outlined.Search

    @Composable
    fun menu(): ImageVector = Icons.Outlined.Menu

    @Composable
    fun add(): ImageVector = Icons.Outlined.Add

    @Composable
    fun back(): ImageVector = Icons.Outlined.ArrowBack

    @Composable
    fun more(): ImageVector = Icons.Outlined.MoreVert

    @Composable
    fun share(): ImageVector = Icons.Outlined.Share

    @Composable
    fun close(): ImageVector = Icons.Outlined.Close

    @Composable
    fun about(): ImageVector = Icons.Outlined.Info

    // Custom Icons

    @Composable
    fun files(): ImageVector = ImageVector.vectorResource(R.drawable.file_outline)

    @Composable
    fun folder(): ImageVector = ImageVector.vectorResource(R.drawable.folder_outline)

    @Composable
    fun folderOpen(): ImageVector = ImageVector.vectorResource(R.drawable.folder_open_outline)

    @Composable
    fun heart(): ImageVector = ImageVector.vectorResource(R.drawable.heart_outline)

    @Composable
    fun code(): ImageVector = ImageVector.vectorResource(R.drawable.code_tags_outline)

    @Composable
    fun fileCode(): ImageVector = ImageVector.vectorResource(R.drawable.file_code_outline)

    @Composable
    fun html(): ImageVector = ImageVector.vectorResource(R.drawable.language_html5)

    @Composable
    fun python(): ImageVector = ImageVector.vectorResource(R.drawable.language_python)

    @Composable
    fun printer(): ImageVector = ImageVector.vectorResource(R.drawable.printer_outline)

    @Composable
    fun delete(): ImageVector = ImageVector.vectorResource(R.drawable.delete_outline)

    @Composable
    fun chevronUp(): ImageVector = ImageVector.vectorResource(R.drawable.chevron_up_outline)

    @Composable
    fun chevronDown(): ImageVector = ImageVector.vectorResource(R.drawable.chevron_down_outline)

    @Composable
    fun fileFind(): ImageVector = ImageVector.vectorResource(R.drawable.file_find_outline)
}
