package com.example.controlfinance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.controlfinance.data.model.Transaction
import com.example.controlfinance.data.model.TransactionType
import com.example.controlfinance.util.DateUtils
import com.example.controlfinance.viewmodel.TransactionsUiState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HistoryScreen(
    uiState: TransactionsUiState,
    onDeleteTransaction: (Long) -> Unit
) {
    val currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-DO"))
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            "Historial de Transacciones",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (uiState.monthlyTransactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aún no tienes movimientos este mes", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.monthlyTransactions, key = { it.id }) { tx ->
                    TransactionItem(
                        transaction = tx,
                        currency = currency,
                        onDelete = { transactionToDelete = tx }
                    )
                }
            }
        }

        if (transactionToDelete != null) {
            AlertDialog(
                onDismissRequest = { transactionToDelete = null },
                confirmButton = {
                    TextButton(onClick = {
                        transactionToDelete?.let { onDeleteTransaction(it.id) }
                        transactionToDelete = null
                    }) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { transactionToDelete = null }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("¿Eliminar transacción?") },
                text = { Text("Esta acción no se puede deshacer.") }
            )
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    currency: NumberFormat,
    onDelete: () -> Unit
) {
    val isIncome = transaction.type == TransactionType.INCOME
    val color = if (isIncome) Color(0xFF4CAF50) else Color(0xFFF44336)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono / Indicador
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (isIncome) "↑" else "↓",
                    color = color,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    transaction.description ?: "Sin descripción",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
                Text(
                    DateUtils.formatDate(transaction.transactionDate),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    (if (isIncome) "+" else "-") + currency.format(transaction.amount),
                    color = color,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Borrar",
                        tint = Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
