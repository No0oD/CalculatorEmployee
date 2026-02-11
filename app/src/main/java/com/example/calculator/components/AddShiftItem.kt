package com.example.calculator.components

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.example.calculator.ui.theme.MyRed
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

data class ShiftModel(
    val id: Long = System.currentTimeMillis(),
    val startDate: LocalDate,
    val endDate: LocalDate,
    val startTime: String = "00",
    val endTime: String = "00"
)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShiftSchedule() {
    var shiftList by remember { mutableStateOf(listOf<ShiftModel>()) }
    var firstSelectedDate by remember { mutableStateOf<LocalDate?>(null) }

    // --- ЛОГІКА ПІДРАХУНКУ ГОДИН ---
    // Перераховується автоматично при зміні shiftList
    val totalHours = shiftList.sumOf { shift ->
        val start = shift.startTime.toIntOrNull() ?: 0
        val end = shift.endTime.toIntOrNull() ?: 0

        // Логіка: якщо кінець більший за початок (08 -> 20) = 12
        // Якщо перехід через ніч (22 -> 06) = 8
        if (end >= start) end - start else (24 - start) + end
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
        ExposedDropdownMenuExample()

        Spacer(modifier = Modifier.height(16.dp))

        CustomShiftCalendar(
            selectedDate = firstSelectedDate,
            existingShifts = shiftList,
            onDateSelected = { date ->
                if (firstSelectedDate == null) {
                    firstSelectedDate = date
                } else {
                    val first = firstSelectedDate!!

                    // Спрощена вставка для прикладу (ваша повна логіка тут)
                    val (start, end) = if (first.isBefore(date)) first to date else date to first
                    val alreadyExists = shiftList.any { it.startDate == start && it.endDate == end }

                    if (!alreadyExists && (first.plusDays(1) == date || first.minusDays(1) == date || first == date)) {

                        shiftList = shiftList + ShiftModel(startDate = start, endDate = end)
                        firstSelectedDate = null
                    } else if (!alreadyExists && (date.dayOfMonth == 1 || date.dayOfMonth == date.lengthOfMonth())) {
                        shiftList = shiftList + ShiftModel(startDate = date, endDate = date)
                        firstSelectedDate = null
                    } else {
                        firstSelectedDate = date
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- ЗАГОЛОВОК З ПІДРАХУНКОМ ---
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
                    onTimeChange = { newStart, newEnd ->
                        shiftList = shiftList.map {
                            if (it.id == shift.id) it.copy(startTime = newStart, endTime = newEnd) else it
                        }
                    }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { shiftList = emptyList(); firstSelectedDate = null },
                modifier = Modifier.weight(1f)
            ) {
                Text("Очистити все")
            }

            Button(
                onClick = { /* Save logic */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Зберегти")
            }
        }
    }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomShiftCalendar(
    selectedDate: LocalDate?,
    existingShifts: List<ShiftModel>, // Список збережених змін
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
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Prev")
                }

                Text(
                    text = currentMonth.format(monthFormatter).replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next")
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

                    // Логіка станів
                    val isSelectedActive = selectedDate == date // Обрано зараз (1 клік)

                    // (1) Перевірка чи дата вже є у збережених змінах
                    val isSaved = existingShifts.any { shift ->
                        date == shift.startDate || date == shift.endDate
                    }

                    val isBoundary = day == 1 || day == daysInMonth

                    // Підсвітка можливих ходів
                    val isPossibleSelection = if (selectedDate != null) {
                        val diff = ChronoUnit.DAYS.between(selectedDate, date)
                        kotlin.math.abs(diff) == 1L || (isBoundary && diff == 0L)
                    } else {
                        true
                    }

                    // Вибір кольору
                    val bgColor = when {
                        isSelectedActive -> MaterialTheme.colorScheme.primary // Поточний вибір (Яскравий)
                        isSaved -> MaterialTheme.colorScheme.secondary // Збережені (Інший колір, напр. бірюзовий)
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
    onTimeChange: (String, String) -> Unit // Callback для оновлення часу
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
                // Початок зміни
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Початок зміни", fontSize = 14.sp, color = Color.Gray)
                    TimeInputFields(
                        hours = shift.startTime,
                        minutes = "00",
                        onTimeChange = { h, m -> onTimeChange(h, shift.endTime) }
                    )
                }

                // Кінець зміни
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Кінець зміни", fontSize = 14.sp, color = Color.Gray)
                    TimeInputFields(
                        hours = shift.endTime,
                        minutes = "00",
                        onTimeChange = { h, m -> onTimeChange(shift.startTime, h) }
                    )
                }
            }
        }
    }
}

@Composable
fun TimeInputFields(
    hours: String,
    minutes: String,
    onTimeChange: (String, String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(top = 4.dp)
    ) {
        TimeField(
            value = hours,
            onValueChange = { newValue ->
                if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                    onTimeChange(newValue, minutes)
                }
            },
            onFocusLost = {
                onTimeChange(formatTimeValue(hours), minutes)
            }
        )
        Text(":", style = MaterialTheme.typography.bodyLarge)

        // Поле хвилин (візуальне, якщо поки не впливає на розрахунок)
        TimeField(
            value = minutes,
            onValueChange = { /* Логіка хвилин */ },
            onFocusLost = { /* Логіка хвилин */ }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExposedDropdownMenuExample() {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("ПІБ") }

    val options = listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {

            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = MaterialTheme.colorScheme.onSurface) },
                        onClick = {
                            selectedOption = option
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}


@Composable
fun TimeField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusLost: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .width(55.dp)
            .onFocusChanged { focusState ->
                if (!focusState.isFocused) {
                    onFocusLost()
                }
            },
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true
    )
}

@Composable
fun EmployeeCardSchedule() {

    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        ProvideTextStyle(
            value = TextStyle(
                fontSize = 20.sp,
                color = Color.Black
            )
        ) {

            Column(Modifier.padding(16.dp).fillMaxWidth(),

            ) {
                Text("ПІБ")
                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Статус:") // активний \ завершено
                    Text("активний")
                }
                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Кількість годин :")
                    Text("156 годин")
                }
                Spacer(Modifier.height(10.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Кількість змін :")
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("20 змін")
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))

                IconButton(
                    onClick = { showDeleteDialog = true }, // Спочатку показуємо діалог
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MyRed
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Видалити")
                        Text("Видалити")

                    }
                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = { Text("Видалити працівника?") },
                            text = { Text("Дійсно хочете видалити ?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        // onDeleteClick()
                                        showDeleteDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MyRed
                                    )
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
            }
        }
    }
}

fun formatTimeValue(value: String): String {
    return when {
        value.isEmpty() -> "00"
        value.length == 1 -> "0$value"
        else -> value
    }
}

@Preview
@Composable
fun ShowEmployeeCardSchedule() {
    EmployeeCardSchedule()
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun ShowShiftSchedule() {
    ShiftSchedule()
}




