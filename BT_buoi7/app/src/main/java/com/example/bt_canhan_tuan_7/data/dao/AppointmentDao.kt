package com.example.bt_canhan_tuan_7.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.bt_canhan_tuan_7.data.entity.Appointment
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface AppointmentDao {
    @Insert
    suspend fun insertAppointment(appointment: Appointment): Long

    @Update
    suspend fun updateAppointment(appointment: Appointment)

    @Delete
    suspend fun deleteAppointment(appointment: Appointment)

    @Query("SELECT * FROM appointments ORDER BY fromTime DESC")
    fun getAllAppointments(): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE id = :id")
    suspend fun getAppointmentById(id: Int): Appointment?

    @Query("SELECT * FROM appointments WHERE fromTime BETWEEN :startTime AND :endTime ORDER BY fromTime ASC")
    fun getAppointmentsByTimeRange(startTime: LocalDateTime, endTime: LocalDateTime): Flow<List<Appointment>>

    @Query("SELECT * FROM appointments WHERE notificationSent = 0 ORDER BY fromTime ASC")
    fun getUnsyncedNotifications(): Flow<List<Appointment>>

    @Query("UPDATE appointments SET notificationSent = 1 WHERE id = :id")
    suspend fun markNotificationSent(id: Int)
}
