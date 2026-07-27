package com.omawr.tuisku.viewmodels

import androidx.lifecycle.ViewModel
import com.omawr.tuisku.settings.Preferences

class TextEditorViewModel(
    private val prefs: Preferences
) : ViewModel() {
    val encryptionKey = prefs.getEncryptionKey()
    val ivKey = prefs.getIVKey()
}