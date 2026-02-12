package com.example.calculator.view


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.calculator.components.AddEmployeeItem
import com.example.calculator.components.ShiftSchedule
import com.example.calculator.dataClass.Employee
import com.example.calculator.dataClass.EmployeeSchedule
import com.example.calculator.nav.Screen
import com.example.calculator.nav.NavigationHost


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val screens = listOf(Screen.AddEmployee, Screen.CreateEmployeeSchedule, Screen.Report)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreen = screens.find { it.route == currentRoute } ?: Screen.AddEmployee

    // Стан для відображення діалогів
    var showAddEmployeeDialog by remember { mutableStateOf(false) }
    var showCreateScheduleDialog by remember { mutableStateOf(false) }

    // ДАНІ
    var employees by remember { mutableStateOf(listOf<Employee>()) }
    var schedules by remember { mutableStateOf(listOf<EmployeeSchedule>()) }

    val showFab = currentRoute == Screen.AddEmployee.route || currentRoute == Screen.CreateEmployeeSchedule.route

    Scaffold(
        topBar = { TopAppBar(title = { Text(currentScreen.title) }) },
        bottomBar = {
            BottomNavBar(
                screens = screens,
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = {
                        when (currentRoute) {
                            Screen.AddEmployee.route -> showAddEmployeeDialog = true
                            Screen.CreateEmployeeSchedule.route -> showCreateScheduleDialog = true
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentRoute == Screen.CreateEmployeeSchedule.route) Icons.Filled.DateRange else Icons.Filled.Add,
                        contentDescription = "FAB"
                    )
                }
            }
        }
    ) { paddingValues ->
        NavigationHost(
            navController = navController,
            employees = employees,
            schedules = schedules,
            onUpdateEmployees = { employees = it },
            onUpdateSchedules = { schedules = it },
            modifier = Modifier.padding(paddingValues),
            onDeleteEmployee = { employee ->
                employees = employees.filter { it.id != employee.id }
            }
        )
        // ДІАЛОГ ДОДАВАННЯ ПРАЦІВНИКА
        if (showAddEmployeeDialog) {
            Dialog(
                onDismissRequest = { showAddEmployeeDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                AddEmployeeItem(
                    onEmployeeAdded = { name ->
                        employees = employees + Employee(fullName = name)
                        showAddEmployeeDialog = false
                    }
                )
            }
        }

        // ДІАЛОГ СТВОРЕННЯ ГРАФІКУ (ShiftSchedule)
        if (showCreateScheduleDialog) {
            Dialog(
                onDismissRequest = { showCreateScheduleDialog = false },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false
                )
            ) {
                ShiftSchedule(
                    employees = employees,
                    onDismiss = { showCreateScheduleDialog = false },
                    onSave = { employee, shifts ->
                        // Додаємо новий графік або оновлюємо існуючий для цього працівника
                        val newSchedule = EmployeeSchedule(employee, shifts)

                        // Логіка: видаляємо старий графік цього працівника, якщо він був, і додаємо новий
                        val otherSchedules = schedules.filter { it.employee.id != employee.id }
                        schedules = otherSchedules + newSchedule

                        showCreateScheduleDialog = false
                    }
                )
            }
        }
    }
}




@Composable
fun BottomNavBar(
    screens: List<Screen>,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar {
        screens.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title)},
                selected = currentRoute == screen.route,
                onClick = {onNavigate(screen.route)}
            )
        }
    }
}

