package com.example.neuroinicial.pantallas.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AdminPrimary = Color(0xFF1565C0)
private val AdminAccent = Color(0xFF42A5F5)
private val AdminSurface = Color(0xFFF0F4FF)
private val AdminCardBg = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF1A237E)
private val TextMedium = Color(0xFF546E7A)

@Composable
fun AdminInicioScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AdminSurface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Header gradient card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(AdminPrimary, AdminAccent)
                        )
                    )
                    .padding(28.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Bienvenido,",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            text = "Administrador",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Panel de control · NeuroInicial",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Accesos rápidos",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Quick action cards grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminQuickCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.People,
                label = "Usuarios",
                color = Color(0xFF1565C0)
            )
            AdminQuickCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.School,
                label = "Colegios",
                color = Color(0xFF00897B)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminQuickCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.BarChart,
                label = "Reportes",
                color = Color(0xFF7B1FA2)
            )
            AdminQuickCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Settings,
                label = "Config.",
                color = Color(0xFFE65100)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AdminCardBg),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = AdminAccent,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Selecciona una opción del menú lateral para gestionar la plataforma.",
                    fontSize = 13.sp,
                    color = TextMedium,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun AdminQuickCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AdminCardBg),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(26.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
        }
    }
}