package com.example.buoi9_cn.ui.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.buoi9_cn.R

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_HO_VA_TEN = "extra_ho_va_ten"
        const val EXTRA_NOI_DUNG = "extra_noi_dung"
        const val EXTRA_LICH_ID = "extra_lich_id"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val bundle = intent.extras
            val hoVaTen = bundle?.getString(EXTRA_HO_VA_TEN) ?: ""
            val noiDung = bundle?.getString(EXTRA_NOI_DUNG) ?: ""

            showNotification(context, hoVaTen, noiDung)
        }
    }

    private fun showNotification(context: Context, title: String, content: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "lich_hen_channel"

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Lịch Hẹn Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for upcoming appointments"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Lịch hẹn với $title")
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationId = if (EXTRA_LICH_ID.isNotEmpty()) EXTRA_LICH_ID.hashCode() else System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notification)
    }
}

