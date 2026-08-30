package com.omaawr.tuisku.components

import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Settings item
 *
 * @param modifier - The settings item's modifier, usually unused
 * @param text - Settings item text
 * @param trailing - Trailing content
 */
@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    text: @Composable () -> Unit,
    trailing: @Composable (() -> Unit) = { },
    index: Int,
    count: Int,
    onClick: () -> Unit = {}
) {
    val listItemColors = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        trailingContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    ListItem(
        onClick = onClick,
        colors = listItemColors,
        shapes = ListItemDefaults.segmentedShapes(index = index, count = count),
        content = {
            text()
        },
        trailingContent = {
            trailing()
        }
    )
}