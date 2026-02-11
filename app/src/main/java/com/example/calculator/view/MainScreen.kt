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

    val currentScreen = screens.find { it.route == currentRoute }?: Screen.AddEmployee

    var showAddEmployee by remember { mutableStateOf(false) }
    var showCreateEmployee by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState (skipPartiallyExpanded = true)


    var employees by remember { mutableStateOf(listOf<Employee>()) }


    val showFab = currentRoute == Screen.AddEmployee.route || currentRoute == Screen.CreateEmployeeSchedule.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentScreen.title) }
            )
        },
        bottomBar = {
            BottomNavBar(
                screens = screens,
                currentRoute = currentRoute,
                onNavigate = {route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
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
                            Screen.AddEmployee.route -> {
                                // to do late
                                showAddEmployee = true
                            }
                            Screen.CreateEmployeeSchedule.route -> {
                                // to do late
                                showCreateEmployee = true
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = when (currentRoute) {
                            Screen.AddEmployee.route -> Icons.Filled.Add
                            Screen.CreateEmployeeSchedule.route -> Icons.Filled.DateRange
                            else -> Icons.Filled.Add
                        },
                        contentDescription = "FAB"
                    )
                }
            }
        }
    ) {paddingValues ->
        NavigationHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
            employees = employees,
            onDeleteEmployee = { employee ->
                employees = employees.filter { it.id != employee.id }
            }
        )
        if(showAddEmployee) {
            Dialog(
                onDismissRequest = { showAddEmployee = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                AddEmployeeItem(
                    onEmployeeAdded = { name ->
                        employees = employees + Employee(fullName = name)
                        showAddEmployee = false
                    }
                )
            }
        }

        Dialog(
            onDismissRequest = { showCreateEmployee = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                 dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
               ShiftSchedule()
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

