package com.example.controlfinance.ui

sealed class AppScreen(val route: String, val label: String) {
    data object Dashboard : AppScreen("dashboard", "Dashboard")
    data object Register : AppScreen("register", "Registrar")
    data object History : AppScreen("history", "Historial")
    data object Reports : AppScreen("reports", "Reportes")
}
