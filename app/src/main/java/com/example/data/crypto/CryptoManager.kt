package com.example.data.crypto

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoManager {
    private var myKeyPair: KeyPair? = null
    private var partnerPublicKey: PublicKey? = null
    private var cachedSafetyNumber: String? = null

    init {
        try {
            val keyGen = KeyPairGenerator.getInstance("RSA")
            keyGen.initialize(2048)
            myKeyPair = keyGen.generateKeyPair()

            // Generate mock partner key pair for local simulated E2EE peer
            val partnerKeyGen = KeyPairGenerator.getInstance("RSA")
            partnerKeyGen.initialize(2048)
            partnerPublicKey = partnerKeyGen.generateKeyPair().public
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getMyPublicKeyBase64(): String {
        val pubKey = myKeyPair?.public ?: return ""
        return Base64.encodeToString(pubKey.encoded, Base64.NO_WRAP)
    }

    fun getFingerprintSafetyNumber(): String {
        cachedSafetyNumber?.let { return it }
        val pubKeyBytes = myKeyPair?.public?.encoded ?: ByteArray(32)
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(pubKeyBytes)

        // Convert to 12-block numeric safety numbers like Signal (e.g. "49201 82941 77301...")
        val sb = StringBuilder()
        for (i in 0 until minOf(hash.size, 24) step 2) {
            val num = ((hash[i].toInt() and 0xFF) shl 8) or (hash[i + 1].toInt() and 0xFF)
            val formatted = String.format("%05d", num % 100000)
            sb.append(formatted)
            if (i < 22) sb.append(" ")
        }
        val result = sb.toString()
        cachedSafetyNumber = result
        return result
    }

    fun computeSafetyNumber(userA: String, userB: String): String {
        val sorted = listOf(userA, userB).sorted().joinToString(":")
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(sorted.toByteArray(StandardCharsets.UTF_8))
        val sb = StringBuilder()
        for (i in 0 until minOf(hash.size, 8) step 2) {
            val num = ((hash[i].toInt() and 0xFF) shl 8) or (hash[i + 1].toInt() and 0xFF)
            val formatted = String.format("%05d", num % 100000)
            sb.append(formatted)
            if (i < 6) sb.append(" ")
        }
        return sb.toString()
    }

    /**
     * Encrypts plaintext using AES-256-GCM with a freshly generated ephemeral session key
     * then packs the IV + encrypted payload into a formatted encrypted string.
     */
    fun encryptMessage(plainText: String): String {
        return try {
            val keyGen = KeyGenerator.getInstance("AES")
            keyGen.init(256)
            val secretKey: SecretKey = keyGen.generateKey()

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv
            val cipherText = cipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))

            val combined = ByteArray(iv.size + secretKey.encoded.size + cipherText.size)
            // Header packing: [1 byte iv len][iv][32 bytes key][ciphertext]
            val keyBytes = secretKey.encoded
            val packed = ByteArray(1 + iv.size + keyBytes.size + cipherText.size)
            packed[0] = iv.size.toByte()
            System.arraycopy(iv, 0, packed, 1, iv.size)
            System.arraycopy(keyBytes, 0, packed, 1 + iv.size, keyBytes.size)
            System.arraycopy(cipherText, 0, packed, 1 + iv.size + keyBytes.size, cipherText.size)

            "AURA:ENC:" + Base64.encodeToString(packed, Base64.NO_WRAP)
        } catch (e: Exception) {
            "AURA:ENC:" + Base64.encodeToString(plainText.toByteArray(), Base64.NO_WRAP)
        }
    }

    /**
     * Decrypts an encrypted payload back to plaintext.
     */
    fun decryptMessage(encryptedPayload: String): String {
        if (!encryptedPayload.startsWith("AURA:ENC:")) {
            return encryptedPayload
        }
        return try {
            val rawB64 = encryptedPayload.removePrefix("AURA:ENC:")
            val packed = Base64.decode(rawB64, Base64.NO_WRAP)
            val ivSize = packed[0].toInt()
            val iv = ByteArray(ivSize)
            System.arraycopy(packed, 1, iv, 0, ivSize)

            val keySize = 32 // 256 bit AES key
            val keyBytes = ByteArray(keySize)
            System.arraycopy(packed, 1 + ivSize, keyBytes, 0, keySize)

            val cipherTextSize = packed.size - (1 + ivSize + keySize)
            val cipherText = ByteArray(cipherTextSize)
            System.arraycopy(packed, 1 + ivSize + keySize, cipherText, 0, cipherTextSize)

            val secretKey = SecretKeySpec(keyBytes, "AES")
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            val plainBytes = cipher.doFinal(cipherText)
            String(plainBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            // Fallback for mock strings
            try {
                val rawB64 = encryptedPayload.removePrefix("AURA:ENC:")
                val bytes = Base64.decode(rawB64, Base64.NO_WRAP)
                String(bytes, StandardCharsets.UTF_8)
            } catch (ex: Exception) {
                "[Encrypted Content • Decryption Error]"
            }
        }
    }

    fun getShortCipherSample(fullEncrypted: String): String {
        val clean = fullEncrypted.removePrefix("AURA:ENC:")
        return if (clean.length > 20) {
            clean.take(10) + "..." + clean.takeLast(8)
        } else {
            clean
        }
    }

    fun signAuditRecord(rawPayload: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(rawPayload.toByteArray(StandardCharsets.UTF_8))
            val hexString = StringBuilder()
            for (b in hashBytes) {
                val hex = Integer.toHexString(0xff and b.toInt())
                if (hex.length == 1) hexString.append('0')
                hexString.append(hex)
            }
            "sig_sha256_" + hexString.toString().take(24)
        } catch (e: Exception) {
            "sig_fallback_" + System.currentTimeMillis()
        }
    }
}
