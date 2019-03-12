package com.scrapper.his_scrapper.data.local

import android.content.Context
import com.scrapper.his_scrapper.R
import javax.inject.Inject

interface IPreferencesRepo {
    fun isUserLoggedIn(): Boolean
}

class PreferencesRepo @Inject constructor(private val context: Context) :
    IPreferencesRepo {

    override fun isUserLoggedIn(): Boolean {
        val userDetailsId = context.resources.getString(R.string.login_details)
        val sp = context.getSharedPreferences(userDetailsId, Context.MODE_PRIVATE)
        return sp.getBoolean("is_logged_in", false)
    }
}
