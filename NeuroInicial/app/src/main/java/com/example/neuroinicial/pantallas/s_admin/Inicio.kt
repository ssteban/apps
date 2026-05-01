package com.example.neuroinicial.pantallas.s_admin

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SAdminPrimary = Color(0xFFB71C1C)
private val SAdminAccent = Color(0xFFEF9A9A)
private val SAdminSurface = Color(0xFFFFF5F5)
private val CardBg = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF7F0000)
private val TextMedium = Color(0xFF546E7A)

@Composable
fun SAdminInicioScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SAdminSurface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Header card
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
                            colors = listOf(SAdminPrimary, Color(0xFFE53935))
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
                            imageVector = Icons.Default.SupervisorAccount,
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
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Text(
                            text = "Super Admin",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Control total · NeuroInicial",
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

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SAdminQuickCard(Modifier.weight(1f), Icons.Default.ManageAccounts, "Admins", SAdminPrimary)
            SAdminQuickCard(Modifier.weight(1f), Icons.Default.Domain, "Instituciones", Color(0xFF1565C0))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SAdminQuickCard(Modifier.weight(1f), Icons.Default.Security, "Permisos", Color(0xFF7B1FA2))
            SAdminQuickCard(Modifier.weight(1f), Icons.Default.MonitorHeart, "Sistema", Color(0xFF00897B))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Warning info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFE65100),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Tienes acceso completo al sistema. Selecciona una opción del menú lateral.",
                    fontSize = 13.sp,
                    color = Color(0xFF5D4037),
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun SAdminQuickCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
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