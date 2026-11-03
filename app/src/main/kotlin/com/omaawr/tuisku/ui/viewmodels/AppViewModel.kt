package com.omaawr.tuisku.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.omaawr.tuisku.domain.manager.EncryptionManager
import com.omaawr.tuisku.domain.repo.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

sealed interface State {
    data object Loading : State
    data class Loaded(val usingPassword: Boolean) : State
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
            val flow = settings.password().transformLatest { trigger ->
                if (trigger.isNotEmpty()) {
                    emit(State.Loaded(true))
                } else {
                    emit(State.Loaded(false))
                }
            }.distinctUntilChanged().stateIn(
                scope,
                SharingStarted.Eagerly,
                State.Loading
            )

            flow.collect { state ->
                uiState.value = state
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