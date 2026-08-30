package com.omaawr.tuisku.components

import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Settings item
 *
 * @param text - Settings item text
 * @param trailing - Trailing content
 */
@Composable
fun SettingsItem(
    text: @Composable () -> Unit,
    trailing: @Composable (() -> Unit) = { },
    index: Int,
    count: Int,
    onClick: () -> Unit = {},
    enabled: Boolean = true
) {
    val listItemColors = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
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
        },
        enabled = enabled
    )
}