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
import com.omaawr.tuisku.domain.manager.EncryptionManager
import com.omaawr.tuisku.domain.repo.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

interface TextEditorUiState {
    var loading: Boolean
    var loaded: Boolean
}

private class MutableTextEditorUiState : TextEditorUiState {
    override var loading: Boolean by mutableStateOf(false)
    override var loaded: Boolean by mutableStateOf(false)
}

class TextEditorViewModel(
    private val encryptionManager: EncryptionManager,
    settings: SettingsRepository
) : ViewModel() {
    val uiState: TextEditorUiState
        field = MutableTextEditorUiState()
    val useAutoSave = settings.useAutoSave()

    fun encrypt(data: String, filePath: String) {
        viewModelScope.launch {
            encryptionManager.encryptFile(data.toByteArray(), filePath)
        }
    }

    fun decrypt(data: ByteArray): Flow<String> = flow {
        emit(encryptionManager.decryptBytes(data))
    }.take(1)
}