package com.omaawr.tuisku.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun ShareFile(
    text: String,
    context: Context
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

    val intent = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK || result.resultCode == Activity.RESULT_CANCELED) file.delete()
    }

    LaunchedEffect(Unit) {
        intent.launch(Intent.createChooser(shareIntent, "note"))
    }
}