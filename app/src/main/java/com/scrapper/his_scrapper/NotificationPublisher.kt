package com.scrapper.his_scrapper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationPublisher : BroadcastReceiver() {
    companion object {
        const val ACTION_STARTUP_COMPLETED = "ACTION_STARTUP_COMPLETED"
    }

    override fun onReceive(context: Context, intent: Intent) {
        intent.action?.let {
            when (it) {
                Intent.ACTION_BOOT_COMPLETED, ACTION_STARTUP_COMPLETED -> NotificationService.startActionCheck(context)
            }
        }
    }
}
