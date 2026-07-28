package com.omaawr.tuisku

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.omaawr.tuisku.navigation.Navigator
import com.omaawr.tuisku.navigation.Screen
import com.omaawr.tuisku.navigation.rememberNavigationState
import com.omaawr.tuisku.navigation.toEntries
import com.omaawr.tuisku.screens.Home
import com.omaawr.tuisku.screens.Settings
import com.omaawr.tuisku.screens.TextEditor

@Composable
fun App() {
    val routes = setOf(Screen.Home)
    val navigationState = rememberNavigationState(
        startRoute = Screen.Home,
        topLevelRoutes = routes
    )

    val navigator = remember { Navigator(navigationState) }
    val entryProvider = entryProvider {
        entry<Screen.Home> {
            Home(
                modifier = Modifier.fillMaxSize(),
                onTextEditor = { file, path -> navigator.navigate(Screen.TextEditor(file, path)) },
                onSettings = { navigator.navigate(Screen.Settings) }
            )
        }

        entry<Screen.Settings> {
            Settings(
                onBack = { navigator.goBack() }
            )
        }

        entry<Screen.TextEditor> { key ->
            TextEditor(
                modifier = Modifier.fillMaxSize(),
                bytes = key.fileContents,
                path = key.filePath,
                onBack = { navigator.goBack() }
            )
        }
    }

    Scaffold { contentPadding ->
        NavDisplay(
            modifier = Modifier.padding(contentPadding),
            entries = navigationState.toEntries(entryProvider),
            onBack = { navigator.goBack() },
            sceneStrategies = remember { listOf(DialogSceneStrategy()) }
        )
    }
}