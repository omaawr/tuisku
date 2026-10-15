/*
 * Tuisku, a silly notes app
 * Copyright (c) 2026 Omar
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.omaawr.tuisku.di

import com.omaawr.tuisku.ui.viewmodels.HomeViewModel
import com.omaawr.tuisku.ui.viewmodels.PortViewModel
import com.omaawr.tuisku.ui.viewmodels.SettingsViewModel
import com.omaawr.tuisku.ui.viewmodels.TextEditorViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelsModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::TextEditorViewModel)
    viewModelOf(::PortViewModel)
}