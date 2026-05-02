package com.example.controlfinance.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.controlfinance.data.model.TransactionType
import com.example.controlfinance.viewmodel.TransactionsUiState

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RegisterTransactionScreen(
    uiState: TransactionsUiState,
    onAddTransaction: (Double, TransactionType, String, Long) -> Unit,
    onAddCategory: (String, TransactionType, (Long) -> Unit) -> Unit
) {
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var categoryName by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableLongStateOf(0L) }
    
    var showSaveConfirmation by remember { mutableStateOf(false) }
    var showCategoryConfirmation by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val categories = if (selectedType == TransactionType.INCOME) {
        uiState.incomeCategories
    } else {
        uiState.expenseCategories
    }

    LaunchedEffect(selectedType, categories) {
        if (categories.isNotEmpty() && categories.none { it.id == selectedCategoryId }) {
            selectedCategoryId = categories.first().id
        }
    }

    if (uiState.isLoading && categories.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "Registrar Movimiento",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )

        // Switch de Tipo
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                TypeButton(
                    label = "Gasto",
                    isSelected = selectedType == TransactionType.EXPENSE,
                    color = Color(0xFFF44336),
                    modifier = Modifier.weight(1f),
                    onClick = { 
                        selectedType = TransactionType.EXPENSE
                        selectedCategoryId = 0L
                    }
                )
                TypeButton(
                    label = "Ingreso",
                    isSelected = selectedType == TransactionType.INCOME,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f),
                    onClick = { 
                        selectedType = TransactionType.INCOME
                        selectedCategoryId = 0L
                    }
                )
            }
        }

        // Formulario
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    if (it.all { char -> char.isDigit() || char == '.' || char == ',' }) {
                        amountText = it
                    }
                },
                label = { Text("Monto") },
                placeholder = { Text("0.00") },
                prefix = { Text("$ ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                placeholder = { Text("Ej. Compra de supermercado") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 2
            )

            Text(
                "Seleccionar Categoría",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategoryId == category.id,
                        onClick = { selectedCategoryId = category.id },
                        label = { Text(category.name) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                
                // Botón para nueva categoría rápida
                InputChip(
                    selected = false,
                    onClick = { showCategoryConfirmation = true },
                    label = { Text("Nueva") },
                    leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            if (categories.isEmpty()) {
                AssistChip(
                    onClick = { showCategoryConfirmation = true },
                    label = { Text("No hay categorias, crea una") }
                )
            } else {
                Text(
                    text = "Categoria activa: ${categories.firstOrNull { it.id == selectedCategoryId }?.name ?: "Ninguna"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        val parsedAmount = amountText.replace(',', '.').toDoubleOrNull() ?: 0.0
        val canSave = parsedAmount > 0 && selectedCategoryId != 0L && !uiState.isLoading

        Button(
            onClick = { showSaveConfirmation = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = canSave
        ) {
            Text("Guardar Registro", style = MaterialTheme.typography.titleMedium)
        }

        if (!canSave) {
            Text(
                text = "Para guardar: ingresa monto y selecciona categoria",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider()

        // Categoría Rápida
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Nueva Categoría", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                IconButton(
                    onClick = { showCategoryConfirmation = true },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }
    }

    // Diálogos de confirmación
    if (showSaveConfirmation) {
        AlertDialog(
            onDismissRequest = { showSaveConfirmation = false },
            confirmButton = {
                TextButton(onClick = {
                    val amount = amountText.replace(',', '.').toDoubleOrNull() ?: 0.0
                    onAddTransaction(amount, selectedType, description, selectedCategoryId)
                    amountText = ""
                    description = ""
                    showSaveConfirmation = false
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showSaveConfirmation = false }) { Text("Cancelar") }
            },
            title = { Text("¿Guardar este movimiento?") },
            text = { Text("Se añadirá un ${if (selectedType == TransactionType.INCOME) "ingreso" else "gasto"} de $amountText a tu historial.") }
        )
    }

    if (showCategoryConfirmation) {
        AlertDialog(
            onDismissRequest = { showCategoryConfirmation = false },
            confirmButton = {
                TextButton(onClick = {
                    onAddCategory(categoryName, selectedType) { newId ->
                        selectedCategoryId = newId
                    }
                    categoryName = ""
                    showCategoryConfirmation = false
                }) { Text("Crear") }
            },
            dismissButton = {
                TextButton(onClick = { showCategoryConfirmation = false }) { Text("Cancelar") }
            },
            title = { Text("Nueva Categoría") },
            text = { Text("¿Deseas crear la categoría \"$categoryName\" para ${if (selectedType == TransactionType.INCOME) "ingresos" else "gastos"}?") }
        )
    }
}

@Composable
fun TypeButton(
    label: String,
    isSelected: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) color else Color.Transparent
    val contentColor = if (isSelected) Color.White else Color.Gray

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
