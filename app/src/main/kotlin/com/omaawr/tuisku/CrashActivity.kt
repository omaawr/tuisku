package com.omaawr.tuisku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.omaawr.tuisku.screens.CrashScreen
import com.omaawr.tuisku.ui.theme.TuiskuTheme

class CrashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val exception = intent.getStringExtra("exception") ?: "no exception"

        setContent {
            TuiskuTheme(
                useSystemFont = true
            ) {
                CrashScreen(exception)
            }
        }
    }
}