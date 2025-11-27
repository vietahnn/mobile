package com.example.bt_canhan_tuan_7.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.bt_canhan_tuan_7.data.dao.AppointmentDao
import com.example.bt_canhan_tuan_7.data.entity.Appointment
import com.example.bt_canhan_tuan_7.data.util.LocalDateTimeConverter

@Database(entities = [Appointment::class], version = 1, exportSchema = false)
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppointmentDatabase : RoomDatabase() {
    abstract fun appointmentDao(): AppointmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppointmentDatabase? = null

        fun getDatabase(context: Context): AppointmentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppointmentDatabase::class.java,
                    "appointment_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
