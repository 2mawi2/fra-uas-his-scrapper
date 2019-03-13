package com.scrapper.his_scrapper

import android.content.Context
import org.junit.Test

import android.content.SharedPreferences
import android.content.res.Resources
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.scrapper.his_scrapper.application.EncryptionResult
import com.scrapper.his_scrapper.application.IDecryptor
import com.scrapper.his_scrapper.application.IEncryptor
import com.scrapper.his_scrapper.data.local.Credentials
import com.scrapper.his_scrapper.data.local.IPreferencesRepo
import com.scrapper.his_scrapper.data.local.PreferencesRepo
import com.scrapper.his_scrapper.testUtils.anything
import org.amshove.kluent.shouldBeEqualTo
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*


class PreferencesRepoTest {


    val mockContext: Context = mock()
    val mockResources: Resources = mock()
    val mockSharedPreferences: SharedPreferences = mock()
    val mockEncryptor: IEncryptor = mock()
    val mockDecryptor: IDecryptor = mock()
    val mockSharedPreferencesEditor: SharedPreferences.Editor = mock()

    fun create(): IPreferencesRepo {
        whenever(mockContext.resources).thenReturn(mockResources)
        whenever(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences)
        whenever(mockSharedPreferences.edit()).thenReturn(mockSharedPreferencesEditor)
        whenever(mockSharedPreferencesEditor.putBoolean(anyString(), anyBoolean())).thenReturn(
            mockSharedPreferencesEditor
        )
        whenever(mockSharedPreferencesEditor.putString(anyString(), anyString())).thenReturn(
            mockSharedPreferencesEditor
        )
        whenever(mockEncryptor.encryptText(ArgumentMatchers.anyString())).thenReturn(
            EncryptionResult(
                encryptedData = "encryptedPassword",
                iv = "iv"
            )
        )
        whenever(mockDecryptor.decryptData(anything(), anything())).thenReturn(
            "decryptedPassword"
        )
        whenever(mockResources.getString(R.string.login_details)).thenReturn("user_details")
        return PreferencesRepo(mockContext, encryptor = mockEncryptor, decryptor = mockDecryptor)
    }

    @Test
    fun `should retrieve is_logged_in`() {
        create().isUserLoggedIn()

        verify(mockSharedPreferences).getBoolean("is_logged_in", false)
    }

    @Test
    fun `should set logged in flag`() {
        create().setUserLoggedIn(true)

        verify(mockSharedPreferencesEditor).putBoolean("is_logged_in", true)
        verify(mockSharedPreferencesEditor).apply()
    }

    @Test
    fun `should store credentials`() {
        create().storeCredentials(Credentials("user", "password"))

        verify(mockSharedPreferencesEditor).putString("user_name", "user")
        verify(mockSharedPreferencesEditor).putString("user_password", "encryptedPassword")
        verify(mockSharedPreferencesEditor).apply()
    }

    @Test
    fun `should retrieve stored credentials`() {
        whenever(mockSharedPreferences.getString("user_name", "")).thenReturn("userName")
        whenever(mockSharedPreferences.getString("user_password", "")).thenReturn("user_password")
        whenever(mockSharedPreferences.getString("user_password_iv", "")).thenReturn("user_password_iv")

        val credentials = create().getCredentials()

        credentials.userName.shouldBeEqualTo("userName")
        credentials.password.shouldBeEqualTo("decryptedPassword")
    }


}