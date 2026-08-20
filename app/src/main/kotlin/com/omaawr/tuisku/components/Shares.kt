package com.omaawr.tuisku.components

import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

/**
 * Share a note
 *
 * @param text - Unencrypted string to share securely
 */
@Composable
fun ShareFile(
    text: String
) {
    val activity = LocalActivity.current

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooser = Intent.createChooser(shareIntent, "note")

    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    LaunchedEffect(Unit) {
        activity!!.startActivity(chooser)
    }
}