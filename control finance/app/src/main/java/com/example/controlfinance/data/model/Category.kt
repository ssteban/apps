package com.example.controlfinance.data.model

data class Category(
    val id: Long = 0L,
    val name: String,
    val type: TransactionType,
    val createdAt: Long = System.currentTimeMillis()
)
