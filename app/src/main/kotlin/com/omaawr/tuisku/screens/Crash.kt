package com.omaawr.tuisku.screens

import android.content.ClipData
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.nativeClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.omaawr.tuisku.R

@Composable
fun CrashScreen(
    exception: String
) {
    val clipboard = LocalClipboard.current.nativeClipboardManager
    val ctx = LocalContext.current

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(stringResource(R.string.unexpected_error))

            Spacer(Modifier.height(8.dp))

            Surface(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            6.dp, 6.dp, 6.dp, 6.dp
                        )
                    ),
                color = MaterialTheme.colorScheme.surfaceContainer,
                onClick = {
                    val clipData = ClipData.newPlainText("Tuisku crash log", exception)
                    clipboard.setPrimaryClip(clipData)

                    Toast.makeText(ctx, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                }
            ) {
                SelectionContainer(
                    Modifier
                        .padding(8.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    Text(
                        text = exception,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}