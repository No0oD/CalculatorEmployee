package com.example.calculator.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.calculator.App
import com.example.calculator.components.ShiftModel
import com.example.calculator.entity.EmployeeEntity
import com.example.calculator.entity.ShiftEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlinx.coroutines.flow.first


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val employeeRepository = (application as App).employeeRepository
    private val shiftRepository = (application as App).shiftRepository

    val employees: StateFlow<List<EmployeeEntity>> = employeeRepository.allEmployees
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allShifts: StateFlow<List<ShiftEntity>> = shiftRepository.getAllShifts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addEmployee(fullName: String) {
        viewModelScope.launch {
            employeeRepository.insert(EmployeeEntity(fullName = fullName))
        }
    }

    fun deleteEmployee(employee: EmployeeEntity) {
        viewModelScope.launch {
            employeeRepository.delete(employee)
            shiftRepository.deleteAllByEmployee(employee.id) // видаляємо і зміни
        }
    }

    fun addShifts(employeeId: Int, shifts: List<ShiftModel>) {
        viewModelScope.launch {
            shifts.forEach { shift ->
                shiftRepository.insert(
                    ShiftEntity(
                        employeeId = employeeId,
                        startDate = shift.startDate.toString(),
                        endDate = shift.endDate.toString(),
                        startHour = shift.startHour,
                        startMinute = shift.startMinute,
                        endHour = shift.endHour,
                        endMinute = shift.endMinute
                    )
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteShiftsByEmployeeAndMonth(employeeId: Int, monthKey: String) {
        viewModelScope.launch {
            val shifts = shiftRepository.getShiftsByEmployee(employeeId).first()
            shifts.filter { shift ->
                val date = LocalDate.parse(shift.startDate)
                "${date.year}-${date.monthValue}" == monthKey
            }.forEach { shift ->
                shiftRepository.delete(shift)
            }
        }
    }

    fun getShiftsForEmployee(employeeId: Int): Flow<List<ShiftEntity>> {
        return shiftRepository.getShiftsByEmployee(employeeId)
    }
}
class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}