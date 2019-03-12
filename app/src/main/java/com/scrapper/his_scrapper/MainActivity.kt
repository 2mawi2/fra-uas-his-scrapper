package com.scrapper.his_scrapper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.scrapper.his_scrapper.application.DaggerMainComponent
import com.scrapper.his_scrapper.application.MainModule
import com.scrapper.his_scrapper.data.local.IPreferencesRepo
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var preferencesRepo: IPreferencesRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDI()

        if (preferencesRepo.isUserLoggedIn()) {
            setContentView(R.layout.activity_main)
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun setupDI() {
        DaggerMainComponent.builder().mainModule(MainModule(this)).build().inject(this)
    }
}
