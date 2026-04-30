package com.example.studybotia.pantalla

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.studybotia.R
import com.example.studybotia.model.LoginRequest
import com.example.studybotia.network.RetrofitClient
import com.example.studybotia.ui.theme.StudyBotIATheme
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyBotIATheme {
                LoginScreen(this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(activity: LoginActivity) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    // Paleta de colores consistente
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
                .verticalScroll(androidx.compose.foundation.rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo pequeño decorativo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¡Hola de nuevo!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = azulOscuro
            )

            Text(
                text = "Inicia sesión para continuar",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de Usuario
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario o Email") },
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

            // Campo de Contraseña
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

            Spacer(modifier = Modifier.height(30.dp))

            // Botón de Ingreso
            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        Toast.makeText(activity, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (password.length < 6) {
                        Toast.makeText(activity, "Mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    loading = true
                    activity.lifecycleScope.launch {
                        try {
                            val response = RetrofitClient.instance.login(LoginRequest(username, password))
                            loading = false
                            if (response.isSuccessful && response.body()?.status == "ok") {
                                Toast.makeText(activity, "Bienvenido", Toast.LENGTH_SHORT).show()
                                val prefs = activity.getSharedPreferences("studybot", android.content.Context.MODE_PRIVATE)
                                prefs.edit().putString("username", username).apply()
                                activity.startActivity(Intent(activity, ChatActivity::class.java))
                                activity.finish()
                            } else {
                                Toast.makeText(activity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            loading = false
                            Toast.makeText(activity, "Error de conexión", Toast.LENGTH_SHORT).show()
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
                if (loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("ENTRAR", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Enlaces inferiores
            TextButton(onClick = {
                activity.startActivity(Intent(activity, RegistroActivity::class.java))
            }) {
                Text("¿No tienes cuenta? ", color = Color.Gray)
                Text("Regístrate", color = azulPrimario, fontWeight = FontWeight.Bold)
            }

            TextButton(
                onClick = { activity.startActivity(Intent(activity, ReporteActivity::class.java)) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Reportar problema", color = Color.LightGray, fontSize = 12.sp)
            }
        }
    }
}