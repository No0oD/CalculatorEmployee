package com.example.calculator.view


import android.app.Application
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.calculator.components.AddEmployeeItem
import com.example.calculator.components.ShiftSchedule
import com.example.calculator.nav.Screen
import com.example.calculator.nav.NavigationHost
import com.example.calculator.viewmodel.MainViewModel
import com.example.calculator.viewmodel.MainViewModelFactory


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(LocalContext.current.applicationContext as Application)
    )

    val employees by viewModel.employees.collectAsState()

    val navController = rememberNavController()
    val screens = listOf(Screen.AddEmployee, Screen.CreateEmployeeSchedule, Screen.Report)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreen = screens.find { it.route == currentRoute } ?: Screen.AddEmployee

    var showAddEmployeeDialog by remember { mutableStateOf(false) }
    var showCreateScheduleDialog by remember { mutableStateOf(false) }

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
            viewModel = viewModel,
            modifier = Modifier.padding(paddingValues)
        )

        if (showAddEmployeeDialog) {
            Dialog(
                onDismissRequest = { showAddEmployeeDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                AddEmployeeItem(
                    onEmployeeAdded = { name ->
                        viewModel.addEmployee(name)
                        showAddEmployeeDialog = false
                    }
                )
            }
        }

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
                    employeeEntities = employees,
                    onDismiss = { showCreateScheduleDialog = false },
                    onSave = { employee, shifts ->
                        viewModel.addShifts(employee.id, shifts)
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

