package com.omaawr.tuisku.domain.manager

import android.content.Context
import android.util.Log
import com.omaawr.tuisku.domain.repo.SettingsRepository
import com.omaawr.tuisku.ui.viewmodels.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import kotlin.io.encoding.Base64

sealed interface WidgetState {
    data object Loading : WidgetState
    data class Loaded(val decryptedFilename: String, val decryptedContent: String) : WidgetState
    data object Error : WidgetState
}

class WidgetManager(
    val settings: SettingsRepository,
    val encryptionManager: EncryptionManager,
) {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val uiState: StateFlow<WidgetState>
        field = MutableStateFlow<WidgetState>(WidgetState.Loading)

    init {
        collectState()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun collectState() {
        scope.launch {
            settings.noteToUseOnWidget().collect { path ->
                try {
                    val file = File(path)

                    val decryptedContent = encryptionManager.decryptBytes(file.readBytes())
                    val decryptedFilename = encryptionManager.decryptBytes(
                        Base64.UrlSafe.decode(file.nameWithoutExtension)
                    )

                    uiState.value = WidgetState.Loaded(decryptedFilename, decryptedContent)
                } catch (e: Exception) {
                    Log.e("Tuisku", "Failed to decrypt content for widget!", e)
                    uiState.value = WidgetState.Error
                }
            }
        }
    }
}