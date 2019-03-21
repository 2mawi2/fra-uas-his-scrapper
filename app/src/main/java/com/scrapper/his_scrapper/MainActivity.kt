package com.scrapper.his_scrapper

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.scrapper.his_scrapper.application.DaggerMainComponent
import com.scrapper.his_scrapper.application.HisServiceResult
import com.scrapper.his_scrapper.application.MainModule
import com.scrapper.his_scrapper.application.Reason.CREDENTIALS
import com.scrapper.his_scrapper.application.Reason.PAGE
import com.scrapper.his_scrapper.application.toast
import com.scrapper.his_scrapper.data.local.IGradeRepo
import com.scrapper.his_scrapper.data.local.IPreferencesRepo
import com.scrapper.his_scrapper.data.remote.IHisService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var preferencesRepo: IPreferencesRepo

    @Inject
    lateinit var gradeRepo: IGradeRepo

    @Inject
    lateinit var hisService: IHisService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDI()
        updateActivity()
    }

    private fun updateActivity() {
        if (preferencesRepo.isUserLoggedIn()) {
            setContentView(R.layout.activity_main)
            GlobalScope.launch(Dispatchers.Main) {
                val credentials = preferencesRepo.getCredentials()
                val result = hisService.requestGrades(credentials.userName, credentials.password)
                if (result.success) {
                    gradeRepo.updateOrCreate(result.grades)
                    updateGradeList()
                    sheduleNotifications()
                    toast(applicationContext, "fetched grades...")
                } else {
                    handleFetchFailure(result)
                }
            }
        } else {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }
    }

    private suspend fun handleFetchFailure(result: HisServiceResult) {
        when (result.reason) {
            CREDENTIALS -> {
                preferencesRepo.setUserLoggedIn(false)
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
            PAGE -> {
                toast(applicationContext, "Error while fetching grades.")
                updateGradeList()
            }
        }
    }

    private suspend fun updateGradeList() {
        val grades = gradeRepo.getAll()
        val listItems = grades.map { "${it.name} : ${it.grade}" }.toTypedArray()
        gradeList.adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, listItems)
    }

    private fun sheduleNotifications() {
        val notificationIntent = Intent(applicationContext, NotificationPublisher::class.java)
        notificationIntent.action = NotificationPublisher.ACTION_STARTUP_COMPLETED

        val broadCastId = 23556
        val pendingIntent = PendingIntent
            .getBroadcast(this, broadCastId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.MINUTE, 1)
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60 * 60, // 60 minutes
            pendingIntent
        )
    }

    private fun setupDI() {
        DaggerMainComponent.builder().mainModule(MainModule(applicationContext)).build().inject(this)
    }
}
