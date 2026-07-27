package com.omawr.tuisku.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
sealed class Screen : NavKey {
    @Serializable
    data object Home : NavKey

    @Serializable
    data object Settings : NavKey

    @Serializable
    data class TextEditor(@Contextual val file: File) : NavKey
}