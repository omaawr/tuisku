package com.omaawr.tuisku.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey {
    @Serializable
    data object Home : NavKey

    @Serializable
    data object Settings : NavKey

    @Serializable
    data class TextEditor(val fileContents: ByteArray, val filePath: String) : NavKey {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TextEditor

            return fileContents.contentEquals(other.fileContents)
        }

        override fun hashCode(): Int {
            return fileContents.contentHashCode()
        }
    }
}