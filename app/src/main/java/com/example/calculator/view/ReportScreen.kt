package com.example.calculator.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.calculator.components.AddReportItem
import com.example.calculator.components.ShiftModel
import com.example.calculator.dao.ShiftMapper
import com.example.calculator.dataClass.EmployeeReport
import com.example.calculator.dataClass.EmployeeSchedule
import com.example.calculator.dataClass.ReportCard
import com.example.calculator.entity.EmployeeEntity
import com.example.calculator.utils.groupShiftsByMonth
import com.example.calculator.viewmodel.MainViewModel
import kotlin.collections.buildList

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Report(
    employees: List<EmployeeEntity>,
    viewModel: MainViewModel
) {
    val allShifts by viewModel.allShifts.collectAsState()

    val reportCards = remember(employees, allShifts) {
        buildList {
            employees.forEach { employee ->
                val employeeShifts = allShifts.filter { it.employeeId == employee.id }
                if (employeeShifts.isEmpty()) {
                    add(Triple(employee, "", emptyList<ShiftModel>()))
                } else {
                    val shiftModels = employeeShifts.map {
                        with(ShiftMapper) { it.toShiftModel() }
                    }
                    val shiftsByMonth = groupShiftsByMonth(shiftModels)
                    shiftsByMonth.forEach { (monthKey, shifts) ->
                        add(Triple(employee, monthKey, shifts))
                    }
                }
            }
        }
    }

    if (reportCards.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Працівників ще немає", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(reportCards) { (employee, monthKey, shifts) ->
                val schedule = EmployeeSchedule(employee = employee, shifts = shifts)
                AddReportItem(schedule = schedule, monthKey = monthKey)
            }
        }
    }
}