package com.omaawr.tuisku.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omaawr.tuisku.settings.Preferences
import kotlinx.coroutines.launch

interface SettingsUiState {
    var showEncryptionKeys: Boolean
    var showChangePasswordDialog: Boolean
    var showRemovePasswordDialog: Boolean
    var showConfirmPassswordDialog: Boolean
}

private class MutableSettingsUiState: SettingsUiState {
    override var showEncryptionKeys: Boolean by mutableStateOf(false)
    override var showChangePasswordDialog: Boolean by mutableStateOf(false)
    override var showRemovePasswordDialog: Boolean by mutableStateOf(false)
    override var showConfirmPassswordDialog: Boolean by mutableStateOf(false)
}

class SettingsViewModel(
    private val prefs: Preferences
) : ViewModel() {
    private val _uiState = MutableSettingsUiState()
    val uiState: SettingsUiState = _uiState

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