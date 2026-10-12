package com.omaawr.tuisku.di

import com.omaawr.tuisku.managers.EncryptionManager
import com.omaawr.tuisku.managers.PortManager
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