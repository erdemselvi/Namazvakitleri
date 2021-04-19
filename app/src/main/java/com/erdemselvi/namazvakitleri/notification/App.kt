package com.erdemselvi.namazvakitleri.notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.getSystemService

class App: Application() {
    companion object {
        val CHANNEL_ID="Namaz Vakitleri"
    }

    @Override
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val serviceChannel=NotificationChannel(
                    CHANNEL_ID,
                    "Namaz Vakitleri Kanalı",
                    NotificationManager.IMPORTANCE_HIGH
            )
            //val manager: NotificationManager =getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val manager: NotificationManager =getSystemService(NotificationManager::class.java)
                manager.createNotificationChannel(serviceChannel)
        }
    }

}