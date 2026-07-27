package com.omawr.tuisku.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omawr.tuisku.components.generateKey
import com.omawr.tuisku.settings.Preferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(
    private val prefs: Preferences
) : ViewModel() {
    val notePassword = prefs.getPassword()
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
}