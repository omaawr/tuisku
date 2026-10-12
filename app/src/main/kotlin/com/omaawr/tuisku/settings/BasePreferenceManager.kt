package com.omaawr.tuisku.settings

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.map

/**
 * Tuisku's base preference manager comes from zoot's tau project and modified,
 * https://github.com/zt64/tau/blob/main/core/src/main/kotlin/dev/zt64/tau/domain/manager/base/BasePreferencesManager.kt
 * Copyright (c) 2026 zt64 and contributors
 *
 * @param settings - Datastore to be used
 * @since 1.3.4
 */
abstract class BasePreferenceManager(protected val settings: DataStore<Settings>) {
    fun <T> preference(getter: (Settings) -> T): Pref<T> = Pref(getter)

    inner class Pref<T>(val getter: (Settings) -> T) {
        operator fun invoke() = settings.data.map(getter)

        suspend fun update(block: (Settings) -> Settings) = settings.updateData(block)
    }
}
