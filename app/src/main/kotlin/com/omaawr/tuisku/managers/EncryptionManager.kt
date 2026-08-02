package com.omaawr.tuisku.managers

import com.omaawr.tuisku.settings.Preferences
import kotlinx.coroutines.flow.first
import java.io.File
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.random.asKotlinRandom

class EncryptionManager(
    private val prefs: Preferences
) {
    // (secureRandom bytes wrapped in base64 for backwards compatibility)
    private val pattern = Regex(
        "^(?=.*[+/=])(?:[A-Za-z0-9+/]{4}\\n?)*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$"
    )

    private suspend fun getEncryptionKey(): ByteArray {
        val encryptionKeyIsBase64 = pattern.matches(prefs.getEncryptionKey().first())

        return if (encryptionKeyIsBase64) {
            Base64.decode(prefs.getEncryptionKey().first())
        } else {
            prefs.getEncryptionKey().first().toByteArray()
        }
    }

    private suspend fun getIvKey(): ByteArray {
        val ivKeyIsBase64 = pattern.matches(prefs.getIVKey().first())

        return if (ivKeyIsBase64) {
            Base64.decode(prefs.getIVKey().first())
        } else {
            prefs.getIVKey().first().toByteArray()
        }
    }

    suspend fun encryptFile(bytes: ByteArray, filePath: String) {
        val key = getEncryptionKey()
        val iv = getIvKey()

        val cipher = Cipher.getInstance("ChaCha20")
        val mode = Cipher.ENCRYPT_MODE
        cipher.init(mode, SecretKeySpec(key, "ChaCha20"), IvParameterSpec(iv))

        val bytes = cipher.doFinal(bytes)

        File(filePath).writeBytes(bytes)
    }

    suspend fun decryptFile(bytes: ByteArray): String {
        val key = getEncryptionKey()
        val iv = getIvKey()

        val cipher = Cipher.getInstance("ChaCha20")
        val mode = Cipher.DECRYPT_MODE
        cipher.init(mode, SecretKeySpec(key, "ChaCha20"), IvParameterSpec(iv))

        val bytes = cipher.doFinal(bytes)

        return String(bytes)
    }

    fun generateKey(length: Int): String {
        val secureRandom = SecureRandom()
        val byteArray = ByteArray(length)

        secureRandom.asKotlinRandom().nextBytes(byteArray)

        return Base64.encode(byteArray)
    }
}