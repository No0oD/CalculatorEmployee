package com.example.calculator.repository

import com.example.calculator.dao.ShiftDao
import com.example.calculator.entity.ShiftEntity
import kotlinx.coroutines.flow.Flow

class ShiftRepository(private val shiftDao: ShiftDao) {

    fun getShiftsByEmployee(employeeId: Int): Flow<List<ShiftEntity>> {
        return shiftDao.getByEmployeeId(employeeId)
    }

    suspend fun insert(shift: ShiftEntity) {
        shiftDao.insert(shift)
    }

    suspend fun delete(shift: ShiftEntity) {
        shiftDao.delete(shift)
    }
}