package com.example.calculator.nav

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.calculator.dataClass.EmployeeReport
import com.example.calculator.utils.groupShiftsByMonth
import com.example.calculator.view.AddEmployeeView
import com.example.calculator.view.CreateEmployeeScheduleScreen
import com.example.calculator.view.Report
import kotlin.collections.map

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    reports: List<EmployeeReport>,
    onUpdateReports: (List<EmployeeReport>) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.AddEmployee.route,
        modifier = modifier
    ) {

        composable(Screen.AddEmployee.route) {
            AddEmployeeView(
                employees = reports.map { it.employee },
                onDeleteEmployee = { employeeToDelete ->
                    onUpdateReports(
                        reports.filter { it.employee.id != employeeToDelete.id }
                    )
                }
            )
        }


        composable(Screen.CreateEmployeeSchedule.route) {
            CreateEmployeeScheduleScreen(
                reports = reports,
                onDeleteSchedule = { reportToDelete, monthKeyToDelete ->

                    onUpdateReports(
                        reports.map { report ->
                            if (report.employee.id == reportToDelete.employee.id) {

                                val shiftsByMonth = groupShiftsByMonth(report.shifts)

                                val remainingShifts = shiftsByMonth
                                    .filterKeys { it != monthKeyToDelete }
                                    .values
                                    .flatten()

                                report.copy(shifts = remainingShifts)
                            } else {
                                report
                            }
                        }
                    )
                }
            )
        }

        composable(Screen.Report.route) {
            Report(
                reports = reports
            )
        }
    }
}