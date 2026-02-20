package com.example.calculator.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.calculator.dao.EmployeeDao
import com.example.calculator.dao.ShiftDao
import com.example.calculator.entity.EmployeeEntity
import com.example.calculator.entity.ShiftEntity

@Database(entities = [EmployeeEntity::class, ShiftEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun employeeDao(): EmployeeDao
    abstract fun shiftDao(): ShiftDao
}