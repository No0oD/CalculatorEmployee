package com.example.calculator.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.calculator.components.ShiftModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun groupShiftsByMonth(shifts: List<ShiftModel>): Map<String, List<ShiftModel>> {
    return shifts.groupBy { shift ->
        "${shift.startDate.year}-${shift.startDate.monthValue}"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatMonthKey(monthKey: String): String {
    val parts = monthKey.split("-")
    val year = parts[0].toInt()
    val month = parts[1].toInt()

    val ukrainianMonths = listOf(
        "Січень", "Лютий", "Березень", "Квітень",
        "Травень", "Червень", "Липень", "Серпень",
        "Вересень", "Жовтень", "Листопад", "Грудень"
    )

    return "${ukrainianMonths[month - 1]} $year"
}

@RequiresApi(Build.VERSION_CODES.O)
fun getMonthKeyFromShifts(shifts: List<ShiftModel>): String {
    if (shifts.isEmpty()) return ""
    val firstShift = shifts.first()
    return "${firstShift.startDate.year}-${firstShift.startDate.monthValue}"
}

@RequiresApi(Build.VERSION_CODES.O)
fun areAllShiftsInSameMonth(shifts: List<ShiftModel>): Boolean {
    if (shifts.isEmpty()) return true

    val grouped = shifts.groupBy {
        Pair(it.startDate.year, it.startDate.monthValue)
    }

    return grouped.size == 1
}