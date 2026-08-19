package com.omaawr.tuisku.di

import com.omaawr.tuisku.viewmodels.HomeViewModel
import com.omaawr.tuisku.viewmodels.SettingsViewModel
import com.omaawr.tuisku.viewmodels.TextEditorViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelsModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::TextEditorViewModel)
}