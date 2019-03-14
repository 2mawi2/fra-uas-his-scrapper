package com.scrapper.his_scrapper

import android.app.ListActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TableRow
import android.widget.TextView
import com.scrapper.his_scrapper.application.DaggerMainComponent
import com.scrapper.his_scrapper.application.MainModule
import com.scrapper.his_scrapper.data.local.IGradeRepo
import com.scrapper.his_scrapper.data.local.IPreferencesRepo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var preferencesRepo: IPreferencesRepo

    @Inject
    lateinit var gradeRepo: IGradeRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDI()

        if (preferencesRepo.isUserLoggedIn()) {
            setContentView(R.layout.activity_main)

            GlobalScope.launch(Dispatchers.Main) {
                val grades = gradeRepo.getAll()
                val listItems = grades.map { "${it.name} : ${it.grade}" }.toTypedArray()

                gradeList.adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, listItems)
                sheduleNotifications()
            }

        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun sheduleNotifications() {
        val notificationIntent = Intent(applicationContext, NotificationPublisher::class.java)

        val broadCastId = 23556
        val pendingIntent = PendingIntent
            .getBroadcast(this, broadCastId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setInexactRepeating(
            AlarmManager.RTC,
            System.currentTimeMillis() + 3000, //start in 10 seconds
            AlarmManager.INTERVAL_HOUR,
            pendingIntent
        )
    }

    private fun setupDI() {
        DaggerMainComponent.builder().mainModule(MainModule(this)).build().inject(this)
    }
}
