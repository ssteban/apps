package com.example.controlfinance.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlfinance.data.local.FinanceDatabaseHelper
import com.example.controlfinance.data.model.CategoryMonthlyTotal
import com.example.controlfinance.data.repository.CategoryRepository
import com.example.controlfinance.data.repository.TransactionRepository
import com.example.controlfinance.data.service.FinanceService
import com.example.controlfinance.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReportsUiState(
    val monthlyTotals: List<CategoryMonthlyTotal> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ReportsViewModel(application: Application) : AndroidViewModel(application) {
    private val dbHelper = FinanceDatabaseHelper(application.applicationContext)
    private val service = FinanceService(CategoryRepository(dbHelper), TransactionRepository(dbHelper))

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)
            runCatching {
                val (start, end) = DateUtils.monthRangeMillis()
                val snapshot = service.loadSnapshot(start, end)
                _uiState.value = ReportsUiState(
                    monthlyTotals = snapshot.monthlyTotalsByCategory,
                    isLoading = false
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }
}
