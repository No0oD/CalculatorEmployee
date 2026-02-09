package com.example.calculator.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {

    object AddEmployee: Screen("add_employee","Працівники", Icons.Filled.Person)
    object CreateEmployeeSchedule: Screen("create_employee_schedule","Створити графік", Icons.Filled.DateRange)
    object Report: Screen("report", "Звіт", Icons.Filled.List)
}