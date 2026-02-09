package com.example.calculator.dataClass

import java.util.UUID

data class Employee(
    val id: String = UUID.randomUUID().toString(),
    val fullName: String
)
