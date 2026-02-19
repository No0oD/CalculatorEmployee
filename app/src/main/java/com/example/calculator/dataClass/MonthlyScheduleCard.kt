package com.example.calculator.dataClass

data class MonthlyScheduleCard(
    val employee: com.example.calculator.dataClass.Employee,
    val monthKey: String,
    val shifts: List<com.example.calculator.components.ShiftModel>,
    val originalReport: EmployeeReport
)