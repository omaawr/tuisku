package com.omaawr.tuisku.viewmodels

import androidx.lifecycle.ViewModel
import com.omaawr.tuisku.settings.Preferences

class TextEditorViewModel(
    private val prefs: Preferences
) : ViewModel() {
    val encryptionKey = prefs.getEncryptionKey()
    val ivKey = prefs.getIVKey()
}