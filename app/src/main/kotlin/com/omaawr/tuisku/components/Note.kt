package com.omaawr.tuisku.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omaawr.tuisku.R

/**
 * Note card
 *
 * @param onClick - Upon clicking the note
 * @param onLongClick - Upon holding the note (which would cause the bottom sheet to spawn)
 * @param filename - Note filename
 * @param date - The note's last modified date
 */
@Composable
fun Note(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    filename: String,
    date: String
) {
    var noteFilename = filename

    if (filename == "<untitled>") noteFilename = ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    onClick()
                },
                onLongClick = {
                    onLongClick()
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                noteFilename,
                fontSize = 18.sp
            )

            Text(
                stringResource(
                    R.string.note_last_modified_on_date,
                    date
                ),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}