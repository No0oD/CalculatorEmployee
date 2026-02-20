package com.example.calculator.repository

import com.example.calculator.dao.EmployeeDao
import com.example.calculator.entity.EmployeeEntity
import kotlinx.coroutines.flow.Flow

class EmployeeRepository(private val employeeDao: EmployeeDao) {

    val allEmployees: Flow<List<EmployeeEntity>> = employeeDao.getAll()

    suspend fun insert(employee: EmployeeEntity) {
        employeeDao.insert(employee)
    }

    suspend fun delete(employee: EmployeeEntity) {
        employeeDao.delete(employee)
    }
}