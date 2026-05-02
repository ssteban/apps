package com.example.controlfinance.data.model

data class Transaction(
    val id: Long = 0L,
    val amount: Double,
    val type: TransactionType,
    val description: String? = null,
    val categoryId: Long,
    val transactionDate: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
