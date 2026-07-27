package com.omawr.tuisku.di

import com.omawr.tuisku.settings.Preferences
import com.omawr.tuisku.viewmodels.HomeViewModel
import com.omawr.tuisku.viewmodels.SettingsViewModel
import com.omawr.tuisku.viewmodels.TextEditorViewModel
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

    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::TextEditorViewModel)
}