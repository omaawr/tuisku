package com.omaawr.tuisku.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omaawr.tuisku.components.EncryptionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

interface TextEditorUiState {
    var loading: Boolean
    var loaded: Boolean
}

private class MutableTextEditorUiState: TextEditorUiState {
    override var loading: Boolean by mutableStateOf(false)
    override var loaded: Boolean by mutableStateOf(false)
}

class TextEditorViewModel(
    private val encryptionManager: EncryptionManager
) : ViewModel() {
    private val _uiState = MutableTextEditorUiState()
    val uiState: TextEditorUiState = _uiState

    fun encrypt(data: String, filePath: String) {
        viewModelScope.launch {
            encryptionManager.encryptFile(data.toByteArray(), filePath)
        }
    }

    fun decrypt(data: ByteArray): Flow<String> = flow {
        emit(encryptionManager.decryptFile(data))
    }
}