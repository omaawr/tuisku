package com.omaawr.tuisku.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omaawr.tuisku.components.EncryptionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class TextEditorViewModel(
    private val encryptionManager: EncryptionManager
) : ViewModel() {
    fun encrypt(data: String, filePath: String) {
        viewModelScope.launch {
            encryptionManager.encryptFile(data.toByteArray(), filePath)
        }
    }

    fun decrypt(data: ByteArray): Flow<String> = flow {
        emit(encryptionManager.decryptFile(data))
    }
}