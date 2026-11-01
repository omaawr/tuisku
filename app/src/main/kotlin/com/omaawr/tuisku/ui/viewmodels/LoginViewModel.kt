package com.omaawr.tuisku.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.omaawr.tuisku.domain.repo.SettingsRepository

class LoginViewModel(
    settings: SettingsRepository
) : ViewModel() {
    val password = settings.password()
    val usingBiometrics = settings.useBiometrics()
}