package com.example.bt_canhan_tuan_7.data.repository

import com.example.bt_canhan_tuan_7.data.dao.AppointmentDao
import com.example.bt_canhan_tuan_7.data.entity.Appointment
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class AppointmentRepository(private val appointmentDao: AppointmentDao) {
    
    fun getAllAppointments(): Flow<List<Appointment>> {
        return appointmentDao.getAllAppointments()
    }

    suspend fun insertAppointment(appointment: Appointment): Long {
        return appointmentDao.insertAppointment(appointment)
    }

    suspend fun updateAppointment(appointment: Appointment) {
        appointmentDao.updateAppointment(appointment)
    }

    suspend fun deleteAppointment(appointment: Appointment) {
        appointmentDao.deleteAppointment(appointment)
    }

    suspend fun getAppointmentById(id: Int): Appointment? {
        return appointmentDao.getAppointmentById(id)
    }

    fun getAppointmentsByTimeRange(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Flow<List<Appointment>> {
        return appointmentDao.getAppointmentsByTimeRange(startTime, endTime)
    }

    fun getUnsyncedNotifications(): Flow<List<Appointment>> {
        return appointmentDao.getUnsyncedNotifications()
    }

    suspend fun markNotificationSent(id: Int) {
        appointmentDao.markNotificationSent(id)
    }
}
