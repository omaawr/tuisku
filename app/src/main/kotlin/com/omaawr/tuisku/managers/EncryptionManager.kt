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

/**
 * Tuisku's Encryption manager, handiling encryption/decryption (obviously)
 *
 * @param context - Android context (provided by dependency injection, Koin)
 * @param prefs - Preferences, for getting the master key (also known as the encryption key)
 */
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

    /**
     * Migrating notes with the critical security flaw (nonces being used twice) to secure notes by decrypting bytes and reencrypting them to a secure one
     *
     * (somehow i didnt notice this for a while because i was very dumb at security guh)
     * @param index - Current index of the note
     * @param file - Note file (obviously)
     * @since 1.2.0
     */
    suspend fun migrateFile(index: Int, file: File) {
        val key = getEncryptionKey()
        val oldNonce = Base64.decode(prefs.getIVKey().first()) // somehow i was super dumb at security at first so i thought iv was something to keep hidden when it was actually a nonce..

        val cipher = Cipher.getInstance("ChaCha20").apply {
            init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "ChaCha20"), IvParameterSpec(oldNonce))
        }

        val bytes = cipher.doFinal(file.readBytes())
        val newFile = File(context.filesDir, "new-note-$index.migrated-note")

        val encryptedFilename = encryptFilename(newFile.nameWithoutExtension.toByteArray())

        encryptFile(bytes, newFile.path)
        file.delete()

        newFile.renameTo(
            File(context.filesDir, "$encryptedFilename.encrypted-note")
        )
    }

    /**
     * Encrypting a note's content
     *
     * @param plain - Bytes to be encrypted with the ChaCha20 algorithm (and also the nonce block of course)
     * @param filePath - Path to the file to write encrypted bytes to
     */
    suspend fun encryptFile(plain: ByteArray, filePath: String) {
        val key = getEncryptionKey()
        val iv = getRandomNonce()

        val cipher = Cipher.getInstance("ChaCha20").apply {
            init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "ChaCha20"), IvParameterSpec(iv))
        }

        val ciphered = cipher.doFinal(plain)

        File(filePath).writeBytes(ByteBuffer.allocate(ciphered.size + 12)
            .put(ciphered)
            .put(iv)
            .array())
    }

    /**
     * Encrypting a note's filename, same as encryptFile() but it returns Base64 Urlsafe encoded bytes
     *
     * @param bytes - Bytes to be encrypted with the ChaCha20 algorithm (and also the nonce block of course)
     */
    suspend fun encryptFilename(bytes: ByteArray): String {
        val key = getEncryptionKey()
        val iv = getRandomNonce()

        val cipher = Cipher.getInstance("ChaCha20").apply {
            init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "ChaCha20"), IvParameterSpec(iv))
        }

        val ciphered = cipher.doFinal(bytes)

        return Base64.UrlSafe.encode(ByteBuffer.allocate(ciphered.size + 12)
            .put(ciphered)
            .put(iv)
            .array())
    }

    /**
     * Decrypting a note's content
     *
     * @param bytes - Bytes to be encrypted with the ChaCha20 algorithm (and also the nonce block of course)
     * @return The decrypted content
     */
    suspend fun decryptBytes(bytes: ByteArray): String {
        val key = getEncryptionKey()
        val buffer: ByteBuffer = ByteBuffer.wrap(bytes)

        val encryptedText = ByteArray(bytes.size - 12)
        val nonce = ByteArray(12)
        buffer.get(encryptedText)
        buffer.get(nonce)

        val iv = IvParameterSpec(nonce)
        val cipher = Cipher.getInstance("ChaCha20").apply {
            init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "ChaCha20"), iv)
        }

        return String(cipher.doFinal(encryptedText))
    }

    /**
     * Write a new blank note (with a nonce block of course)
     *
     * @param filename - Filename to use
     * @since 1.2.1
     */
    suspend fun newFile(filename: String) {
        val nonce = getRandomNonce()
        val filename = encryptFilename(filename.toByteArray())

        File(context.filesDir, "$filename.encrypted-note").writeBytes(nonce)
    }

    /**
     * Rename a note
     *
     * @param newFilename - Filename to use
     * @since 1.2.1
     */
    suspend fun renameFile(newFilename: String, file: File) {
        val filename = encryptFilename(newFilename.toByteArray())

        file.renameTo(
            File(context.filesDir, "$filename.encrypted-note")
        )
    }

    /**
     * Generate a key with SecureRandom
     */
    fun generateKey(): String {
        val secureRandom = SecureRandom()
        val byteArray = ByteArray(32)

        secureRandom.asKotlinRandom().nextBytes(byteArray)

        return Base64.encode(byteArray)
    }
}