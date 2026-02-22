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
import com.example.calculator.components.AddReportItem
import com.example.calculator.dataClass.EmployeeReport
import com.example.calculator.dataClass.EmployeeSchedule
import com.example.calculator.dataClass.ReportCard
import com.example.calculator.utils.groupShiftsByMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Report(
    reports: List<EmployeeReport>
) {
    val reportCards = buildList<ReportCard> {
        reports.forEach { report ->
            if (report.shifts.isEmpty()) {

                add(
                    ReportCard(
                        employeeEntity = report.employeeEntity,
                        monthKey = "",  // Порожній ключ = немає графіка
                        shifts = emptyList()
                    )
                )
            } else {
                val shiftsByMonth = groupShiftsByMonth(report.shifts)

                shiftsByMonth.forEach { (monthKey, shifts) ->
                    add(
                        ReportCard(
                            employeeEntity = report.employeeEntity,
                            monthKey = monthKey,
                            shifts = shifts
                        )
                    )
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
            items(reportCards) { card ->
                val schedule = EmployeeSchedule(
                    employeeEntity = card.employeeEntity,
                    shifts = card.shifts
                )

                AddReportItem(
                    schedule = schedule,
                    monthKey = card.monthKey
                )
            }
        }
    }
}
