package com.example.calculator.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shifts")
data class ShiftEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val employeeId: Int,
    val startDate: String,
    val endDate: String,
    val startHour: String = "00",
    val startMinute: String = "00",
    val endHour: String = "00",
    val endMinute: String = "00"
)