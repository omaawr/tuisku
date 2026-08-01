package com.omaawr.tuisku.di

import com.omaawr.tuisku.managers.EncryptionManager
import com.omaawr.tuisku.settings.Preferences
import com.omaawr.tuisku.viewmodels.HomeViewModel
import com.omaawr.tuisku.viewmodels.SettingsViewModel
import com.omaawr.tuisku.viewmodels.TextEditorViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<Preferences> {
        Preferences(
            androidContext(),
            androidApplication()
        )
    }

    single<EncryptionManager> {
        EncryptionManager(
            get()
        )
    }
}

val viewModelsModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::TextEditorViewModel)
}