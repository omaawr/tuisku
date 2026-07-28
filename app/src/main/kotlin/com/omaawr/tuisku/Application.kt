package com.omaawr.tuisku

import com.google.crypto.tink.aead.AeadConfig
import com.omaawr.tuisku.di.appModule
import com.omaawr.tuisku.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        AeadConfig.register()

        startKoin {
            androidLogger()
            androidContext(this@Application)
            modules(appModule, viewModelsModule)
        }
    }
}