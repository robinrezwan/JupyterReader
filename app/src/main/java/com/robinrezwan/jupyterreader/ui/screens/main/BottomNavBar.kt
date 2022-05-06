package com.robinrezwan.jupyterreader.ui.screens.main

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalPagerApi
@Composable
fun BottomNavBar(
    items: List<BottomNavBarItem>,
    selectedItemIndex: Int,
    onSelectItem: (itemIndex: Int) -> Unit,
) {
    NavigationBar {
        items.forEachIndexed { index, navBarItem ->
            NavigationBarItem(
                selected = index == selectedItemIndex,
                onClick = { onSelectItem(index) },
                icon = {
                    Icon(
                        imageVector = navBarItem.icon,
                        contentDescription = null,
                    )
                },
                label = { Text(text = navBarItem.label) },
            )
        }
    }
}

data class BottomNavBarItem(
    val icon: ImageVector,
    val label: String,
)
