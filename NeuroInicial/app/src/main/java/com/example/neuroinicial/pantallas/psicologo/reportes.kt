package com.example.neuroinicial.pantallas.psicologo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.neuroinicial.data.SharedData

// ── Palette (consistent with psicologo screens) ───────────────────────────────
private val PsicPrimary  = Color(0xFF7B1FA2)
private val PsicAccent   = Color(0xFFBA68C8)
private val PsicSurface  = Color(0xFFF9F0FF)
private val CardBg       = Color(0xFFFFFFFF)
private val TextDark     = Color(0xFF4A148C)
private val TextMedium   = Color(0xFF546E7A)
private val DividerColor = Color(0xFFEDE7F6)
private val RiskHigh     = Color(0xFFC62828)
private val RiskHighBg   = Color(0xFFFFEBEE)
private val RiskLow      = Color(0xFF2E7D32)
private val RiskLowBg    = Color(0xFFE8F5E9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesScreen(navController: NavController) {
    val result = SharedData.selectedTestResult
    val isHigh = result?.resultadoModelo?.contains("ALTO", ignoreCase = true) == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reporte Detallado", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PsicPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = PsicSurface
    ) { padding ->
        if (result == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ErrorOutline, null, tint = PsicAccent, modifier = Modifier.size(56.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Sin información", fontWeight = FontWeight.SemiBold, color = TextDark, fontSize = 16.sp)
                    Text("Selecciona un resultado para ver el reporte.", color = TextMedium, fontSize = 13.sp)
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── 1. Student hero card ──────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(PsicPrimary, PsicAccent)))
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = result.nombreEstudiante,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = result.nombreCurso,
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                InfoBadge(Icons.Default.Wc, result.genero)
                                InfoBadge(Icons.Default.Cake, "${result.edad} años")
                            }
                        }
                    }
                }
            }

            // ── 2. Risk banner ────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isHigh) RiskHighBg else RiskLowBg),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isHigh) Icons.Default.Warning else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isHigh) RiskHigh else RiskLow,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = result.resultadoModelo,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isHigh) RiskHigh else RiskLow
                        )
                        Text(
                            text = "Probabilidad: ${(result.probabilidad * 100).toInt()}%",
                            fontSize = 13.sp,
                            color = if (isHigh) RiskHigh.copy(alpha = 0.7f) else RiskLow.copy(alpha = 0.7f)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    // Probability ring indicator
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (isHigh) RiskHigh.copy(alpha = 0.12f) else RiskLow.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${(result.probabilidad * 100).toInt()}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isHigh) RiskHigh else RiskLow
                        )
                    }
                }
            }

            // ── 3. Test info card ─────────────────────────────────────────
            ReportCard(
                title = "Información de la Prueba",
                icon = Icons.Default.Assignment
            ) {
                DetailRow(label = "Nombre de la prueba", value = result.nombrePrueba)
                HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
                DetailRow(label = "Fecha de evaluación", value = result.fecha)
            }

            // ── 4. Responses card ─────────────────────────────────────────
            ReportCard(
                title = "Respuestas / Datos",
                icon = Icons.Default.History
            ) {
                val respuestas = result.respuestas
                when {
                    respuestas.containsKey("url") -> {
                        val imageUrl = respuestas["url"].toString()
                        Text(text = "Evidencia de la prueba (Imagen):", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Imagen de la prueba",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 200.dp, max = 500.dp)
                                    .background(Color.LightGray)
                            )
                        }
                    }
                    respuestas.containsKey("request") -> {
                        val list = respuestas["request"] as? List<*>
                        if (list.isNullOrEmpty()) {
                            Text("Sin respuestas detalladas", fontSize = 13.sp, color = TextMedium)
                        } else {
                            list.forEachIndexed { index, item ->
                                if (index > 0) HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(PsicPrimary.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PsicPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = item.toString(),
                                        fontSize = 14.sp,
                                        color = TextDark,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        Text("No hay respuestas disponibles", fontSize = 13.sp, color = TextMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ── Reusable components ───────────────────────────────────────────────────────

@Composable
private fun ReportCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Section title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PsicPrimary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = PsicPrimary, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontSize = 11.sp, color = TextMedium, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 15.sp, color = TextDark, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun InfoBadge(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Medium)
    }
}