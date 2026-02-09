package com.example.calculator.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calculator.components.EmployeeItem
import com.example.calculator.dataClass.Employee
import com.example.calculator.ui.theme.NonActive


@Composable
fun AddEmployeeView(
    employees: List<Employee>,
    onDeleteEmployee: (Employee) -> Unit
) {
    if(employees.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Працівників ще немає", color = NonActive)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(employees) {employee ->
                EmployeeItem(
                    employee = employee,
                    onDeleteClick = {onDeleteEmployee(employee)}
                )
            }
        }
    }
}