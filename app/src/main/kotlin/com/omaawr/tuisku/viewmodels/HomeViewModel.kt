package com.omaawr.tuisku.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omaawr.tuisku.settings.Preferences
import kotlinx.coroutines.launch

interface HomeUiState {
    var showNewFileDialog: Boolean
    var showPasswordDialog: Boolean
    var showDeleteFileDialog: Boolean
    var showFirstLaunchDialog: Boolean
    var showPasswordDeleteFileDialog: Boolean
}

private class MutableHomeUiState: HomeUiState {
    override var showNewFileDialog: Boolean by mutableStateOf(false)
    override var showPasswordDialog: Boolean by mutableStateOf(false)
    override var showDeleteFileDialog: Boolean by mutableStateOf(false)
    override var showFirstLaunchDialog: Boolean by mutableStateOf(false)
    override var showPasswordDeleteFileDialog: Boolean by mutableStateOf(false)
}

class HomeViewModel(
    private val prefs: Preferences
) : ViewModel() {
    private val _uiState = MutableHomeUiState()
    val uiState: HomeUiState = _uiState

    val notePassword = prefs.getPassword()
    val firstLaunch = prefs.getFirstLaunch()

    fun writeFirstLaunch(value: Boolean) {
        viewModelScope.launch {
            prefs.writeFirstLaunch(value)
        }
    }
}