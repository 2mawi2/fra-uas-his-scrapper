package com.scrapper.his_scrapper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View

import android.widget.Toast

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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

        val userStr = user.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
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
            focusView?.requestFocus()
        } else {
            showProgress(true)
            scrapeGrades(userStr, passwordStr)
        }
    }

    private fun scrapeGrades(userStr: String, passwordStr: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val grades = hisService.requestGrades(userStr, passwordStr)
            Toast.makeText(applicationContext, grades.size.toString(), Toast.LENGTH_LONG).show()
            showProgress(false)
        }
    }

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
