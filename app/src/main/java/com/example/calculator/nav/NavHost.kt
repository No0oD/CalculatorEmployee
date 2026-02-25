package com.example.calculator.nav

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.calculator.entity.EmployeeEntity
import com.example.calculator.view.AddEmployeeView
import com.example.calculator.view.CreateEmployeeScheduleScreen
import com.example.calculator.view.Report
import com.example.calculator.viewmodel.MainViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    employees: List<EmployeeEntity>,
    viewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.AddEmployee.route,
        modifier = modifier
    ) {
        composable(Screen.AddEmployee.route) {
            AddEmployeeView(
                employees = employees,
                onDeleteEmployee = { employee ->
                    viewModel.deleteEmployee(employee)
                }
            )
        }

        composable(Screen.CreateEmployeeSchedule.route) {
            CreateEmployeeScheduleScreen(
                employees = employees,
                viewModel = viewModel
            )
        }

        composable(Screen.Report.route) {
            Report(
                employees = employees,
                viewModel = viewModel
            )
        }
    }
}