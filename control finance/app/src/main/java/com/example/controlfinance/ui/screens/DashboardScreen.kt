package com.example.controlfinance.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.controlfinance.data.model.TransactionType
import com.example.controlfinance.util.DateUtils
import com.example.controlfinance.viewmodel.DashboardUiState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onMonthChange: (Int) -> Unit
) {
    val currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-DO"))
    val expenseCategories = uiState.topCategories.filter { it.type == TransactionType.EXPENSE }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp, top = 16.dp)
    ) {
        // Selector de Mes
        item {
            MonthSelector(
                currentMonthLabel = DateUtils.formatMonthYear(uiState.referenceDate),
                onPreviousMonth = { onMonthChange(-1) },
                onNextMonth = { onMonthChange(1) }
            )
        }

        // Tarjeta de Balance Principal
        item {
            MainBalanceCard(
                balance = currency.format(uiState.balanceSummary.balance),
                income = currency.format(uiState.balanceSummary.totalIncome),
                expense = currency.format(uiState.balanceSummary.totalExpense)
            )
        }

        // Gráfico de Gastos (Donut) y Barras
        if (expenseCategories.isNotEmpty()) {
            item {
                SectionHeader("Distribución de Gastos")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        DonutChartAnimated(
                            categories = expenseCategories,
                            totalExpense = uiState.balanceSummary.totalExpense
                        )
                        Spacer(Modifier.height(24.dp))
                        HorizontalBarChart(
                            categories = expenseCategories,
                            totalExpense = uiState.balanceSummary.totalExpense
                        )
                    }
                }
            }
        }

        // Listado de Categorías Principales
        item {
            SectionHeader("Top Movimientos")
        }

        if (uiState.topCategories.isEmpty()) {
            item {
                EmptyDashboardState()
            }
        } else {
            items(uiState.topCategories.take(6)) { row ->
                CategoryRowProfessional(
                    name = row.categoryName,
                    amount = currency.format(row.totalAmount),
                    isIncome = row.type == TransactionType.INCOME
                )
            }
        }
    }
}

@Composable
fun MonthSelector(
    currentMonthLabel: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mes anterior")
        }
        Text(
            text = currentMonthLabel,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Mes siguiente")
        }
    }
}

@Composable
fun MainBalanceCard(
    balance: String,
    income: String,
    expense: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Balance Actual",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                balance,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp
                )
            )
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BalanceInfoItem("Ingresos", income, Color.White.copy(alpha = 0.9f))
                VerticalDivider(
                    modifier = Modifier.height(40.dp),
                    color = Color.White.copy(alpha = 0.3f)
                )
                BalanceInfoItem("Gastos", expense, Color.White.copy(alpha = 0.9f))
            }
        }
    }
}

@Composable
fun BalanceInfoItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = color.copy(alpha = 0.7f), style = MaterialTheme.typography.labelMedium)
        Text(value, color = color, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DonutChartAnimated(
    categories: List<com.example.controlfinance.data.model.CategoryMonthlyTotal>,
    totalExpense: Double
) {
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(categories) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    val chartColors = listOf(
        Color(0xFFE15A4F), Color(0xFFFFB74D), Color(0xFF4DB6AC),
        Color(0xFF9575CD), Color(0xFF81C784), Color(0xFF4FC3F7)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(150.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                categories.forEachIndexed { index, category ->
                    val sweepAngle = (category.totalAmount / totalExpense).toFloat() * 360f * animationProgress.value
                    drawArc(
                        color = chartColors.getOrElse(index) { Color.Gray },
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 30f, cap = StrokeCap.Round)
                    )
                    startAngle += sweepAngle
                }
            }
            Text(
                "Gastos",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.Gray
            )
        }

        Spacer(Modifier.width(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            categories.take(5).forEachIndexed { index, category ->
                ChartLegendItem(
                    name = category.categoryName,
                    color = chartColors.getOrElse(index) { Color.Gray },
                    percentage = (category.totalAmount / totalExpense * 100).toInt()
                )
            }
        }
    }
}

@Composable
fun ChartLegendItem(name: String, color: Color, percentage: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "$name ($percentage%)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

@Composable
fun CategoryRowProfessional(name: String, amount: String, isIncome: Boolean) {
    val color = if (isIncome) Color(0xFF2E7D32) else Color(0xFFC62828)
    val bgColor = if (isIncome) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        name.take(1).uppercase(),
                        color = color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
            Text(
                (if (isIncome) "+" else "-") + amount,
                color = color,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun HorizontalBarChart(
    categories: List<com.example.controlfinance.data.model.CategoryMonthlyTotal>,
    totalExpense: Double
) {
    val chartColors = listOf(
        Color(0xFFE15A4F), Color(0xFFFFB74D), Color(0xFF4DB6AC),
        Color(0xFF9575CD), Color(0xFF81C784), Color(0xFF4FC3F7)
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        categories.take(5).forEachIndexed { index, category ->
            val percentage = (category.totalAmount / totalExpense).toFloat()
            val animatedWidth = remember { Animatable(0f) }

            LaunchedEffect(category) {
                animatedWidth.animateTo(
                    targetValue = percentage,
                    animationSpec = tween(durationMillis = 1000)
                )
            }

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        category.categoryName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "${(percentage * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedWidth.value)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(chartColors.getOrElse(index) { Color.Gray })
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun EmptyDashboardState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text("📊", fontSize = 40.sp)
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Sin movimientos registrados",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )
        Text(
            "Empieza a registrar tus finanzas para ver el dashboard",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray.copy(alpha = 0.8f)
        )
    }
}
