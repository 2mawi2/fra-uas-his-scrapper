package com.scrapper.his_scrapper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView

import java.util.ArrayList
import android.Manifest.permission.READ_CONTACTS
import android.webkit.URLUtil
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject


class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var hisService: IHisService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDI()
        setContentView(R.layout.activity_login)

        email_sign_in_button.setOnClickListener { attemptLogin() }
    }

    private fun setupDI() {
        DaggerMainComponent.builder().mainModule(MainModule(this)).build().inject(this)
    }


    private fun attemptLogin() {

        // Reset errors.
        user.error = null
        password.error = null
        domain.error = null


        // Store values at the time of the requestGrades attempt.
        val userStr = user.text.toString()
        val passwordStr = password.text.toString()
        val domainStr = domain.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        if (!isDomainValid(domainStr)) {
            domain.error = getString(R.string.error_invalid_domain)
            focusView = password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(userStr)) {
            user.error = getString(R.string.error_field_required)
            focusView = user
            cancel = true
        } else if (!isUserValid(userStr)) {
            user.error = getString(R.string.error_invalid_email)
            focusView = user
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt requestGrades and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user requestGrades attempt.
            showProgress(true)
            login(domainStr, userStr, passwordStr)
        }
    }

    private fun login(domainStr: String, userStr: String, passwordStr: String) {
        Toast.makeText(this, "done",Toast.LENGTH_LONG).show()
        showProgress(false)
    }

    private fun isDomainValid(domainStr: String): Boolean = URLUtil.isValidUrl(domainStr)

    private fun isUserValid(user: String): Boolean = user.length > 2

    private fun isPasswordValid(password: String): Boolean = password.length > 4

    private fun showProgress(show: Boolean) {
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        login_form.visibility = if (show) View.GONE else View.VISIBLE
        login_form.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 0 else 1).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_form.visibility = if (show) View.GONE else View.VISIBLE
                }
            })

        login_progress.visibility = if (show) View.VISIBLE else View.GONE
        login_progress.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 1 else 0).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_progress.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
    }
}
