package com.example.controlfinance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.controlfinance.data.model.TransactionType
import com.example.controlfinance.viewmodel.ReportsUiState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ReportsScreen(uiState: ReportsUiState) {
    val currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-DO"))
    val incomeGroups = uiState.monthlyTotals.filter { it.type == TransactionType.INCOME }
    val expenseGroups = uiState.monthlyTotals.filter { it.type == TransactionType.EXPENSE }
    
    val totalIncome = incomeGroups.sumOf { it.totalAmount }
    val totalExpense = expenseGroups.sumOf { it.totalAmount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Reporte Mensual",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Resumen del mes", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    
                    ReportRow("Total Ingresos", currency.format(totalIncome), Color(0xFF4CAF50))
                    ReportRow("Total Gastos", currency.format(totalExpense), Color(0xFFF44336))
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    val balance = totalIncome - totalExpense
                    ReportRow(
                        "Balance Neto", 
                        currency.format(balance), 
                        if (balance >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                        isBold = true
                    )
                }
            }
        }

        if (incomeGroups.isNotEmpty()) {
            item {
                SectionHeader("Ingresos por Categoría")
            }
            items(incomeGroups) { group ->
                CategoryProgressRow(group.categoryName, group.totalAmount, totalIncome, Color(0xFF4CAF50), currency)
            }
        }

        if (expenseGroups.isNotEmpty()) {
            item {
                SectionHeader("Gastos por Categoría")
            }
            items(expenseGroups) { group ->
                CategoryProgressRow(group.categoryName, group.totalAmount, totalExpense, Color(0xFFF44336), currency)
            }
        }
        
        if (uiState.monthlyTotals.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text("No hay datos para mostrar", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun ReportRow(label: String, value: String, color: Color, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Text(
            value,
            color = color,
            style = if (isBold) MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun CategoryProgressRow(
    name: String, 
    amount: Double, 
    total: Double, 
    color: Color,
    currency: NumberFormat
) {
    val progress = if (total > 0) (amount / total).toFloat() else 0f
    
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, style = MaterialTheme.typography.bodyMedium)
            Text(currency.format(amount), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.1f)
        )
        Text(
            String.format("%.1f%% del total", progress * 100),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.End).padding(top = 2.dp)
        )
    }
}
