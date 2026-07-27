package com.omawr.tuisku

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.omawr.tuisku.navigation.Navigator
import com.omawr.tuisku.navigation.Screen
import com.omawr.tuisku.navigation.rememberNavigationState
import com.omawr.tuisku.navigation.toEntries
import com.omawr.tuisku.screens.Home
import com.omawr.tuisku.screens.Settings
import com.omawr.tuisku.screens.TextEditor

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
                onTextEditor = { file -> navigator.navigate(Screen.TextEditor(file)) },
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
                file = key.file,
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