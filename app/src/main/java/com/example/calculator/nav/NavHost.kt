package com.example.calculator.nav

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.calculator.dataClass.Employee
import com.example.calculator.view.AddEmployeeView
import com.example.calculator.view.CreateEmployeeScheduleScreen
import com.example.calculator.view.Report

@RequiresApi(Build.VERSION_CODES.O)
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
            CreateEmployeeScheduleScreen(
                employees = employees,
                onDeleteEmployee = onDeleteEmployee
            )
        }
        composable(Screen.Report.route){
            Report()
        }
    }
}