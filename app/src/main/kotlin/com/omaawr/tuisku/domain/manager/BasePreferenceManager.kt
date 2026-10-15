/*
 * Tuisku's base preference manager comes from zoot's tau project and modified,
 * https://github.com/zt64/tau/blob/main/core/src/main/kotlin/dev/zt64/tau/domain/manager/base/BasePreferencesManager.kt
 * Copyright (c) 2026 zt64 and contributors
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.omaawr.tuisku.domain.manager

import androidx.datastore.core.DataStore
import com.omaawr.tuisku.domain.repo.Settings
import kotlinx.coroutines.flow.map

abstract class BasePreferenceManager(protected val settings: DataStore<Settings>) {
    fun <T> preference(getter: (Settings) -> T): Pref<T> = Pref(getter)

    inner class Pref<T>(val getter: (Settings) -> T) {
        operator fun invoke() = settings.data.map(getter)

        suspend fun update(block: (Settings) -> Settings) = settings.updateData(block)
    }
}