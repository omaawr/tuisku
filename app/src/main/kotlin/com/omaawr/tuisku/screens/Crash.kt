package com.omaawr.tuisku.screens

import android.content.ClipData
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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

/**
 * Crash screen to be used with CrashActivity
 *
 * @param exception - The exception to display, provided by the crash
 * @since 1.1.0
 */
@Composable
fun Crash(
    exception: String
) {
    Scaffold { innerPadding ->
        Content(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            exception = exception
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier,
    exception: String
) {
    val clipboard = LocalClipboard.current.nativeClipboardManager
    val ctx = LocalContext.current

    LazyColumn(
        modifier = modifier
    ) {
        item {
            Text(stringResource(R.string.unexpected_error))
        }

        item {
            Spacer(Modifier.height(8.dp))
        }

        item {
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