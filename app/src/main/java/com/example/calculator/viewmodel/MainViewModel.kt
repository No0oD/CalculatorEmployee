package com.example.calculator.viewmodel

import android.app.Application
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

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val employeeRepository = (application as App).employeeRepository
    private val shiftRepository = (application as App).shiftRepository

    val employees: StateFlow<List<EmployeeEntity>> = employeeRepository.allEmployees
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addEmployee(fullName: String) {
        viewModelScope.launch {
            employeeRepository.insert(EmployeeEntity(fullName = fullName))
        }
    }

    fun deleteEmployee(employee: EmployeeEntity) {
        viewModelScope.launch {
            employeeRepository.delete(employee)
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

    fun deleteShift(shift: ShiftEntity) {
        viewModelScope.launch {
            shiftRepository.delete(shift)
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