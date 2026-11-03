package com.omaawr.tuisku.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.omaawr.tuisku.domain.manager.EncryptionManager
import com.omaawr.tuisku.domain.repo.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface State {
    data object Loading : State
    data class Loaded(val useAuthentication: Boolean) : State
}

class AppViewModel(
    val settings: SettingsRepository,
    val encryptionManager: EncryptionManager
) : ViewModel() {
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    val uiState: StateFlow<State>
        field = MutableStateFlow<State>(State.Loading)

    init {
        checkEncryptionKeys()
        collectState()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun collectState() {
        scope.launch {
            settings.password().collect { value ->
                if (value.isNotEmpty() || settings.useBiometrics().first()) {
                    uiState.value = State.Loaded(true)
                } else {
                    uiState.value = State.Loaded(false)
                }
            }
        }
    }

    fun checkEncryptionKeys() {
        scope.launch {
            if (settings.encryptionKey().first().isEmpty()) {
                settings.encryptionKey.update { settings ->
                    settings.copy(encryptionKey = encryptionManager.generateKey())
                }
            }
        }
    }
}