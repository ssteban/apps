package com.example.studybotia.pantalla

import android.content.Intent
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.studybotia.model.RegisterRequest
import com.example.studybotia.network.RetrofitClient
import com.example.studybotia.ui.theme.StudyBotIATheme
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

class RegistroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyBotIATheme {
                RegistroScreen(this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(activity: RegistroActivity) {

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var tipo by remember { mutableStateOf("estudiante") }

    // Paleta de colores consistente con Login y Splash
    val azulPrimario = Color(0xFF1E88E5)
    val azulOscuro = Color(0xFF0D47A1)
    val naranjaAcento = Color(0xFFFF9800)
    val fondoClaro = Color(0xFFF5F7FA)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(fondoClaro, Color.White)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Crear cuenta",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = azulOscuro
            )

            Text(
                text = "Únete a la comunidad de estudio AI",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo Usuario
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de usuario") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = azulPrimario) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = azulPrimario,
                    focusedLabelColor = azulPrimario,
                    cursorColor = naranjaAcento
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Correo
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = azulPrimario) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = azulPrimario,
                    focusedLabelColor = azulPrimario,
                    cursorColor = naranjaAcento
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Tipo de Usuario (Dropdown)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = tipo.replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de usuario") },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = azulPrimario) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = azulPrimario,
                        focusedLabelColor = azulPrimario
                    )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    DropdownMenuItem(
                        text = { Text("Estudiante", color = Color.Black) },
                        onClick = {
                            tipo = "estudiante"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Profesor", color = Color.Black) },
                        onClick = {
                            tipo = "profesor"
                            expanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = azulPrimario) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(if (passwordVisible) "🙈" else "👁", fontSize = 20.sp)
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = azulPrimario,
                    focusedLabelColor = azulPrimario,
                    cursorColor = naranjaAcento
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Registro
            Button(
                onClick = {
                    if (username.isBlank() || email.isBlank() || password.isBlank()) {
                        Toast.makeText(activity, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (!email.contains("@")) {
                        Toast.makeText(activity, "Correo inválido", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (password.length < 6) {
                        Toast.makeText(activity, "Mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    activity.lifecycleScope.launch {
                        try {
                            val response = RetrofitClient.instance.register(
                                RegisterRequest(username, email, tipo, password)
                            )
                            if (response.isSuccessful) {
                                Toast.makeText(activity, "¡Cuenta creada con éxito!", Toast.LENGTH_LONG).show()
                                activity.startActivity(Intent(activity, LoginActivity::class.java))
                                activity.finish()
                            } else {
                                Toast.makeText(activity, "Error en el registro", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(activity, "Error de conexión", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azulPrimario),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("REGISTRARSE", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Volver al Login
            TextButton(onClick = {
                activity.startActivity(Intent(activity, LoginActivity::class.java))
                activity.finish()
            }) {
                Text("¿Ya tienes cuenta? ", color = Color.Gray)
                Text("Inicia sesión", color = azulPrimario, fontWeight = FontWeight.Bold)
            }

            TextButton(onClick = {
                activity.startActivity(Intent(activity, ReporteActivity::class.java))
            }) {
                Text("Reportar problema", color = Color.LightGray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}