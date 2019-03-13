package com.scrapper.his_scrapper.data.local

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.scrapper.his_scrapper.application.IDecryptor
import com.scrapper.his_scrapper.application.IEncryptor
import javax.inject.Inject


interface IPreferencesRepo {
    fun isUserLoggedIn(): Boolean
    fun storeCredentials(credentials: Credentials)
    fun setUserLoggedIn(isLoggedIn: Boolean)
    fun getCredentials(): Credentials
}

data class Credentials(
    val userName: String,
    val password: String
)

class PreferencesRepo @Inject constructor(
    private val context: Context,
    private val decryptor: IDecryptor,
    private val encryptor: IEncryptor
) : IPreferencesRepo {

    private fun getLoginDetails(): SharedPreferences {
        val userDetailsId = context.resources.getString(com.scrapper.his_scrapper.R.string.login_details)
        val sp = context.getSharedPreferences(userDetailsId, Context.MODE_PRIVATE)
        return sp
    }

    override fun getCredentials(): Credentials {
        val userName = getLoginDetails().getString("user_name", "")
        val password = getLoginDetails().getString("user_password", "")
        val iv = getLoginDetails().getString("user_password_iv", "")

        if (userName.isNullOrEmpty() || password.isNullOrEmpty() || iv.isNullOrEmpty()) {
            throw Resources.NotFoundException("no credentials found")
        }

        return Credentials(
            userName = userName,
            password = decryptor.decryptData(password, iv)
        )
    }

    override fun setUserLoggedIn(isLoggedIn: Boolean) {
        getLoginDetails().edit().putBoolean("is_logged_in", isLoggedIn).apply()
    }

    override fun storeCredentials(credentials: Credentials) {
        val encryptionResult = encryptor.encryptText(credentials.password)

        getLoginDetails().edit()
            .putString("user_name", credentials.userName)
            .putString("user_password", encryptionResult.encryptedData)
            .putString("user_password_iv", encryptionResult.iv)
            .apply()
    }

    override fun isUserLoggedIn(): Boolean {
        return getLoginDetails().getBoolean("is_logged_in", false)
    }
}
