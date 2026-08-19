package com.omaawr.tuisku.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.omaawr.tuisku.R

/**
 * Note bottom sheet, used for renaming/deleting notes
 *
 * @param onDismissRequest - Upon dismissing the sheet
 * @param sheetState - Bottom sheet state
 * @param onRenameNoteClick - Upon clicking on the Rename note item
 * @param onDeleteNoteClick - Upon clicking on the Delete note item
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    onRenameNoteClick: () -> Unit,
    onDeleteNoteClick: () -> Unit
) {
    val colors = ListItemDefaults.colors(
        containerColor = Color.Transparent,
        trailingIconColor = MaterialTheme.colorScheme.onSurface,
        headlineColor = MaterialTheme.colorScheme.onSurface
    )

    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ListItem(
                content = {
                    Text(stringResource(R.string.rename_note_bottom_sheet))
                },
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = null
                    )
                },
                onClick = {
                    onRenameNoteClick()
                },
                colors = colors
            )

            ListItem(
                content = {
                    Text(
                        text = stringResource(R.string.delete_note_bottom_sheet),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                onClick = {
                    onDeleteNoteClick()
                },
                colors = colors
            )
        }
    }
}