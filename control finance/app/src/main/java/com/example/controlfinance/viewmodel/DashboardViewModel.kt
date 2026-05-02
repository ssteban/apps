package com.example.controlfinance.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.controlfinance.data.local.FinanceDatabaseHelper
import com.example.controlfinance.data.model.BalanceSummary
import com.example.controlfinance.data.model.CategoryMonthlyTotal
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

data class DashboardUiState(
    val balanceSummary: BalanceSummary = BalanceSummary(0.0, 0.0),
    val topCategories: List<CategoryMonthlyTotal> = emptyList(),
    val referenceDate: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val dbHelper = FinanceDatabaseHelper(application.applicationContext)
    private val service = FinanceService(CategoryRepository(dbHelper), TransactionRepository(dbHelper))

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun changeMonth(offset: Int) {
        val calendar = java.util.Calendar.getInstance().apply { 
            timeInMillis = _uiState.value.referenceDate 
        }
        calendar.add(java.util.Calendar.MONTH, offset)
        _uiState.value = _uiState.value.copy(referenceDate = calendar.timeInMillis)
        refresh()
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)
            runCatching {
                service.ensureDefaultCategories()
                val (start, end) = DateUtils.monthRangeMillis(_uiState.value.referenceDate)
                val snapshot = service.loadSnapshot(start, end)
                _uiState.value = _uiState.value.copy(
                    balanceSummary = snapshot.balanceSummary,
                    topCategories = snapshot.monthlyTotalsByCategory,
                    isLoading = false
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }
}
