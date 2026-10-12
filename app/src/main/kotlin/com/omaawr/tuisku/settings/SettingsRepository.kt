package com.omaawr.tuisku.settings

import androidx.datastore.core.DataStore

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