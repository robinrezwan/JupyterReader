package com.robinrezwan.jupyterreader.ui.screens.main.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChipTabBar(
    items: List<ChipTabBarItem>,
    selectedItemIndex: Int,
    onSelectItem: (itemIndex: Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .wrapContentSize(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEachIndexed { index, tabBarItem ->
            Chip(
                onClick = { onSelectItem(index) },
                colors = ChipDefaults.chipColors(
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
                border = if (index == selectedItemIndex)
                    BorderStroke(
                        ChipDefaults.OutlinedBorderSize,
                        MaterialTheme.colorScheme.primary,
                    ) else null
            ) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = tabBarItem.label,
                )
            }
        }
    }
}

data class ChipTabBarItem(
    val label: String,
)
