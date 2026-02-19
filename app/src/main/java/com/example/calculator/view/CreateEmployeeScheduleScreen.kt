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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.calculator.components.EmployeeCardSchedule
import com.example.calculator.dataClass.EmployeeReport
import com.example.calculator.dataClass.EmployeeSchedule
import com.example.calculator.dataClass.MonthlyScheduleCard
import com.example.calculator.utils.groupShiftsByMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateEmployeeScheduleScreen(
    reports: List<EmployeeReport>,
    onDeleteSchedule: (EmployeeReport, String) -> Unit
) {


    val scheduleCards = buildList<MonthlyScheduleCard> {
        reports.forEach { report ->
            if (report.shifts.isNotEmpty()) {
                val shiftsByMonth = groupShiftsByMonth(report.shifts)

                shiftsByMonth.forEach { (monthKey, shifts) ->
                    add(
                        MonthlyScheduleCard(
                            employee = report.employee,
                            monthKey = monthKey,
                            shifts = shifts,
                            originalReport = report
                        )
                    )
                }
            }
        }
    }

    if (scheduleCards.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Графіки ще не створені", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(scheduleCards) { card ->
                val schedule = EmployeeSchedule(
                    employee = card.employee,
                    shifts = card.shifts
                )

                EmployeeCardSchedule(
                    schedule = schedule,
                    monthKey = card.monthKey,
                    onDeleteClick = {
                        onDeleteSchedule(card.originalReport, card.monthKey)
                    }
                )
            }
        }
    }
}
