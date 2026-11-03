package com.omaawr.tuisku.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omaawr.tuisku.domain.manager.EncryptionManager
import com.omaawr.tuisku.domain.repo.SettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    val uiState: StateFlow<State>
        field = MutableStateFlow<State>(State.Loading)
    val isUsingBiometrics = settings.useBiometrics()

    init {
        checkEncryptionKeys()
        collectState()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun collectState() {
        viewModelScope.launch {
            settings.password().collect { value ->
                if (value.isNotEmpty() || settings.useBiometrics().first()) {
                    uiState.value = State.Loaded(true)
                } else {
                    uiState.value = State.Loaded(false)
                }
            }
        }
    }

    private fun checkEncryptionKeys() {
        viewModelScope.launch {
            if (settings.encryptionKey().first().isEmpty()) {
                settings.encryptionKey.update { settings ->
                    settings.copy(encryptionKey = encryptionManager.generateKey())
                }
            }
        }
    }

    fun updateUseBiometrics() {
        viewModelScope.launch {
            settings.useBiometrics.update { settings ->
                settings.copy(useBiometrics = false)
            }
        }
    }
}