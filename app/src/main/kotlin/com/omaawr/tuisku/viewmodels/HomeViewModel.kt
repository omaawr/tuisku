package com.omaawr.tuisku.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omaawr.tuisku.components.EncryptionManager
import com.omaawr.tuisku.settings.Preferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(
    private val prefs: Preferences,
    private val encryptionManager: EncryptionManager
) : ViewModel() {
    val notePassword = prefs.getPassword()
    val firstLaunch = prefs.getFirstLaunch()

    fun checkKeys() {
        viewModelScope.launch {
            if (prefs.getEncryptionKey().first().isEmpty()) {
                prefs.writeEncryptionKey(encryptionManager.generateKey(32))
            }

            if (prefs.getIVKey().first().isEmpty()) {
                prefs.writeIVKey(encryptionManager.generateKey(12))
            }
        }
    }

    fun writeFirstLaunch(value: Boolean) {
        viewModelScope.launch {
            prefs.writeFirstLaunch(value)
        }
    }
}