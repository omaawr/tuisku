/*
 * Tuisku, a silly notes app
 * Copyright (c) 2026 Omar
 * SPDX-License-Identifier: GPL-3.0-only
 */

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
    data object Port : NavKey

    @Serializable
    data class TextEditor(val filePath: String) : NavKey
}