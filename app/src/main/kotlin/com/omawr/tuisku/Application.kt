package com.omawr.tuisku

import com.google.crypto.tink.aead.AeadConfig
import com.omawr.tuisku.di.appModule
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
            modules(appModule)
        }
    }
}