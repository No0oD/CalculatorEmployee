package com.example.calculator.dataClass

import com.example.calculator.entity.EmployeeEntity

data class ReportCard(
    val employeeEntity: EmployeeEntity,
    val monthKey: String,
    val shifts: List<com.example.calculator.components.ShiftModel>
)