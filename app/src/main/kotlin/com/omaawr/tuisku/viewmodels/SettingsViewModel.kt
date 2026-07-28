package com.omaawr.tuisku.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omaawr.tuisku.settings.Preferences
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val prefs: Preferences
) : ViewModel() {
    val encryptionKey = prefs.getEncryptionKey()
    val ivKey = prefs.getIVKey()
    val useSystemFont = prefs.getUseSystemFont()
    val disableScreenshots = prefs.getDisableScreenshots()
    val password = prefs.getPassword()

    fun writeUseSystemFont(value: Boolean) {
        viewModelScope.launch {
            prefs.writeUseSystemFont(value)
        }
    }

    fun writeDisableScreenshots(value: Boolean) {
        viewModelScope.launch {
            prefs.writeDisableScreenshots(value)
        }
    }

    fun writePassword(value: String) {
        viewModelScope.launch {
            prefs.writePassword(value)
        }
    }
}