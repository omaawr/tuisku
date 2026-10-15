/*
 * Tuisku, a silly notes app
 * Copyright (c) 2026 Omar
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.omaawr.tuisku.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.omaawr.tuisku.domain.manager.PortManager

class PortViewModel(
    private val portManager: PortManager
) : ViewModel() {
    fun exportNotes(uri: Uri) {
        portManager.exportNotes(uri)
    }

    fun importNotes(uri: Uri) {
        portManager.importNotes(uri)
    }

    fun exportUnencryptedNotes(uri: Uri) {
        portManager.exportUnencryptedNotes(uri)
    }

    fun importUnencryptedNotes(uri: Uri) {
        portManager.importUnencryptedNotes(uri)
    }
}