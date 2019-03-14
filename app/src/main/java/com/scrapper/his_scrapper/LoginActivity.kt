package com.scrapper.his_scrapper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View

import android.widget.Toast
import com.scrapper.his_scrapper.application.DaggerMainComponent
import com.scrapper.his_scrapper.application.MainModule
import com.scrapper.his_scrapper.application.toast
import com.scrapper.his_scrapper.data.local.Credentials
import com.scrapper.his_scrapper.data.local.IGradeRepo
import com.scrapper.his_scrapper.data.local.IPreferencesRepo
import com.scrapper.his_scrapper.data.remote.IHisService

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var hisService: IHisService

    @Inject
    lateinit var preferencesRepo: IPreferencesRepo

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
        user.error = null
        password.error = null

        val userStr = user.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        if (!TextUtils.isEmpty(passwordStr) && !isPasswordValid(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

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
            login(userStr, passwordStr)
        }
    }

    private fun login(userStr: String, passwordStr: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val validCredentials = hisService.checkCredentials(userStr, passwordStr)

            if (validCredentials) {
                preferencesRepo.storeCredentials(Credentials(userStr, passwordStr))
                preferencesRepo.setUserLoggedIn(true)
                showProgress(false)
                startActivity(Intent(applicationContext, MainActivity::class.java))
            } else {
                preferencesRepo.setUserLoggedIn(false)
                showProgress(false)
                toast(applicationContext, "Invalid credentials")
            }
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
