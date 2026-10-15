/*
 * Tuisku, a silly notes app
 * Copyright (c) 2026 Omar
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.omaawr.tuisku.di

import com.omaawr.tuisku.domain.manager.EncryptionManager
import com.omaawr.tuisku.domain.manager.PortManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    includes(settingsModule)

    single<EncryptionManager> {
        EncryptionManager(
            get(),
            androidContext()
        )
    }

    single<PortManager> {
        PortManager(
            get(),
            get()
        )
    }
}