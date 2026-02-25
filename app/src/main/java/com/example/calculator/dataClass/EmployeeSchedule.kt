package com.example.calculator.dataClass

import com.example.calculator.components.ShiftModel
import com.example.calculator.entity.EmployeeEntity

data class EmployeeSchedule(
    val employee: EmployeeEntity,
    val shifts: List<ShiftModel>
)