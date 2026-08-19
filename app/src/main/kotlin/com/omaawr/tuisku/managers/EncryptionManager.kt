package com.omaawr.tuisku.managers

import android.content.Context
import com.omaawr.tuisku.settings.Preferences
import kotlinx.coroutines.flow.first
import java.io.File
import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.random.asKotlinRandom

class EncryptionManager(
    private val prefs: Preferences,
    private val context: Context
) {
    // (secureRandom bytes wrapped in base64 for backwards compatibility)
    private val pattern = Regex(
        "^(?=.*[+/=])(?:[A-Za-z0-9+/]{4}\\n?)*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$"
    )

    private suspend fun getEncryptionKey(): ByteArray {
        val encryptionKeyIsBase64 = prefs.getEncryptionKey().first().length > 32 && pattern.matches(
            prefs.getEncryptionKey().first()
        )

        return if (encryptionKeyIsBase64) {
            Base64.decode(prefs.getEncryptionKey().first())
        } else {
            prefs.getEncryptionKey().first().toByteArray()
        }
    }

    private fun getRandomNonce(): ByteArray {
        val secureRandom = SecureRandom()
        val byteArray = ByteArray(12)

        secureRandom.asKotlinRandom().nextBytes(byteArray)

        return byteArray
    }

    suspend fun migrateFile(index: Int, file: File) {
        val key = getEncryptionKey()
        val oldNonce = Base64.decode(prefs.getIVKey().first()) // somehow i was super dumb at security at first so i thought iv was something to keep hidden when it was actually a nonce..

        val cipher = Cipher.getInstance("ChaCha20")
        val mode = Cipher.DECRYPT_MODE
        cipher.init(mode, SecretKeySpec(key, "ChaCha20"), IvParameterSpec(oldNonce))

        val bytes = cipher.doFinal(file.readBytes())
        val newFile = File(context.filesDir, "new-note-$index.migrated-note")

        val encryptedFilename = encryptFilename(newFile.nameWithoutExtension.toByteArray())

        newFile.writeBytes(bytes)
        encryptFile(newFile.readBytes(), newFile.path)
        file.delete()

        newFile.renameTo(
            File(context.filesDir, "$encryptedFilename.encrypted-note")
        )
    }

    suspend fun encryptFile(plain: ByteArray, filePath: String) {
        val key = getEncryptionKey()
        val cipher = Cipher.getInstance("ChaCha20")

        val iv = getRandomNonce()

        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "ChaCha20"), IvParameterSpec(iv))
        val ciphered = cipher.doFinal(plain)

        File(filePath).writeBytes(ByteBuffer.allocate(ciphered.size + 12)
            .put(ciphered)
            .put(iv)
            .array())
    }

    suspend fun encryptFilename(bytes: ByteArray): String {
        val key = getEncryptionKey()
        val cipher = Cipher.getInstance("ChaCha20")

        val iv = getRandomNonce()

        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "ChaCha20"), IvParameterSpec(iv))
        val ciphered = cipher.doFinal(bytes)

        return Base64.UrlSafe.encode(ByteBuffer.allocate(ciphered.size + 12)
            .put(ciphered)
            .put(iv)
            .array())
    }

    suspend fun decryptFile(bytes: ByteArray): String {
        val key = getEncryptionKey()
        val buffer: ByteBuffer = ByteBuffer.wrap(bytes)

        val encryptedText = ByteArray(bytes.size - 12)
        val nonce = ByteArray(12)
        buffer.get(encryptedText)
        buffer.get(nonce)

        val iv = IvParameterSpec(nonce)
        val cipher = Cipher.getInstance("ChaCha20")
        val mode = Cipher.DECRYPT_MODE
        cipher.init(mode, SecretKeySpec(key, "ChaCha20"), iv)

        return String(cipher.doFinal(encryptedText))
    }

    fun generateKey(length: Int): String {
        val secureRandom = SecureRandom()
        val byteArray = ByteArray(length)

        secureRandom.asKotlinRandom().nextBytes(byteArray)

        return Base64.encode(byteArray)
    }
}