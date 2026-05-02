package com.example.controlfinance.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlfinance.data.local.FinanceDatabaseHelper
import com.example.controlfinance.data.model.Category
import com.example.controlfinance.data.model.Transaction
import com.example.controlfinance.data.model.TransactionType
import com.example.controlfinance.data.repository.CategoryRepository
import com.example.controlfinance.data.repository.TransactionRepository
import com.example.controlfinance.data.service.FinanceService
import com.example.controlfinance.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TransactionsUiState(
    val incomeCategories: List<Category> = emptyList(),
    val expenseCategories: List<Category> = emptyList(),
    val monthlyTransactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val systemMessage: String? = null
)

class TransactionsViewModel(application: Application) : AndroidViewModel(application) {
    private val dbHelper = FinanceDatabaseHelper(application.applicationContext)
    private val service = FinanceService(CategoryRepository(dbHelper), TransactionRepository(dbHelper))

    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { service.ensureDefaultCategories() }
            refresh()
        }
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)
            runCatching {
                service.ensureDefaultCategories()
                val (start, end) = DateUtils.monthRangeMillis()
                val snapshot = service.loadSnapshot(start, end)
                _uiState.value = _uiState.value.copy(
                    incomeCategories = snapshot.incomeCategories,
                    expenseCategories = snapshot.expenseCategories,
                    monthlyTransactions = snapshot.monthlyTransactions,
                    isLoading = false
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun addTransaction(amount: Double, type: TransactionType, description: String, categoryId: Long) {
        if (amount <= 0 || categoryId <= 0) {
            _uiState.value = _uiState.value.copy(errorMessage = "Datos invalidos")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                service.addTransaction(amount, type, description, categoryId)
                _uiState.value = _uiState.value.copy(systemMessage = "Movimiento guardado")
                refresh()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun addCategory(name: String, type: TransactionType, onSuccess: (Long) -> Unit) {
        if (name.trim().length < 3) {
            _uiState.value = _uiState.value.copy(errorMessage = "Nombre muy corto")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val newId = service.addCategory(name, type)
                _uiState.value = _uiState.value.copy(systemMessage = "Categoria creada")
                refresh()
                onSuccess(newId)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                service.deleteTransaction(id)
                _uiState.value = _uiState.value.copy(systemMessage = "Movimiento eliminado")
                refresh()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, systemMessage = null)
    }
}
