package com.example.controlfinance.data.model

enum class TransactionType(val value: String) {
    INCOME("income"),
    EXPENSE("expense");

    companion object {
        fun fromValue(value: String): TransactionType = when (value) {
            INCOME.value -> INCOME
            EXPENSE.value -> EXPENSE
            else -> throw IllegalArgumentException("Invalid transaction type: $value")
        }
    }
}
