package com.omaawr.tuisku.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omaawr.tuisku.components.generateKey
import com.omaawr.tuisku.settings.Preferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(
    private val prefs: Preferences
) : ViewModel() {
    val notePassword = prefs.getPassword()
    val firstLaunch = prefs.getFirstLaunch()

    fun checkKeys() {
        viewModelScope.launch {
            if (prefs.getEncryptionKey().first().isEmpty()) {
                prefs.writeEncryptionKey(generateKey(32))
            }

            if (prefs.getIVKey().first().isEmpty()) {
                prefs.writeIVKey(generateKey(12))
            }
        }
    }

    fun writeFirstLaunch(value: Boolean) {
        viewModelScope.launch {
            prefs.writeFirstLaunch(value)
        }
    }
}