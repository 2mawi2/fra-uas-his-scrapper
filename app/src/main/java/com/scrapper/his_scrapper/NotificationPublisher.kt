package com.scrapper.his_scrapper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationPublisher : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationService.startActionCheck(context)
    }
}
