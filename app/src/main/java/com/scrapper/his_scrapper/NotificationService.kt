package com.scrapper.his_scrapper

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.scrapper.his_scrapper.application.DaggerMainComponent
import com.scrapper.his_scrapper.application.MainModule
import com.scrapper.his_scrapper.data.local.IGradeRepo
import com.scrapper.his_scrapper.data.local.IPreferencesRepo
import com.scrapper.his_scrapper.data.remote.IHisService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs


private const val ACTION_CHECK = "com.scrapper.his_scrapper.action.CHECK"

class NotificationService : IntentService("NotificationService") {

    @Inject
    lateinit var hisService: IHisService

    @Inject
    lateinit var gradeRepo: IGradeRepo

    @Inject
    lateinit var preferencesRepo: IPreferencesRepo


    override fun onCreate() {
        super.onCreate()
        DaggerMainComponent.builder().mainModule(MainModule(this)).build().inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        try {
            when (intent?.action) {
                ACTION_CHECK -> {
                    handleActionCheck()
                }
            }
        } catch (e: Exception) {
            Log.e("Fetching failed.", e.message)
        }
    }

    private fun handleActionCheck() {
        if (preferencesRepo.isUserLoggedIn().not()) {
            return
        }

        GlobalScope.launch(Dispatchers.Default) {
            val credentials = preferencesRepo.getCredentials()
            val result = hisService.requestGrades(credentials.userName, credentials.password)
            val grades = gradeRepo.getAll()

            val diff = abs(result.grades.size - result.grades.size)

            if (diff > 0) {
                notifyUser(diff)
                gradeRepo.updateOrCreate(grades)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel("com.scraper.his_scrapper.id", "com.scraper.his_scrapper.name", importance).apply {
                    description = "com.scraper.his_scrapper.description"
                }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun notifyUser(diff: Int) {
        createNotificationChannel()

        val builder = NotificationCompat.Builder(this, "com.scraper.his_scrapper.id")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("New HISQIS grade!")
            .setContentText("$diff new grades available.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(1234567, builder.build())
        }

    }

    companion object {
        @JvmStatic
        fun startActionCheck(context: Context) {
            val intent = Intent(context, NotificationService::class.java).apply {
                action = ACTION_CHECK
            }
            context.startService(intent)
        }
    }
}
