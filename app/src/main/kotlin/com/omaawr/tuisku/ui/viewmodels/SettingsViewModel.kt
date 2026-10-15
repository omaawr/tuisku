/*
 * Tuisku, a silly notes app
 * Copyright (c) 2026 Omar
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.omaawr.tuisku.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omaawr.tuisku.domain.repo.SettingsRepository
import kotlinx.coroutines.launch

interface SettingsUiState {
    var showEncryptionKeys: Boolean
    var showChangePasswordDialog: Boolean
    var showRemovePasswordDialog: Boolean
    var showConfirmPassswordDialog: Boolean
    var showConfirmPassswordDialogForNotesNames: Boolean
    var showConfirmPasswordDialogForPort: Boolean
    var showConfirmPasswordDialogForBiometrics: Boolean
    fun clear()
}

private class MutableSettingsUiState : SettingsUiState {
    override var showEncryptionKeys: Boolean by mutableStateOf(false)
    override var showChangePasswordDialog: Boolean by mutableStateOf(false)
    override var showRemovePasswordDialog: Boolean by mutableStateOf(false)
    override var showConfirmPassswordDialog: Boolean by mutableStateOf(false)
    override var showConfirmPassswordDialogForNotesNames: Boolean by mutableStateOf(false)
    override var showConfirmPasswordDialogForPort: Boolean by mutableStateOf(false)
    override var showConfirmPasswordDialogForBiometrics: Boolean by mutableStateOf(false)

    override fun clear() {
        showEncryptionKeys = false
        showChangePasswordDialog = false
        showRemovePasswordDialog = false
        showChangePasswordDialog = false
        showConfirmPassswordDialogForNotesNames = false
        showConfirmPasswordDialogForBiometrics = false
    }
}

class SettingsViewModel(
    private val settings: SettingsRepository
) : ViewModel() {
    val uiState: SettingsUiState
        field = MutableSettingsUiState()

    val encryptionKey = settings.encryptionKey()
    val useSystemFont = settings.useSystemFont()
    val disableScreenshots = settings.enableSecureMode()
    val showNotesNames = settings.showNotesNames()
    val password = settings.password()
    val useBiometrics = settings.useBiometrics()
    val useAutoSave = settings.useAutoSave()

    fun writeUseSystemFont(value: Boolean) {
        viewModelScope.launch {
            settings.useSystemFont.update { settings ->
                settings.copy(useSystemFont = value)
            }
        }
    }

    fun writeShowNotesNames(value: Boolean) {
        viewModelScope.launch {
            settings.showNotesNames.update { settings ->
                settings.copy(showNotesNames = value)
            }
        }
    }

    fun writeDisableScreenshots(value: Boolean) {
        viewModelScope.launch {
            settings.enableSecureMode.update { settings ->
                settings.copy(disableScreenshots = value)
            }
        }
    }

    fun writePassword(value: String) {
        viewModelScope.launch {
            settings.password.update { settings ->
                settings.copy(password = value)
            }
        }
    }

    fun writeUseBiometrics(value: Boolean) {
        viewModelScope.launch {
            settings.useBiometrics.update { settings ->
                settings.copy(useBiometrics = value)
            }
        }
    }

    fun writeUseAutoSave(value: Boolean) {
        viewModelScope.launch {
            settings.useAutoSave.update { settings ->
                settings.copy(useAutoSave = value)
            }
        }
    }
}