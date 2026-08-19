package com.omaawr.tuisku.di

import com.omaawr.tuisku.managers.EncryptionManager
import com.omaawr.tuisku.settings.Preferences
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
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
            get(),
            androidContext()
        )
    }
}