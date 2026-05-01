package com.example.neuroinicial.pantallas.psicologo

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.neuroinicial.data.SharedData
import com.example.neuroinicial.data.TokenManager
import com.example.neuroinicial.models.TestResult
import com.example.neuroinicial.red.AuthService
import com.example.neuroinicial.red.RetrofitClient

// ── Palette (matches PsicologoInicioScreen) ──────────────────────────────────
private val PsicPrimary   = Color(0xFF7B1FA2)
private val PsicAccent    = Color(0xFFBA68C8)
private val PsicSurface   = Color(0xFFF9F0FF)
private val CardBg        = Color(0xFFFFFFFF)
private val TextDark      = Color(0xFF4A148C)
private val TextMedium    = Color(0xFF546E7A)
private val RowEven       = Color(0xFFFAF5FF)
private val RowOdd        = Color(0xFFFFFFFF)
private val DividerColor  = Color(0xFFEDE7F6)
private val RiskHigh      = Color(0xFFC62828)
private val RiskHighBg    = Color(0xFFFFEBEE)
private val RiskLow       = Color(0xFF2E7D32)
private val RiskLowBg     = Color(0xFFE8F5E9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultadosScreen(navController: NavController) {
    val context       = LocalContext.current
    val tokenManager  = remember { TokenManager(context) }
    val authService   = remember { RetrofitClient.createService(AuthService::class.java) }

    var results       by remember { mutableStateOf<List<TestResult>>(emptyList()) }
    var isLoading     by remember { mutableStateOf(true) }
    var searchQuery   by remember { mutableStateOf("") }
    var currentPage   by remember { mutableStateOf(0) }
    var filterRisk    by remember { mutableStateOf("Todos") }   // "Todos" | "Alto" | "Bajo"
    val pageSize = 10

    LaunchedEffect(Unit) {
        val token = tokenManager.getToken()
        if (token != null) {
            try {
                val response = authService.obtenerPruebas("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    results = response.body()!!.data
                } else {
                    Toast.makeText(context, "Error al obtener datos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    // ── Filters ──────────────────────────────────────────────────────────────
    val filteredResults = results.filter { r ->
        val matchesSearch = r.nombreEstudiante.contains(searchQuery, ignoreCase = true) ||
                r.nombrePrueba.contains(searchQuery, ignoreCase = true)
        val matchesRisk = when (filterRisk) {
            "Alto" -> r.resultadoModelo.contains("ALTO", ignoreCase = true)
            "Bajo" -> !r.resultadoModelo.contains("ALTO", ignoreCase = true)
            else   -> true
        }
        matchesSearch && matchesRisk
    }

    // Reset page whenever filters change
    LaunchedEffect(searchQuery, filterRisk) { currentPage = 0 }

    val totalPages       = if (filteredResults.isEmpty()) 1 else (filteredResults.size + pageSize - 1) / pageSize
    val paginatedResults = filteredResults.drop(currentPage * pageSize).take(pageSize)
    val totalHigh        = results.count { it.resultadoModelo.contains("ALTO", ignoreCase = true) }
    val totalLow         = results.size - totalHigh

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PsicSurface)
    ) {
        // ── Gradient header ───────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(PsicPrimary, PsicAccent)))
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Text(
                    text = "Resultados de Pruebas",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "${results.size} registros totales",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        }

        // ── Summary chips ─────────────────────────────────────────────────
        if (!isLoading && results.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryChip(
                    label = "Total",
                    value = results.size.toString(),
                    color = PsicPrimary,
                    selected = filterRisk == "Todos",
                    onClick = { filterRisk = "Todos" }
                )
                SummaryChip(
                    label = "Riesgo Alto",
                    value = totalHigh.toString(),
                    color = RiskHigh,
                    selected = filterRisk == "Alto",
                    onClick = { filterRisk = "Alto" }
                )
                SummaryChip(
                    label = "Riesgo Bajo",
                    value = totalLow.toString(),
                    color = RiskLow,
                    selected = filterRisk == "Bajo",
                    onClick = { filterRisk = "Bajo" }
                )
            }
        }

        // ── Search bar ────────────────────────────────────────────────────
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Buscar estudiante o prueba...", color = TextMedium, fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PsicPrimary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = TextMedium)
                    }
                }
            },
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PsicPrimary,
                unfocusedBorderColor = DividerColor,
                focusedContainerColor = CardBg,
                unfocusedContainerColor = CardBg,
                focusedTextColor = TextDark,
                unfocusedTextColor = TextDark,
                cursorColor = PsicPrimary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Body ──────────────────────────────────────────────────────────
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PsicPrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Cargando resultados...", color = TextMedium, fontSize = 13.sp)
                    }
                }
            }

            filteredResults.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = PsicAccent,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Sin resultados", fontWeight = FontWeight.SemiBold, color = TextDark, fontSize = 16.sp)
                        Text("Intenta con otra búsqueda o filtro", color = TextMedium, fontSize = 13.sp)
                    }
                }
            }

            else -> {
                // Table card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column {
                        // Table header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(listOf(PsicPrimary, PsicAccent)),
                                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Estudiante", modifier = Modifier.weight(2f), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Prueba", modifier = Modifier.weight(2f), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Riesgo", modifier = Modifier.weight(1f), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(60.dp))
                        }

                        LazyColumn {
                            itemsIndexed(paginatedResults) { index, result ->
                                ResultRow(
                                    result = result,
                                    rowBg = if (index % 2 == 0) RowEven else RowOdd
                                ) {
                                    SharedData.selectedTestResult = result
                                    navController.navigate("psicologo_reportes")
                                }
                                if (index < paginatedResults.lastIndex) {
                                    Divider(color = DividerColor, thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }

                // ── Pagination ────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { if (currentPage > 0) currentPage-- },
                        enabled = currentPage > 0,
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = PsicSurface, contentColor = PsicPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Anterior", fontSize = 13.sp)
                    }

                    Text(
                        text = "${currentPage + 1} / $totalPages",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )

                    FilledTonalButton(
                        onClick = { if (currentPage < totalPages - 1) currentPage++ },
                        enabled = currentPage < totalPages - 1,
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = PsicSurface, contentColor = PsicPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Siguiente", fontSize = 13.sp)
                        Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// ── Result row ────────────────────────────────────────────────────────────────
@Composable
fun ResultRow(result: TestResult, rowBg: Color, onInfoClick: () -> Unit) {
    val isHigh = result.resultadoModelo.contains("ALTO", ignoreCase = true)
    val riskColor = if (isHigh) RiskHigh else RiskLow
    val riskBg    = if (isHigh) RiskHighBg else RiskLowBg
    val riskLabel = if (isHigh) "Alto" else "Bajo"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .clickable { onInfoClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Student
        Column(modifier = Modifier.weight(2f)) {
            Text(
                text = result.nombreEstudiante,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = TextDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = result.fecha,
                fontSize = 11.sp,
                color = TextMedium
            )
        }
        // Test name
        Text(
            text = result.nombrePrueba,
            modifier = Modifier.weight(2f),
            fontSize = 13.sp,
            color = TextMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        // Risk badge
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(riskBg)
                .padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = riskLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = riskColor)
        }
        // Action
        IconButton(onClick = onInfoClick, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Detalles", tint = PsicPrimary)
        }
    }
}

// ── Summary chip ──────────────────────────────────────────────────────────────
@Composable
fun SummaryChip(label: String, value: String, color: Color, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) color else color.copy(alpha = 0.10f)
    val fg = if (selected) Color.White else color

    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(if (selected) 4.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = fg)
            Text(text = label, fontSize = 11.sp, color = fg.copy(alpha = 0.85f))
        }
    }
}