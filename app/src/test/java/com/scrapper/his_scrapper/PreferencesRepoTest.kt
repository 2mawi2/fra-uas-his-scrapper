package com.scrapper.his_scrapper

import android.content.Context
import org.junit.Test

import android.content.SharedPreferences
import android.content.res.Resources
import com.scrapper.his_scrapper.data.local.IPreferencesRepo
import com.scrapper.his_scrapper.data.local.PreferencesRepo
import org.mockito.Mockito.*


class PreferencesRepoTest {
    val mockContext: Context = mock(Context::class.java)
    val mockResources: Resources = mock(Resources::class.java)
    val mockSharedPreferences: SharedPreferences = mock(SharedPreferences::class.java)

    fun create(): IPreferencesRepo {
        `when`(mockContext.resources).thenReturn(mockResources)
        `when`(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences)
        return PreferencesRepo(mockContext)
    }

    @Test
    fun `should retrieve is_logged_in`() {
        `when`(mockResources.getString(R.string.login_details)).thenReturn("user_details")

        create().isUserLoggedIn()

        verify(mockSharedPreferences).getBoolean("is_logged_in", false)
    }
}