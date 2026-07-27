package com.omawr.tuisku.components

import java.io.File
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

fun handleEncryptedData(
    data: String?,
    file: File,
    decrypt: Boolean,
    key: ByteArray,
    iv: ByteArray
): String? {
    val cipher = Cipher.getInstance("ChaCha20")
    val mode = if (decrypt) Cipher.DECRYPT_MODE else Cipher.ENCRYPT_MODE
    cipher.init(mode, SecretKeySpec(key, "ChaCha20"), IvParameterSpec(iv))

    val bytes =
        if (!decrypt) cipher.doFinal(data!!.toByteArray()) else cipher.doFinal(file.readBytes())

    if (decrypt) {
        return String(bytes)
    } else {
        file.writeBytes(bytes)
        return null
    }
}

fun generateKey(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}