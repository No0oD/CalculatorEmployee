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
import com.example.calculator.dataClass.EmployeeSchedule
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateEmployeeScheduleScreen(
    schedules: List<EmployeeSchedule>,
    onDeleteSchedule: (EmployeeSchedule) -> Unit
) {
    if (schedules.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Графіки ще не створені", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(schedules) { schedule ->
                EmployeeCardSchedule(
                    schedule = schedule,
                    onDeleteClick = { onDeleteSchedule(schedule) }
                )
            }
        }
    }
}