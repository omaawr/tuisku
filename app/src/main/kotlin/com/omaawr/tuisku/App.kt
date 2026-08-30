package com.omaawr.tuisku

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.omaawr.tuisku.managers.EncryptionManager
import com.omaawr.tuisku.navigation.Screen
import com.omaawr.tuisku.screens.Home
import com.omaawr.tuisku.screens.Port
import com.omaawr.tuisku.screens.Settings
import com.omaawr.tuisku.screens.TextEditor
import com.omaawr.tuisku.settings.Preferences
import kotlinx.coroutines.flow.first
import org.koin.compose.koinInject
import java.io.File

/**
 * App entry point (obviously)
 */
@Composable
fun App() {
    val ctx = LocalContext.current
    val encryptionManager = koinInject<EncryptionManager>()
    val prefs = koinInject<Preferences>()

    LaunchedEffect(Unit) {
        if (prefs.getEncryptionKey().first().isEmpty()) {
            prefs.writeEncryptionKey(encryptionManager.generateKey())
        }
    }

    // 1.1.3-1 and earlier used to use the cache for when the user tries to share a note,
    // the note sometimes wouldn't delete on exit, however, with 1.1.4, it doesn't have to cache a note to share
    // so if theres a note on cache, it gets deleted on startup
    LaunchedEffect(Unit) {
        if (File(ctx.cacheDir, "note.txt").exists()) {
            File(ctx.cacheDir, "note.txt").delete()
        }
    }

    val backStack = rememberNavBackStack(Screen.Home)
    val onBack = {
        if (backStack.size >= 2) {
            backStack.removeLastOrNull()
        }
    }

    val entryProvider = entryProvider {
        entry<Screen.Home> {
            Home(
                modifier = Modifier.fillMaxSize(),
                onTextEditor = { file, path -> backStack.add(Screen.TextEditor(file, path)) },
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
                bytes = key.fileContents,
                path = key.filePath,
                onBack = onBack
            )
        }
    }

    Scaffold { contentPadding ->
        NavDisplay(
            modifier = Modifier.padding(contentPadding),
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
}