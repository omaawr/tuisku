package com.omaawr.tuisku.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

fun shareFile(
    text: String,
    context: Context,
    activity: Activity
) {
    val file = File(context.cacheDir, "note.txt")
    file.writeText(text)

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.FileProvider",
        file
    )

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    activity.startActivity(Intent.createChooser(shareIntent, "note"))

    file.deleteOnExit()
}