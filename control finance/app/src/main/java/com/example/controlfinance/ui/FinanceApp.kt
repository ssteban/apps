package com.example.controlfinance.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.controlfinance.ui.screens.DashboardScreen
import com.example.controlfinance.ui.screens.HistoryScreen
import com.example.controlfinance.ui.screens.RegisterTransactionScreen
import com.example.controlfinance.ui.screens.ReportsScreen
import com.example.controlfinance.viewmodel.DashboardViewModel
import com.example.controlfinance.viewmodel.ReportsViewModel
import com.example.controlfinance.viewmodel.TransactionsViewModel

@Composable
fun FinanceApp() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // ViewModels
    val dashboardViewModel: DashboardViewModel = viewModel()
    val transactionsViewModel: TransactionsViewModel = viewModel()
    val reportsViewModel: ReportsViewModel = viewModel()

    val dashboardUiState by dashboardViewModel.uiState.collectAsState()
    val transactionsUiState by transactionsViewModel.uiState.collectAsState()
    val reportsUiState by reportsViewModel.uiState.collectAsState()

    // Manejo de mensajes de sistema (Snackbars)
    LaunchedEffect(transactionsUiState.errorMessage, transactionsUiState.systemMessage) {
        val message = transactionsUiState.errorMessage ?: transactionsUiState.systemMessage
        if (!message.isNullOrBlank()) {
            snackbarHostState.showSnackbar(message)
            transactionsViewModel.clearMessages()
            // Refrescar otros viewmodels si hubo cambios en transacciones
            dashboardViewModel.refresh()
            reportsViewModel.refresh()
        }
    }

    val navItems = listOf(
        NavItem(AppScreen.Dashboard, Icons.Default.Home),
        NavItem(AppScreen.Register, Icons.Default.Menu),
        NavItem(AppScreen.History, Icons.AutoMirrored.Filled.List),
        NavItem(AppScreen.Reports, Icons.Default.DateRange)
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar(
                tonalElevation = 8.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navItems.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            runCatching {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }.onFailure {
                                scope.launch {
                                    snackbarHostState.showSnackbar("No se pudo abrir ${item.screen.label}")
                                }
                            }
                        },
                        icon = { 
                            Icon(
                                item.icon, 
                                contentDescription = item.screen.label,
                                modifier = Modifier.size(24.dp)
                            ) 
                        },
                        label = { Text(item.screen.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppScreen.Dashboard.route) {
                DashboardScreen(
                    uiState = dashboardUiState,
                    onMonthChange = { offset -> dashboardViewModel.changeMonth(offset) }
                )
            }
            composable(AppScreen.Register.route) {
                RegisterTransactionScreen(
                    uiState = transactionsUiState,
                    onAddTransaction = transactionsViewModel::addTransaction,
                    onAddCategory = { name, type, onSuccess -> 
                        transactionsViewModel.addCategory(name, type, onSuccess) 
                    }
                )
            }
            composable(AppScreen.History.route) {
                HistoryScreen(
                    uiState = transactionsUiState,
                    onDeleteTransaction = { id ->
                        transactionsViewModel.deleteTransaction(id)
                    }
                )
            }
            composable(AppScreen.Reports.route) {
                ReportsScreen(uiState = reportsUiState)
            }
        }
    }
}

data class NavItem(val screen: AppScreen, val icon: ImageVector)
