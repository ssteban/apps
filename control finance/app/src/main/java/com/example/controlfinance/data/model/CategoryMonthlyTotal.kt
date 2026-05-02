package com.example.controlfinance.data.model

data class CategoryMonthlyTotal(
    val categoryId: Long,
    val categoryName: String,
    val type: TransactionType,
    val totalAmount: Double
)
