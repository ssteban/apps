package com.example.controlfinance.data.model

data class BalanceSummary(
    val totalIncome: Double,
    val totalExpense: Double
) {
    val balance: Double get() = totalIncome - totalExpense
}
