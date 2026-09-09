package com.omaawr.tuisku.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omaawr.tuisku.settings.Preferences
import kotlinx.coroutines.launch
import java.io.File

interface HomeUiState {
    var showNewFileDialog: Boolean
    var showPasswordDialog: Boolean
    var showDeleteFileDialog: Boolean
    var showFirstLaunchDialog: Boolean
    var showPasswordForBottomSheet: Boolean
    var showBottomSheet: Boolean
    var showNoticeDialog: Boolean
    var showRenameNoteDialog: Boolean
    var showAnotherNoticeDialog: Boolean
    fun clear()
}

private class MutableHomeUiState : HomeUiState {
    override var showNewFileDialog: Boolean by mutableStateOf(false)
    override var showPasswordDialog: Boolean by mutableStateOf(false)
    override var showDeleteFileDialog: Boolean by mutableStateOf(false)
    override var showFirstLaunchDialog: Boolean by mutableStateOf(false)
    override var showBottomSheet: Boolean by mutableStateOf(false)
    override var showPasswordForBottomSheet: Boolean by mutableStateOf(false)
    override var showRenameNoteDialog: Boolean by mutableStateOf(false)
    override var showNoticeDialog: Boolean by mutableStateOf(false)
    override var showAnotherNoticeDialog: Boolean by mutableStateOf(false)

    override fun clear() {
        showNewFileDialog = false
        showPasswordDialog = false
        showDeleteFileDialog = false
        showFirstLaunchDialog = false
        showBottomSheet = false
        showPasswordForBottomSheet = false
        showRenameNoteDialog = false
        showNoticeDialog = false
        showAnotherNoticeDialog = false
    }
}

class SelectedFileState(
    var file: File? = null,
    var path: String? = null
)

class HomeViewModel(
    private val prefs: Preferences
) : ViewModel() {
    val uiState: HomeUiState
        field = MutableHomeUiState()

    val notePassword = prefs.getPassword()
    val firstLaunch = prefs.getFirstLaunch()
    val showNotesNames = prefs.getShowNotesNames()
    val ivKey = prefs.getIVKey()
    val useBiometrics = prefs.getUseBiometrics()

    fun writeFirstLaunch(value: Boolean) {
        viewModelScope.launch {
            prefs.writeFirstLaunch(value)
        }
    }

    fun writeIvKey(value: String) {
        viewModelScope.launch {
            prefs.writeIVKey(value)
        }
    }
}