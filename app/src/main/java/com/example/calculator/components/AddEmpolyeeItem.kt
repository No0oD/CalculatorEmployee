package com.example.calculator.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.entity.EmployeeEntity
import com.example.calculator.ui.theme.Active
import com.example.calculator.ui.theme.MyBlack
import com.example.calculator.ui.theme.MyRed
import com.example.calculator.ui.theme.NonActive

@Composable
fun AddEmployeeItem(onEmployeeAdded: (String) -> Unit) {
    var value by remember { mutableStateOf("") }
    val isTextNotEmpty = value.isNotEmpty()


    val animateIconButton by animateColorAsState(
        targetValue = if (isTextNotEmpty) Active else NonActive,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "buttonColorAnimation"
    )


    val contentAlpha by animateFloatAsState(
        targetValue = if (isTextNotEmpty) 1f else 0.5f,
        animationSpec = tween(500),
        label = "contentAlpha"
    )

    Card(Modifier.fillMaxWidth().padding(horizontal =  8.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                label = { Text("ПІБ") },
                textStyle = TextStyle(fontSize = 24.sp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            IconButton(
                onClick = {
                    if (isTextNotEmpty) {
                        onEmployeeAdded(value)
                        value = ""
                    }
                },

                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MyBlack.copy(alpha = contentAlpha),
                    containerColor = animateIconButton
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Додати",
                        tint = MyBlack.copy(alpha = contentAlpha)
                    )
                    Text("Додати працівника")
                }
            }
        }
    }
}


@Composable
fun EmployeeItem(employee: EmployeeEntity, onDeleteClick: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(employee.fullName, fontSize = 24.sp)
            Spacer(Modifier.height(12.dp))
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = IconButtonDefaults.iconButtonColors(containerColor = MyRed)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Видалити")
                    Text("Видалити")
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Видалити працівника?") },
            text = { Text("Дійсно хочете видалити ${employee.fullName}?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClick()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MyRed)
                ) { Text("Видалити") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Скасувати")
                }
            }
        )
    }
}
