package com.example.controlfinance.data.service

import com.example.controlfinance.data.model.BalanceSummary
import com.example.controlfinance.data.model.Category
import com.example.controlfinance.data.model.CategoryMonthlyTotal
import com.example.controlfinance.data.model.Transaction
import com.example.controlfinance.data.model.TransactionType
import com.example.controlfinance.data.repository.CategoryRepository
import com.example.controlfinance.data.repository.TransactionRepository

data class FinanceSnapshot(
    val incomeCategories: List<Category>,
    val expenseCategories: List<Category>,
    val monthlyTransactions: List<Transaction>,
    val balanceSummary: BalanceSummary,
    val monthlyTotalsByCategory: List<CategoryMonthlyTotal>
)

class FinanceService(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) {

    fun ensureDefaultCategories() {
        val income = categoryRepository.listByType(TransactionType.INCOME)
        val expense = categoryRepository.listByType(TransactionType.EXPENSE)

        if (income.isEmpty()) {
            categoryRepository.create(Category(name = "Salario", type = TransactionType.INCOME))
            categoryRepository.create(Category(name = "Freelance", type = TransactionType.INCOME))
        }

        if (expense.isEmpty()) {
            categoryRepository.create(Category(name = "Alimentacion", type = TransactionType.EXPENSE))
            categoryRepository.create(Category(name = "Transporte", type = TransactionType.EXPENSE))
            categoryRepository.create(Category(name = "Servicios", type = TransactionType.EXPENSE))
        }
    }

    fun loadSnapshot(startMillis: Long, endMillis: Long): FinanceSnapshot {
        return FinanceSnapshot(
            incomeCategories = categoryRepository.listByType(TransactionType.INCOME),
            expenseCategories = categoryRepository.listByType(TransactionType.EXPENSE),
            monthlyTransactions = transactionRepository.listByDateRange(startMillis, endMillis),
            balanceSummary = transactionRepository.getBalanceSummary(),
            monthlyTotalsByCategory = transactionRepository.getMonthlyTotalsByCategory(startMillis, endMillis)
        )
    }

    fun addCategory(name: String, type: TransactionType): Long {
        return categoryRepository.create(Category(name = name.trim(), type = type))
    }

    fun addTransaction(amount: Double, type: TransactionType, description: String, categoryId: Long) {
        transactionRepository.create(
            Transaction(
                amount = amount,
                type = type,
                description = description.ifBlank { null },
                categoryId = categoryId,
                transactionDate = System.currentTimeMillis(),
                createdAt = System.currentTimeMillis()
            )
        )
    }

    fun deleteTransaction(transactionId: Long) {
        transactionRepository.delete(transactionId)
    }
}
