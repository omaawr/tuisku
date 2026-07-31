package com.omaawr.tuisku.viewmodels

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omaawr.tuisku.components.EncryptionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface TextEditorUiState {
    var loading: Boolean
    var loaded: Boolean
    var error: Boolean
    var exception: String?
}

private class MutableTextEditorUiState: TextEditorUiState {
    override var loading: Boolean by mutableStateOf(false)
    override var loaded: Boolean by mutableStateOf(false)
    override var error: Boolean by mutableStateOf(false)
    override var exception: String? by mutableStateOf(null)
}

class TextEditorViewModel(
    private val encryptionManager: EncryptionManager
) : ViewModel() {
    private val _uiState = MutableTextEditorUiState()
    val uiState: TextEditorUiState = _uiState
    val textFieldState = TextFieldState()

    fun encrypt(data: String, filePath: String) {
        viewModelScope.launch {
            encryptionManager.encryptFile(data.toByteArray(), filePath)
        }
    }

    fun decrypt(data: ByteArray): Flow<String> = flow {
        emit(encryptionManager.decryptFile(data))
    }

    fun loadDecryptedData(data: ByteArray) {
        try {
            _uiState.loading = true

            viewModelScope.launch {
                val decryptedData = decrypt(data).first()

                withContext(Dispatchers.Main) {
                    textFieldState.setTextAndPlaceCursorAtEnd(decryptedData)
                }

                _uiState.loading = false
                _uiState.loaded = true
            }
        } catch (e: Exception) {
            _uiState.error = true
            _uiState.loading = false

            _uiState.exception = e.message
        }
    }
}