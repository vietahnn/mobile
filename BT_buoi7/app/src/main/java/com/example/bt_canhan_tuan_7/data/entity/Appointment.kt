package com.example.bt_canhan_tuan_7.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val location: String,
    val fromTime: LocalDateTime,
    val toTime: LocalDateTime,
    val personName: String,
    val personAvatarUrl: String,
    val notificationSent: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
