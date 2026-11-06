/*
 * Tuisku, a silly notes app
 * Copyright (c) 2026 Omar
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.omaawr.tuisku.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omaawr.tuisku.domain.repo.SettingsRepository
import kotlinx.coroutines.launch
import java.io.File

interface HomeUiState {
    var showNewFileDialog: Boolean
    var showDeleteFileDialog: Boolean
    var showFirstLaunchDialog: Boolean
    var showBottomSheet: Boolean
    var showNoticeDialog: Boolean
    var showRenameNoteDialog: Boolean
    var showAnotherNoticeDialog: Boolean
    fun clear()
}

private class MutableHomeUiState : HomeUiState {
    override var showNewFileDialog: Boolean by mutableStateOf(false)
    override var showDeleteFileDialog: Boolean by mutableStateOf(false)
    override var showFirstLaunchDialog: Boolean by mutableStateOf(false)
    override var showBottomSheet: Boolean by mutableStateOf(false)
    override var showRenameNoteDialog: Boolean by mutableStateOf(false)
    override var showNoticeDialog: Boolean by mutableStateOf(false)
    override var showAnotherNoticeDialog: Boolean by mutableStateOf(false)

    override fun clear() {
        showNewFileDialog = false
        showDeleteFileDialog = false
        showFirstLaunchDialog = false
        showBottomSheet = false
        showRenameNoteDialog = false
        showNoticeDialog = false
    }
}

class SelectedFileState(
    var file: File? = null,
    var path: String? = null
)

class HomeViewModel(
    private val settings: SettingsRepository
) : ViewModel() {
    val uiState: HomeUiState
        field = MutableHomeUiState()

    val firstLaunch = settings.firstLaunch()
    val showNotesNames = settings.showNotesNames()
    val ivKey = settings.ivKey()
    val notesToUseOnWidget = settings.noteToUseOnWidget()

    fun writeFirstLaunch(value: Boolean) {
        viewModelScope.launch {
            settings.firstLaunch.update { settings ->
                settings.copy(firstLaunch = value)
            }
        }
    }

    fun writeIvKey(value: String) {
        viewModelScope.launch {
            settings.ivKey.update { settings ->
                settings.copy(viKey = value)
            }
        }
    }

    fun writeUseNoteOnWidget(value: List<String>) {
        viewModelScope.launch {
            settings.noteToUseOnWidget.update { settings ->
                settings.copy(noteToUseOnWidget = value)
            }
        }
    }
}