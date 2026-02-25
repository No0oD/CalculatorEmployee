package com.example.calculator.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.dataClass.EmployeeSchedule
import com.example.calculator.utils.formatMonthKey



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddReportItem(
    schedule: EmployeeSchedule,
    monthKey: String
) {
    var expanded by remember { mutableStateOf(false) }

    val totalHours = schedule.shifts.sumOf {
        calculateHours(
            it.startHour, it.startMinute,
            it.endHour, it.endMinute
        )
    }
    val totalShifts = schedule.shifts.size
    val hasSchedule = schedule.shifts.isNotEmpty()

    val formattedMonth = if (hasSchedule) formatMonthKey(monthKey) else "Графік не створено"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = schedule.employee.fullName,
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            )

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Місяць:", color = Color.Gray)
                Text(
                    text = formattedMonth,
                    fontWeight = FontWeight.Medium,
                    color = if (hasSchedule) Color.Black else Color.Gray
                )
            }
            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Загальна кількість годин:", color = Color.Gray)
                Text(
                    text = if (hasSchedule) "$totalHours годин" else "0 годин",
                    fontWeight = FontWeight.Medium,
                    color = if (hasSchedule) Color.Black else Color.Gray
                )
            }
            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Загальна кількість змін:")
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (hasSchedule) "$totalShifts змін" else "0 змін",
                        color = if (hasSchedule) Color.Black else Color.Gray
                    )

                    if (hasSchedule) {
                        IconButton(
                            onClick = { expanded = !expanded },
                        ) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Розгорнути")
                        }
                    }
                }
            }

            if (hasSchedule) {
                AnimatedVisibility(visible = expanded) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Графік змін:", style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.height(8.dp))

                        CustomShiftCalendar(
                            selectedDate = null,
                            existingShifts = schedule.shifts,
                            onDateSelected = { }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}