package com.example.calculator.dataClass

import com.example.calculator.components.ShiftModel

data class EmployeeReport(
    val employee: Employee,
    val shifts: List<ShiftModel> = emptyList()
)