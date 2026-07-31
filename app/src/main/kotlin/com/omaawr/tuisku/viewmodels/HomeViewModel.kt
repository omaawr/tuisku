package com.omaawr.tuisku.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omaawr.tuisku.components.EncryptionManager
import com.omaawr.tuisku.settings.Preferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

interface HomeUiState {
    var showNewFileDialog: Boolean
    var showPasswordDialog: Boolean
    var showDeleteFileDialog: Boolean
    var showFirstLaunchDialog: Boolean
    var showPasswordDeleteFileDialog: Boolean
}

private class MutableHomeUiState: HomeUiState {
    override var showNewFileDialog: Boolean by mutableStateOf(false)
    override var showPasswordDialog: Boolean by mutableStateOf(false)
    override var showDeleteFileDialog: Boolean by mutableStateOf(false)
    override var showFirstLaunchDialog: Boolean by mutableStateOf(false)
    override var showPasswordDeleteFileDialog: Boolean by mutableStateOf(false)
}

class HomeViewModel(
    private val prefs: Preferences,
    private val encryptionManager: EncryptionManager
) : ViewModel() {
    private val _uiState = MutableHomeUiState()
    val uiState: HomeUiState = _uiState

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