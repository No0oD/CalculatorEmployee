package com.example.calculator.dataClass

import com.example.calculator.components.ShiftModel

data class EmployeeSchedule(
    val employee: Employee,
    val shifts: List<ShiftModel>
)