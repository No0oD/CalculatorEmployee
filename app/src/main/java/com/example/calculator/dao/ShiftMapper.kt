package com.example.calculator.dao

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.calculator.components.ShiftModel
import com.example.calculator.entity.ShiftEntity
import java.time.LocalDate

object ShiftMapper {
    @RequiresApi(Build.VERSION_CODES.O)
    fun ShiftEntity.toShiftModel(): ShiftModel {
        return ShiftModel(
            id = this.id.toLong(),
            startDate = LocalDate.parse(this.startDate),
            endDate = LocalDate.parse(this.endDate),
            startHour = this.startHour,
            startMinute = this.startMinute,
            endHour = this.endHour,
            endMinute = this.endMinute
        )
    }
}