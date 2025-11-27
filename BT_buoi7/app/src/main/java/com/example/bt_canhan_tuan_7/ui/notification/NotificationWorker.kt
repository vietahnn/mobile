package com.example.bt_canhan_tuan_7.ui.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.bt_canhan_tuan_7.data.database.AppointmentDatabase
import com.example.bt_canhan_tuan_7.data.repository.AppointmentRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppointmentDatabase.getDatabase(applicationContext)
            val repository = AppointmentRepository(database.appointmentDao())
            
            val appointments = repository.getUnsyncedNotifications().first()
            val now = LocalDateTime.now()

            for (appointment in appointments) {
                val minutesUntilAppointment = ChronoUnit.MINUTES.between(now, appointment.fromTime)
                
                // Show notification if appointment is within 30 minutes
                if (minutesUntilAppointment in 0..30) {
                    showNotification(
                        appointment.name,
                        appointment.personName,
                        appointment.location
                    )
                    repository.markNotificationSent(appointment.id)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showNotification(
        appointmentName: String,
        personName: String,
        location: String
    ) {
        val context = applicationContext
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "appointment_notifications"
        val channelName = "Appointment Notifications"

        // Create notification channel for Android 8+
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Lịch hẹn sắp tới")
            .setContentText("Bạn có cuộc hẹn '$appointmentName' với $personName tại $location trong 30 phút")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Lịch hẹn: $appointmentName\nNgười hẹn: $personName\nĐịa điểm: $location\n\nSắp bắt đầu trong 30 phút"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(appointmentName.hashCode(), notification)
    }
}
