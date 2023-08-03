package me.polynom.moxplatform_android

import android.util.Log
import me.polynom.moxplatform_android.Api.CipherAlgorithm
import me.polynom.moxplatform_android.Api.CryptographyResult

import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.concurrent.thread

// A FileOutputStream that continuously hashes whatever it writes to the file.
private class HashedFileOutputStream(name: String, hashAlgorithm: String) : FileOutputStream(name) {
    private val digest: MessageDigest

    init {
        this.digest = MessageDigest.getInstance(hashAlgorithm)
    }

    override fun write(buffer: ByteArray, offset: Int, length: Int) {
        super.write(buffer, offset, length)

        digest.update(buffer, offset, length)
    }

    fun digest() : ByteArray {
        return digest.digest()
    }
}

fun getCipherSpecFromInteger(algorithm: CipherAlgorithm): String {
    return when (algorithm) {
        CipherAlgorithm.AES128GCM_NO_PADDING -> "AES_128/GCM/NoPadding"
        CipherAlgorithm.AES256GCM_NO_PADDING -> "AES_256/GCM/NoPadding"
        CipherAlgorithm.AES256CBC_PKCS7 -> "AES_256/CBC/PKCS7PADDING"
    }
}

// Compute the hash, specified by @algorithm, of the file at path @srcFile. If an exception
// occurs, returns null. If everything went well, returns the raw hash of @srcFile.
fun hashFile(srcFile: String, algorithm: String, result: Api.Result<ByteArray?>) {
    thread(start = true) {
        val buffer = ByteArray(BUFFER_SIZE)
        try {
            val digest = MessageDigest.getInstance(algorithm)
            val fInputStream = FileInputStream(srcFile)
            var length: Int

            while (true) {
                length = fInputStream.read()
                if (length <= 0) break

                // Only update the digest if we read more than 0 bytes
                digest.update(buffer, 0, length)
            }

            fInputStream.close()

            result.success(digest.digest())
        } catch (e: Exception) {
            Log.e(TAG, "[hashFile]: " + e.stackTraceToString())
            result.success(null)
        }
    }
}

// Encrypt the plaintext file at @src to @dest using the secret key @key and the IV @iv. The algorithm is chosen using @cipherAlgorithm. The file is additionally
// hashed before and after encryption using the hash algorithm specified by @hashAlgorithm.
fun encryptAndHash(src: String, dest: String, key: ByteArray, iv: ByteArray, cipherAlgorithm: CipherAlgorithm, hashAlgorithm: String, result: Api.Result<CryptographyResult?>) {
    thread(start = true) {
        val cipherSpec = getCipherSpecFromInteger(cipherAlgorithm)
        val buffer = ByteArray(BUFFER_SIZE)
        val secretKey = SecretKeySpec(key, cipherSpec)
        try {
            val digest = MessageDigest.getInstance(hashAlgorithm)
            val cipher = Cipher.getInstance(cipherSpec)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))

            val fileInputStream = FileInputStream(src)
            val fileOutputStream = HashedFileOutputStream(dest, hashAlgorithm)
            val cipherOutputStream = CipherOutputStream(fileOutputStream, cipher)

            var length: Int
            while (true) {
                length = fileInputStream.read(buffer)
                if (length <= 0) break

                digest.update(buffer, 0, length)
                cipherOutputStream.write(buffer, 0, length)
            }

            // Flush and close
            cipherOutputStream.flush()
            cipherOutputStream.close()
            fileInputStream.close()

            result.success(
                CryptographyResult().apply {
                    plaintextHash = digest.digest()
                    ciphertextHash = fileOutputStream.digest()
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "[encryptAndHash]: " + e.stackTraceToString())
            result.success(null)
        }
    }
}

// Decrypt the ciphertext file at @src to @dest using the secret key @key and the IV @iv. The algorithm is chosen using @cipherAlgorithm. The file is additionally
// hashed before and after decryption using the hash algorithm specified by @hashAlgorithm.
fun decryptAndHash(src: String, dest: String, key: ByteArray, iv: ByteArray, cipherAlgorithm: CipherAlgorithm, hashAlgorithm: String, result: Api.Result<CryptographyResult?>) {
    thread(start = true) {
        val cipherSpec = getCipherSpecFromInteger(cipherAlgorithm)
        // Shamelessly stolen from https://github.com/hugo-pcl/native-crypto-flutter/pull/3
        val buffer = ByteArray(BUFFER_SIZE)
        val secretKey = SecretKeySpec(key, cipherSpec)
        try {
            val digest = MessageDigest.getInstance(hashAlgorithm)
            val cipher = Cipher.getInstance(cipherSpec)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))

            val fileInputStream = FileInputStream(src)
            val fileOutputStream = HashedFileOutputStream(dest, hashAlgorithm)
            val cipherOutputStream = CipherOutputStream(fileOutputStream, cipher)

            // Read, decrypt, and hash until we read 0 bytes
            var length: Int
            while (true) {
                length = fileInputStream.read(buffer)
                if (length <= 0) break

                digest.update(buffer, 0, length)
                cipherOutputStream.write(buffer, 0, length)
            }

            // Flush
            cipherOutputStream.flush()
            cipherOutputStream.close()
            fileInputStream.close()

            result.success(
                CryptographyResult().apply {
                    plaintextHash = digest.digest()
                    ciphertextHash = fileOutputStream.digest()
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "[hashAndDecrypt]: " + e.stackTraceToString())
            result.success(null)
        }
    }
}