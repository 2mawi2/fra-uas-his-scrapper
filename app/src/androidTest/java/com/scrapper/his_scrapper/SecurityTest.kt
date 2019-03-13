package com.scrapper.his_scrapper

import androidx.test.runner.AndroidJUnit4
import com.scrapper.his_scrapper.application.Decryptor
import com.scrapper.his_scrapper.application.Encryptor
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class SecurityTest {

    @Test
    fun shouldEncryptData() {
        val encryptor = Encryptor()

        val unecrypted = "unencrypted"

        val result = encryptor.encryptText(unecrypted)

        result.encryptedData.shouldNotBeEqualTo(unecrypted)
    }

    @Test
    fun shouldDecryptData() {
        val encryptor = Encryptor()
        val decryptor = Decryptor()

        val unecrypted = "unencrypted"

        val result = encryptor.encryptText(unecrypted)

        val decrypted = decryptor.decryptData(result.encryptedData, result.iv)

        decrypted.shouldBeEqualTo(unecrypted)
    }
}