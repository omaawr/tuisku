package com.omaawr.tuisku.settings

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.tink.AeadSerializer
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplate
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.PredefinedAeadParameters
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Preferences(
    private val context: Context,
    private val application: Application
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val keysetHandle: KeysetHandle =
        AndroidKeysetManager.Builder()
            .withSharedPref(application.applicationContext, "keyset", "keyset_prefs")
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

    val Context.dataStore: DataStore<Settings> by dataStore(
        fileName = "settings.json",
        serializer = serializer,
        scope = scope
    )

    fun getEncryptionKey(): Flow<String> = context.dataStore.data.map { settings ->
        settings.encryptionKey
    }

    suspend fun writeEncryptionKey(value: String) {
        context.dataStore.updateData { settings ->
            settings.copy(encryptionKey = value)
        }
    }

    fun getIVKey(): Flow<String> = context.dataStore.data.map { settings ->
        settings.viKey
    }

    suspend fun writeIVKey(value: String) {
        context.dataStore.updateData { settings ->
            settings.copy(viKey = value)
        }
    }

    fun getUseSystemFont(): Flow<Boolean> = context.dataStore.data.map { settings ->
        settings.useSystemFont
    }

    suspend fun writeUseSystemFont(value: Boolean) {
        context.dataStore.updateData { settings ->
            settings.copy(useSystemFont = value)
        }
    }

    fun getDisableScreenshots(): Flow<Boolean> = context.dataStore.data.map { settings ->
        settings.disableScreenshots
    }

    suspend fun writeDisableScreenshots(value: Boolean) {
        context.dataStore.updateData { settings ->
            settings.copy(disableScreenshots = value)
        }
    }

    fun getPassword(): Flow<String> = context.dataStore.data.map { settings ->
        settings.password
    }

    suspend fun writePassword(value: String) {
        context.dataStore.updateData { settings ->
            settings.copy(password = value)
        }
    }

    fun getFirstLaunch(): Flow<Boolean> = context.dataStore.data.map { settings ->
        settings.firstLaunch
    }

    suspend fun writeFirstLaunch(value: Boolean) {
        context.dataStore.updateData { settings ->
            settings.copy(firstLaunch = value)
        }
    }
}