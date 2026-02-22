package com.example.calculator.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.example.calculator.entity.EmployeeEntity
import com.example.calculator.dataClass.EmployeeSchedule
import com.example.calculator.ui.theme.MyRed
import com.example.calculator.utils.formatMonthKey
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.absoluteValue

@RequiresApi(Build.VERSION_CODES.O)
fun areAllShiftsInSameMonth(shifts: List<ShiftModel>): Boolean {
    if (shifts.isEmpty()) return true
    val grouped = shifts.groupBy {
        Pair(it.startDate.year, it.startDate.monthValue)
    }
    return grouped.size == 1
}

data class ShiftModel(
    val id: Long = System.currentTimeMillis(),
    val startDate: LocalDate,
    val endDate: LocalDate,
    val startHour: String = "00",
    val startMinute: String = "00",
    val endHour: String = "00",
    val endMinute: String = "00"

)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShiftSchedule(
    employeeEntities: List<EmployeeEntity>,
    onDismiss: () -> Unit,
    onSave: (EmployeeEntity, List<ShiftModel>) -> Unit
) {
    var shiftList by remember { mutableStateOf(listOf<ShiftModel>()) }
    var firstSelectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedEmployeeEntity by remember { mutableStateOf<EmployeeEntity?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }

    val totalHours = shiftList.sumOf {
        calculateHours(
            it.startHour, it.startMinute,
            it.endHour, it.endMinute
        )
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EmployeeSelector(
                employeeEntities = employeeEntities,
                selectedEmployeeEntity = selectedEmployeeEntity,
                onEmployeeSelected = { selectedEmployeeEntity = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomShiftCalendar(
                selectedDate = firstSelectedDate,
                existingShifts = shiftList,
                onDateSelected = { date ->

                    if (firstSelectedDate == null) {
                        firstSelectedDate = date
                        return@CustomShiftCalendar
                    }

                    val first = firstSelectedDate!!

                    val (start, end) =
                        if (first.isBefore(date)) first to date else date to first

                    val diff = ChronoUnit.DAYS.between(start, end).toInt()
                    val isSameDay = diff == 0
                    val isNeighbour = diff == 1

                    val isEdgeDay =
                        start.dayOfMonth == 1 ||
                                start.dayOfMonth == start.lengthOfMonth()

                    val alreadyExists = shiftList.any {
                        it.startDate == start && it.endDate == end
                    }

                    val isValid =
                        (isSameDay && isEdgeDay) || // 1->1 або останній->останній
                                isNeighbour                 // n->n+1

                    if (isValid && !alreadyExists) {
                        shiftList = shiftList + ShiftModel(
                            startDate = start,
                            endDate = end
                        )
                        firstSelectedDate = null
                    } else {
                        // 🔥 reset тільки якщо не створили зміну
                        firstSelectedDate = date
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Обрані зміни:",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Загалом годин: $totalHours",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(shiftList) { shift ->
                    ShiftItem(
                        shift = shift,
                        onDelete = { shiftList = shiftList - shift },
                        onTimeChange = { updatedShift ->
                            shiftList = shiftList.map {
                                if (it.id == updatedShift.id) updatedShift else it
                            }
                        }

                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Скасувати")
                }

                Button(
                    onClick = {
                        if (selectedEmployeeEntity != null && shiftList.isNotEmpty()) {
                            if (areAllShiftsInSameMonth(shiftList)) {
                                onSave(selectedEmployeeEntity!!, shiftList)
                            } else {
                                showErrorDialog = true  // ← Показуємо діалог
                            }
                        }
                    }
                )
                {
                    Text("Зберегти")
                }
            }
        }

        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Помилка") },
                text = { Text("Зміни повинні бути в одному місяці!") },
                confirmButton = {
                    Button(onClick = { showErrorDialog = false }) {
                        Text("Зрозуміло")
                    }
                }
            )
        }

    }
}

// Допоміжна функція розрахунку годин
fun calculateHours(
    startHour: String,
    startMinute: String,
    endHour: String,
    endMinute: String
): Int {

    val startH = startHour.toIntOrNull() ?: 0
    val startM = startMinute.toIntOrNull() ?: 0
    val endH = endHour.toIntOrNull() ?: 0
    val endM = endMinute.toIntOrNull() ?: 0

    val startTotal = startH * 60 + startM
    val endTotal = endH * 60 + endM

    val diffMinutes =
        if (endTotal >= startTotal)
            endTotal - startTotal
        else
            (24 * 60 - startTotal) + endTotal

    return diffMinutes / 60
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomShiftCalendar(
    selectedDate: LocalDate?,
    existingShifts: List<ShiftModel>,
    showMonthNavigation: Boolean = true,
    onDateSelected: (LocalDate) -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val ukrainianLocale = Locale("uk", "UA")
    val monthFormatter = DateTimeFormatter.ofPattern("LLLL yyyy", ukrainianLocale)


    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)


    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Навігація
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showMonthNavigation) {
                    IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Prev")
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                Text(
                    text = currentMonth.format(monthFormatter).replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (showMonthNavigation) {
                    IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Нд").forEach { day ->
                    Text(text = day, fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val daysInMonth = currentMonth.lengthOfMonth()
            val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value
            val days = (1..daysInMonth).toList()

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(240.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(firstDayOfWeek - 1) { Box(modifier = Modifier.size(40.dp)) }

                items(days) { day ->
                    val date = currentMonth.atDay(day)

                    val isSelectedActive = selectedDate == date
                    val isSaved = existingShifts.any { shift ->
                        date == shift.startDate || date == shift.endDate
                    }

                    val isPossibleSelection = if (selectedDate != null) {
                        val diff = ChronoUnit.DAYS.between(selectedDate, date).toInt().absoluteValue
                        val isSameDay = diff == 0
                        val isNeighbour = diff == 1

                        val isEdgeDay =
                            selectedDate.dayOfMonth == 1 ||
                                    selectedDate.dayOfMonth == selectedDate.lengthOfMonth()

                        (isNeighbour) || (isSameDay && isEdgeDay)
                    } else {
                        true
                    }

                    val bgColor = when {
                        isSelectedActive -> MaterialTheme.colorScheme.primary
                        isSaved -> MaterialTheme.colorScheme.secondary
                        selectedDate != null && isPossibleSelection -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        else -> Color.Transparent
                    }

                    val contentColor = when {
                        isSelectedActive -> Color.White
                        isSaved -> MaterialTheme.colorScheme.onSecondary
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(bgColor)
                            .clickable { onDateSelected(date) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.toString(),
                            color = contentColor,
                            fontWeight = if (isSelectedActive || isSaved) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShiftItem(
    shift: ShiftModel,
    onDelete: () -> Unit,
    onTimeChange: (ShiftModel) -> Unit
) {
    val dayFormatter = DateTimeFormatter.ofPattern("dd")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Зміна з ${shift.startDate.format(dayFormatter)} на ${shift.endDate.format(dayFormatter)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Text("✕", color = Color.Gray)
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Початок зміни", fontSize = 14.sp, color = Color.Gray)

                    TimeInputFields(
                        hour = shift.startHour,
                        minute = shift.startMinute,
                        onTimeChange = { h, m ->
                            onTimeChange(
                                shift.copy(
                                    startHour = h,
                                    startMinute = m
                                )
                            )
                        }
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Кінець зміни", fontSize = 14.sp, color = Color.Gray)

                    TimeInputFields(
                        hour = shift.endHour,
                        minute = shift.endMinute,
                        onTimeChange = { h, m ->
                            onTimeChange(
                                shift.copy(
                                    endHour = h,
                                    endMinute = m
                                )
                            )
                        }
                    )
                }

            }
        }
    }
}

@Composable
fun TimeInputFields(
    hour: String,
    minute: String,
    onTimeChange: (String, String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TimeField(
            value = hour,
            max = 23,
            onValueChange = { newHour ->
                onTimeChange(newHour, minute)
            }
        )

        Text(":")

        TimeField(
            value = minute,
            max = 59,
            onValueChange = { newMinute ->
                onTimeChange(hour, newMinute)
            }
        )
    }
}


@Composable
fun TimeField(
    value: String,
    max: Int,
    onValueChange: (String) -> Unit
) {

    OutlinedTextField(
        value = value,
        onValueChange = { input ->

            if (input.length <= 2 && input.all { it.isDigit() }) {

                val number = input.toIntOrNull()

                if (number == null || number <= max) {
                    onValueChange(input)
                }
            }
        },
        modifier = Modifier.width(55.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeSelector(
    employeeEntities: List<EmployeeEntity>,
    selectedEmployeeEntity: EmployeeEntity?,
    onEmployeeSelected: (EmployeeEntity) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedEmployeeEntity?.fullName ?: "Оберіть працівника",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable) // Використовуємо коректний тип Anchor
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            employeeEntities.forEach { employee ->
                DropdownMenuItem(
                    text = { Text(employee.fullName) },
                    onClick = {
                        onEmployeeSelected(employee)
                        expanded = false
                    }
                )
            }
            if (employeeEntities.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Список працівників порожній") },
                    onClick = { expanded = false }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EmployeeCardSchedule(
    schedule: EmployeeSchedule,
    monthKey: String,
    onDeleteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f, label = "rotation"
    )

    val totalHours = schedule.shifts.sumOf {
        calculateHours(
            it.startHour, it.startMinute,
            it.endHour, it.endMinute
        )
    }
    val totalShifts = schedule.shifts.size

    val formattedMonth = formatMonthKey(monthKey)

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
                text = schedule.employeeEntity.fullName,
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            )
            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Місяць:", color = Color.Gray)
                Text(formattedMonth, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Кількість годин:", color = Color.Gray)
                Text("$totalHours годин", fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Кількість змін:")
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$totalShifts змін")
                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.rotate(rotationState)
                    ) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Розгорнути")
                    }
                }
            }

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

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MyRed)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Видалити")
                Spacer(Modifier.width(8.dp))
                Text("Видалити")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Видалити графік?") },
            text = {
                Text("Дійсно хочете видалити графік для ${schedule.employeeEntity.fullName} за $formattedMonth?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClick()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MyRed)
                ) {
                    Text("Видалити")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Скасувати")
                }
            }
        )
    }
}