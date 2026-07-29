package com.omaawr.tuisku.components

import com.omaawr.tuisku.settings.Preferences
import kotlinx.coroutines.flow.first
import java.io.File
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncryptionManager(
    private val prefs: Preferences
) {
    suspend fun encryptFile(bytes: ByteArray, filePath: String) {
        val key = prefs.getEncryptionKey().first().toByteArray()
        val iv = prefs.getIVKey().first().toByteArray()

        val cipher = Cipher.getInstance("ChaCha20")
        val mode = Cipher.ENCRYPT_MODE
        cipher.init(mode, SecretKeySpec(key, "ChaCha20"), IvParameterSpec(iv))

        val bytes = cipher.doFinal(bytes)

        File(filePath).writeBytes(bytes)
    }

    suspend fun decryptFile(bytes: ByteArray): String {
        val key = prefs.getEncryptionKey().first().toByteArray()
        val iv = prefs.getIVKey().first().toByteArray()

        val cipher = Cipher.getInstance("ChaCha20")
        val mode = Cipher.DECRYPT_MODE
        cipher.init(mode, SecretKeySpec(key, "ChaCha20"), IvParameterSpec(iv))

        val bytes = cipher.doFinal(bytes)

        return String(bytes)
    }

    fun generateKey(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}