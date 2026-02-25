package com.example.calculator.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.calculator.entity.ShiftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShiftDao {
    @Insert
    suspend fun insert(shift: ShiftEntity)

    @Delete
    suspend fun delete(shift: ShiftEntity)

    @Query("SELECT * FROM shifts")
    fun getAll(): Flow<List<ShiftEntity>>

    @Query("DELETE FROM shifts WHERE employeeId = :employeeId")
    suspend fun deleteAllByEmployee(employeeId: Int)

    @Query("SELECT * FROM shifts WHERE employeeId = :employeeId")
    fun getByEmployeeId(employeeId: Int): Flow<List<ShiftEntity>>
}