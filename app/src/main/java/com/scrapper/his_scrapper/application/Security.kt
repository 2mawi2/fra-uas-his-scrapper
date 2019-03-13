package com.scrapper.his_scrapper.application

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec


interface IDecryptor {
    fun decryptData(encryptedData: String, encryptionIv: String): String
}

class Decryptor : IDecryptor {

    private val alias = "his_scrapper_pref"
    private var keyStore: KeyStore? = null

    init {
        initKeyStore()
    }

    private fun initKeyStore() {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore!!.load(null)
    }

    override fun decryptData(encryptedData: String, encryptionIv: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(
            128, Base64.decode(
                encryptionIv,
                Base64.DEFAULT
            )
        )
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(alias), spec)

        return kotlinx.io.core.String(
            cipher.doFinal(
                Base64.decode(
                    encryptedData,
                    Base64.DEFAULT
                )
            )
        )
    }

    private fun getSecretKey(alias: String): SecretKey {
        return (keyStore!!.getEntry(alias, null) as KeyStore.SecretKeyEntry).secretKey
    }

    companion object {

        private val TRANSFORMATION = "AES/GCM/NoPadding"
        private val ANDROID_KEY_STORE = "AndroidKeyStore"
    }
}


interface IEncryptor {
    fun encryptText(textToEncrypt: String): EncryptionResult
}

data class EncryptionResult(
    val encryptedData: String,
    val iv: String
)

class Encryptor : IEncryptor {
    private val alias = "his_scrapper_pref"

    override fun encryptText(textToEncrypt: String): EncryptionResult {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

        return EncryptionResult(
            encryptedData = Base64.encodeToString(
                cipher.doFinal(textToEncrypt.toByteArray(charset("UTF-8"))),
                Base64.DEFAULT
            ),
            iv = Base64.encodeToString(
                cipher.iv,
                Base64.DEFAULT
            )
        )
    }

    private fun getSecretKey(): SecretKey {

        val keyGenerator = KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)

        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )

        return keyGenerator.generateKey()
    }

    companion object {
        private val TRANSFORMATION = "AES/GCM/NoPadding"
        private val ANDROID_KEY_STORE = "AndroidKeyStore"
    }
}