package com.example.studybotia.pantalla

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studybotia.ui.theme.StudyBotIATheme
import android.os.Build
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReporteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyBotIATheme {
                ReporteScreen(this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReporteScreen(activity: ReporteActivity) {

    var asunto by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var mostrarDialog by remember { mutableStateOf(false) }

    // Paleta de colores consistente
    val azulPrimario = Color(0xFF1E88E5)
    val azulOscuro = Color(0xFF0D47A1)
    val fondoClaro = Color(0xFFF5F7FA)
    val naranjaAcento = Color(0xFFFF9800)

    // 🔹 POPUP CONFIRMACIÓN (Diseño mejorado)
    if (mostrarDialog) {
        AlertDialog(
            onDismissRequest = { mostrarDialog = false },
            icon = { Icon(Icons.Default.Info, contentDescription = null, tint = azulPrimario) },
            title = {
                Text(text = "Confirmar envío", fontWeight = FontWeight.Bold, color = azulOscuro)
            },
            text = {
                Text(text = "¿Deseas abrir tu aplicación de correo para enviar este reporte técnico?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialog = false
                        enviarCorreo(activity, asunto, mensaje)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = azulPrimario)
                ) {
                    Text("Enviar Correo", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(fondoClaro, Color.White)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Icono y Título
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = azulPrimario
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Reportar problema",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = azulOscuro
            )

            Text(
                text = "Ayúdanos a mejorar AprendeBot",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 🔹 INPUT ASUNTO
            OutlinedTextField(
                value = asunto,
                onValueChange = { asunto = it },
                label = { Text("Asunto del problema") },
                placeholder = { Text("Ej: No carga el chat") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = azulPrimario,
                    focusedLabelColor = azulPrimario,
                    cursorColor = naranjaAcento
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 INPUT MENSAJE (Más alto)
            OutlinedTextField(
                value = mensaje,
                onValueChange = { mensaje = it },
                label = { Text("Descripción detallada") },
                placeholder = { Text("Cuéntanos qué pasó...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = azulPrimario,
                    focusedLabelColor = azulPrimario,
                    cursorColor = naranjaAcento
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 🔹 BOTÓN ENVIAR
            Button(
                onClick = {
                    if (asunto.isBlank() || mensaje.isBlank()) {
                        Toast.makeText(activity, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    } else {
                        mostrarDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azulPrimario),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("GENERAR REPORTE", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 BOTÓN VOLVER
            TextButton(
                onClick = { activity.finish() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Volver", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// Función auxiliar para mantener el código limpio
private fun enviarCorreo(activity: ReporteActivity, asunto: String, mensaje: String) {
    val prefs = activity.getSharedPreferences("studybot", android.content.Context.MODE_PRIVATE)
    val usuario = prefs.getString("username", "No identificado") ?: "No identificado"

    val versionApp = try {
        val pInfo = activity.packageManager.getPackageInfo(activity.packageName, 0)
        pInfo.versionName ?: "1.0"
    } catch (e: Exception) { "1.0" }

    val dispositivo = "${Build.MANUFACTURER} ${Build.MODEL}"
    val androidVersion = Build.VERSION.RELEASE
    val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

    val cuerpo = """
        Detalles del Reporte:
        -------------------------
        Usuario: $usuario
        Versión App: $versionApp
        Dispositivo: $dispositivo
        Android: $androidVersion
        Fecha: $fecha
        
        Mensaje del Usuario:
        $mensaje
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("reporte-bot-study@studybot.com"))
        putExtra(Intent.EXTRA_SUBJECT, "[REPORTE] $asunto")
        putExtra(Intent.EXTRA_TEXT, cuerpo)
    }

    try {
        activity.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(activity, "No se encontró app de correo", Toast.LENGTH_SHORT).show()
    }
}