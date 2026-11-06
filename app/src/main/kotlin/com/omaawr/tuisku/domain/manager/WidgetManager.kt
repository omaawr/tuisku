package com.omaawr.tuisku.domain.manager

import android.util.Log
import com.omaawr.tuisku.domain.repo.SettingsRepository
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
    data class Loaded(
        val notes: Map<String, String>
    ) : WidgetState
    data object Error : WidgetState
}

/**
 * Widget manager, which collects the notes and decrypts them for the widget to display
 *
 * @since 1.4.1
 * @param settings - Settings repository (provided by Koin)
 * @param encryptionManager - Encryption maanger (provided by Koin)
 */
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
            settings.noteToUseOnWidget().collect { notes ->
                val map: MutableMap<String, String> = mutableMapOf()

                notes.forEach { path ->
                    try {
                        val file = File(path)

                        val decryptedContent = encryptionManager.decryptBytes(file.readBytes())
                        val decryptedFilename = encryptionManager.decryptBytes(
                            Base64.UrlSafe.decode(file.nameWithoutExtension)
                        )

                        map[decryptedFilename] = decryptedContent

                        uiState.value = WidgetState.Loaded(map)
                    } catch (e: Exception) {
                        Log.e("Tuisku", "Failed to decrypt content for widget!", e)
                        uiState.value = WidgetState.Error
                    }
                }
            }
        }
    }
}