package com.example.calculator.nav

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.calculator.dataClass.Employee
import com.example.calculator.dataClass.EmployeeSchedule
import com.example.calculator.view.AddEmployeeView
import com.example.calculator.view.CreateEmployeeScheduleScreen
import com.example.calculator.view.Report

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    employees: List<Employee>,
    onDeleteEmployee: (Employee) -> Unit,
    schedules: List<EmployeeSchedule>,
    onUpdateEmployees: (List<Employee>) -> Unit,
    onUpdateSchedules: (List<EmployeeSchedule>) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.AddEmployee.route,
        modifier = modifier
    ) {
        composable(Screen.AddEmployee.route) {
            AddEmployeeView(
                employees = employees,
                onDeleteEmployee = { emp ->
                    onUpdateEmployees(
                        employees.filter { it.id != emp.id }
                    )

                    onUpdateSchedules(
                        schedules.filter { it.employee.id != emp.id }
                    )
                }
            )
        }
        composable(Screen.CreateEmployeeSchedule.route) {
            CreateEmployeeScheduleScreen(
                schedules = schedules,
                onDeleteSchedule = { scheduleToDelete ->
                    onUpdateSchedules(
                        schedules.filter { it != scheduleToDelete }
                    )
                }
            )
        }
        composable(Screen.Report.route) {
            Report()
        }
    }
}