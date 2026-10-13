package com.omaawr.tuisku.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.datastore.tink.AeadSerializer
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplate
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.PredefinedAeadParameters
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.omaawr.tuisku.settings.Settings
import com.omaawr.tuisku.settings.SettingsRepository
import com.omaawr.tuisku.settings.SettingsSerializer
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val settingsModule = module {
    single<DataStore<Settings>> {
        val keysetHandle: KeysetHandle =
            AndroidKeysetManager.Builder()
                .withSharedPref(androidApplication().applicationContext, "keyset", "keyset_prefs")
                .withKeyTemplate(KeyTemplate.createFrom(PredefinedAeadParameters.CHACHA20_POLY1305))
                .withMasterKeyUri("android-keystore://master_key")
                .build()
                .keysetHandle

        val serializer = AeadSerializer(
            aead =
                keysetHandle.getPrimitive(
                    RegistryConfiguration.get(),
                    Aead::class.java,
                ),
            wrappedSerializer = SettingsSerializer
        )

        DataStoreFactory.create(
            serializer = serializer,
            produceFile = { androidContext().dataStoreFile("settings.json") }
        )
    }

    single<SettingsRepository> {
        SettingsRepository(
            get()
        )
    }
}