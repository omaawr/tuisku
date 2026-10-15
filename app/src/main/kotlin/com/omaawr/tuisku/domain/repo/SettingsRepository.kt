/*
 * Tuisku, a silly notes app
 * Copyright (c) 2026 Omar
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.omaawr.tuisku.domain.repo

import androidx.datastore.core.DataStore
import com.omaawr.tuisku.domain.manager.BasePreferenceManager

class SettingsRepository(
    settings: DataStore<Settings>
) : BasePreferenceManager(settings) {
    val encryptionKey = preference(Settings::encryptionKey)
    val password = preference(Settings::password)
    val enableSecureMode = preference(Settings::disableScreenshots)

    val showNotesNames = preference(Settings::showNotesNames)
    val useSystemFont = preference(Settings::useSystemFont)
    val useAutoSave = preference(Settings::useAutoSave)

    val useBiometrics = preference(Settings::useBiometrics)
    val firstLaunch = preference(Settings::firstLaunch)
    // this isn't really used anymore, this was kind of back when i was a bit dumb at security (tuisku was my first actual
    // android app after all)
    val ivKey = preference(Settings::viKey)
}