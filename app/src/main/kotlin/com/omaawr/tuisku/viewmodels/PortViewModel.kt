package com.omaawr.tuisku.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.omaawr.tuisku.managers.PortManager

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