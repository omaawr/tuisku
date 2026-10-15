/*
 * Tuisku, a silly notes app
 * Copyright (c) 2026 Omar
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.omaawr.tuisku

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import com.omaawr.tuisku.domain.repo.SettingsRepository
import com.omaawr.tuisku.ui.theme.TuiskuTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val settings: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        settings.enableSecureMode()
            .launchInLifecycle(lifecycle) {
                when (it) {
                    true -> {
                        window.setFlags(
                            WindowManager.LayoutParams.FLAG_SECURE,
                            WindowManager.LayoutParams.FLAG_SECURE
                        )
                    }

                    else -> {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                    }
                }
            }

        setContent {
            val useSystemFont =
                settings.useSystemFont().collectAsStateWithLifecycle(initialValue = false)

            TuiskuTheme(
                useSystemFont = useSystemFont.value
            ) {
                App()
            }
        }
    }
}

// This function comes from the file
// https://github.com/X1nto/Mauth/blob/master/app/src/main/java/com/xinto/mauth/util/Coroutines.kt#L20
// Copyright (c) 2026 X1nto and contributors
fun <T> Flow<T>.launchInLifecycle(
    lifecycle: Lifecycle,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend (T) -> Unit
): Job {
    return flowWithLifecycle(lifecycle, state)
        .onEach(action)
        .launchIn(lifecycle.coroutineScope)
}