package com.omaawr.tuisku.managers

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.omaawr.tuisku.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.io.encoding.Base64

/**
 * Port manager.. used for exporting/importing notes onto Tuisku
 *
 * @param ctx - Context, provided by Koin
 * @param encryptionManager - Encryption manager used to decrypt bytes before exporting them, or encrypting after getting the
 * plaintext bytes over
 * @since 1.3.0
 */
class PortManager(
    private val ctx: Context,
    private val encryptionManager: EncryptionManager
) {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun importNotes(input: Uri) {
        val inputStream = BufferedInputStream(ctx.contentResolver.openInputStream(input))

        ZipInputStream(inputStream).use { zis ->
            var entry = zis.nextEntry

            while (entry != null) {
                if (entry.isDirectory || !entry.name.contains(".encrypted-note")) continue

                val outputFile = File(ctx.filesDir, entry.name)

                val fos = FileOutputStream(outputFile)
                zis.copyTo(fos)

                fos.close()
                zis.closeEntry()

                entry = zis.nextEntry
            }
            zis.close()
        }

        Toast.makeText(ctx, ctx.getString(R.string.notes_imported), Toast.LENGTH_SHORT).show()
    }

    fun exportNotes(out: Uri) {
        val files = ctx.filesDir.listFiles()!!.filter { it.name.contains(".encrypted-note") }

        ZipOutputStream(ctx.contentResolver.openOutputStream(out)).use { out ->
            files.forEach { file ->
                FileInputStream(file).use { fis ->
                    val zipEntry = ZipEntry(file.name)
                    out.putNextEntry(zipEntry)

                    fis.copyTo(out)
                    out.closeEntry()
                }
            }
        }

        Toast.makeText(ctx, ctx.getString(R.string.notes_exported), Toast.LENGTH_SHORT).show()
    }

    fun exportUnencryptedNotes(out: Uri) {
        val files = ctx.filesDir.listFiles()!!.filter { it.name.contains(".encrypted-note") }

        scope.launch {
            ZipOutputStream(ctx.contentResolver.openOutputStream(out)).use { out ->
                files.forEach { file ->
                    val decryptedBytes = encryptionManager.decryptBytes(file.readBytes())
                    val decryptedFilename =
                        encryptionManager.decryptBytes(
                            Base64.UrlSafe.decode(file.nameWithoutExtension)
                        )

                    val zipEntry = ZipEntry("$decryptedFilename.txt")
                    out.putNextEntry(zipEntry)

                    out.write(decryptedBytes.toByteArray())
                    out.closeEntry()
                }
            }
        }

        Toast.makeText(ctx, ctx.getString(R.string.notes_exported), Toast.LENGTH_SHORT).show()
    }

    fun importUnencryptedNotes(input: Uri) {
        val inputStream = BufferedInputStream(ctx.contentResolver.openInputStream(input))

        scope.launch {
            ZipInputStream(inputStream).use { zis ->
                var entry = zis.nextEntry

                while (entry != null) {
                    if (entry.isDirectory || !entry.name.contains(".txt")) continue

                    val outputFile = File(ctx.filesDir, entry.name)

                    val fos = FileOutputStream(outputFile)
                    zis.copyTo(fos)

                    encryptionManager.encryptFile(outputFile.readBytes(), outputFile.path)
                    encryptionManager.renameFile(outputFile.nameWithoutExtension, outputFile)

                    fos.close()
                    zis.closeEntry()

                    entry = zis.nextEntry
                }
                zis.close()
            }
        }
    }
}