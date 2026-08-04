package com.omaawr.tuisku

import android.content.Intent
import android.util.Log
import com.google.crypto.tink.aead.AeadConfig
import com.omaawr.tuisku.di.appModule
import com.omaawr.tuisku.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()

        if (getProcessName().endsWith(":crash")) {
            return
        }

        Thread.setDefaultUncaughtExceptionHandler { _, exception ->
            try {
                val intent = Intent(this, CrashActivity::class.java).apply {
                    putExtra("exception", Log.getStackTraceString(exception))
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }

                startActivity(intent)
            } catch (e: Exception) {
                Log.e("Tuisku", "Failed to start CrashActivity", e)
            }
        }

        AeadConfig.register()

        startKoin {
            androidLogger()
            androidContext(this@Application)
            modules(appModule, viewModelsModule)
        }
    }
}