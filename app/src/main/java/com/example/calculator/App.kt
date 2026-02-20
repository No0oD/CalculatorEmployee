package com.example.calculator

import android.app.Application
import androidx.room.Room
import com.example.calculator.database.AppDatabase
import com.example.calculator.repository.EmployeeRepository
import com.example.calculator.repository.ShiftRepository

class App : Application() {
    val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    val employeeRepository by lazy {
        EmployeeRepository(database.employeeDao())
    }

    val shiftRepository by lazy {
        ShiftRepository(database.shiftDao())
    }
}