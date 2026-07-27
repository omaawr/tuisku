package com.omawr.tuisku.settings

import android.util.Log
import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class Settings(
    val encryptionKey: String = "",
    val viKey: String = "",
    val password: String = "",
    val useSystemFont: Boolean = false,
    val disableScreenshots: Boolean = false
)

object SettingsSerializer : Serializer<Settings> {
    private const val TAG = "SettingsSerializer"
    override val defaultValue = Settings()

    override suspend fun readFrom(input: InputStream): Settings = try {
        Json.decodeFromString(
            Settings.serializer(),
            input.readBytes().decodeToString()
        )
    } catch (e: Exception) {
        Log.e(TAG, "Failed to serialize settings! ${e.message}")
        defaultValue
    }

    override suspend fun writeTo(t: Settings, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(Json.encodeToString(Settings.serializer(), t).toByteArray())
        }
    }
}