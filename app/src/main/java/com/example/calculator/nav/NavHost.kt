package com.example.calculator.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.calculator.dataClass.Employee
import com.example.calculator.view.AddEmployeeView
import com.example.calculator.view.CreateEmployeeScheduleScreen
import com.example.calculator.view.Report

@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    employees: List<Employee>,
    onDeleteEmployee: (Employee) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.AddEmployee.route,
        modifier = modifier
    ) {
        composable(Screen.AddEmployee.route) {
            AddEmployeeView(employees = employees, onDeleteEmployee = onDeleteEmployee)
        }
        composable(Screen.CreateEmployeeSchedule.route) {
            CreateEmployeeScheduleScreen()
        }
        composable(Screen.Report.route){
            Report()
        }
    }
}