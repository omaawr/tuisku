/*
 * Tuisku, a silly notes app
 * Copyright (c) 2026 Omar
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.omaawr.tuisku

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.omaawr.tuisku.navigation.Screen
import com.omaawr.tuisku.ui.screens.Home
import com.omaawr.tuisku.ui.screens.Landing
import com.omaawr.tuisku.ui.screens.Login
import com.omaawr.tuisku.ui.screens.Port
import com.omaawr.tuisku.ui.screens.Settings
import com.omaawr.tuisku.ui.screens.TextEditor
import com.omaawr.tuisku.ui.viewmodels.AppViewModel
import com.omaawr.tuisku.ui.viewmodels.State
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.compose.koinViewModel
import java.io.File

/**
 * App entry point (obviously)
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun App() {
    val ctx = LocalContext.current
    val viewModel: AppViewModel = koinViewModel()
    val uiState = viewModel.uiState.collectAsState()

    // 1.1.3-1 and earlier used to use the cache for when the user tries to share a note,
    // the note sometimes wouldn't delete on exit, however, with 1.1.4, it doesn't have to cache a note to share
    // so if theres a note on cache, it gets deleted on startup
    LaunchedEffect(Unit) {
        if (File(ctx.cacheDir, "note.txt").exists()) {
            File(ctx.cacheDir, "note.txt").delete()
        }
    }

    val backStack = rememberNavBackStack(Screen.Landing)
    val onBack = {
        if (backStack.size >= 2) {
            backStack.removeLastOrNull()
        }
    }

    val entryProvider = entryProvider {
        entry<Screen.Landing> {
            Landing()
        }

        entry<Screen.Home> {
            Home(
                modifier = Modifier.fillMaxSize(),
                onTextEditor = { path -> backStack.add(Screen.TextEditor(path)) },
                onSettings = { backStack.add(Screen.Settings) }
            )
        }

        entry<Screen.Port> {
            Port(
                onBack = onBack
            )
        }

        entry<Screen.Settings> {
            Settings(
                onBack = onBack,
                onPort = { backStack.add(Screen.Port) }
            )
        }

        entry<Screen.TextEditor> { key ->
            TextEditor(
                modifier = Modifier.fillMaxSize(),
                path = key.filePath,
                onBack = onBack
            )
        }

        entry<Screen.Login> {
            Login(
                onHome = {
                    backStack.add(Screen.Home)
                    backStack.remove(Screen.Login)
                }
            )
        }
    }

    when (uiState.value) {
        State.Loaded(true) if !backStack.contains(Screen.Home) -> {
            backStack.add(Screen.Login)
            backStack.remove(Screen.Landing)
        }

        State.Loaded(false) if !backStack.contains(Screen.Home) -> {
            backStack.add(Screen.Home)
            backStack.remove(Screen.Landing)
        }

        else -> {}
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider,
        onBack = onBack,
        sceneStrategies = remember { listOf(DialogSceneStrategy()) },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),

            // used to destory page's viewmodels on their exit
            rememberViewModelStoreNavEntryDecorator()
        )
    )
}