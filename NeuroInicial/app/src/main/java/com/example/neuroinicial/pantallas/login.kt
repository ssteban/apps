package com.example.neuroinicial.pantallas

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.neuroinicial.R
import com.example.neuroinicial.data.TokenManager
import com.example.neuroinicial.models.LoginRequest
import com.example.neuroinicial.red.AuthService
import com.example.neuroinicial.red.RetrofitClient
import kotlinx.coroutines.launch

// Color palette
private val PrimaryBlue = Color(0xFF1565C0)
private val AccentBlue = Color(0xFF42A5F5)
private val CardBackground = Color(0xF2FFFFFF) // semi-transparent white
private val TextDark = Color(0xFF1A237E)
private val TextMedium = Color(0xFF546E7A)
private val FieldBackground = Color(0xFFF5F7FA)
private val BorderColor = Color(0xFFCFD8DC)
private val MicrosoftBlue = Color(0xFF0078D4)

@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tokenManager = remember { TokenManager(context) }
    val authService = remember { RetrofitClient.createService(AuthService::class.java) }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Background image ──────────────────────────────────────────────
        Image(
            painter = painterResource(id = R.drawable.fondo),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // ── Dark gradient overlay for readability ─────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x55000000),
                            Color(0xBB000000)
                        )
                    )
                )
        )

        // ── Content ───────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Logo + app name
            Image(
                painter = painterResource(id = R.drawable.neuro),
                contentDescription = "App Logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "NeuroInicial",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Text(
                text = "Plataforma de gestión educativa",
                fontSize = 13.sp,
                color = Color(0xBBFFFFFF),
                textAlign = TextAlign.Center,
                letterSpacing = 0.3.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Frosted-glass card ────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Ingresa tus credenciales para continuar",
                        fontSize = 12.sp,
                        color = TextMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Email field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = BorderColor,
                            focusedLabelColor = PrimaryBlue,
                            unfocusedLabelColor = TextMedium,
                            cursorColor = PrimaryBlue,
                            focusedContainerColor = FieldBackground,
                            unfocusedContainerColor = FieldBackground
                        ),
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            TextButton(
                                onClick = { passwordVisible = !passwordVisible },
                                contentPadding = PaddingValues(end = 8.dp)
                            ) {
                                Text(
                                    text = if (passwordVisible) "Ocultar" else "Ver",
                                    fontSize = 11.sp,
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = BorderColor,
                            focusedLabelColor = PrimaryBlue,
                            unfocusedLabelColor = TextMedium,
                            cursorColor = PrimaryBlue,
                            focusedContainerColor = FieldBackground,
                            unfocusedContainerColor = FieldBackground
                        ),
                        enabled = !isLoading
                    )

                    // Forgot password
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        TextButton(
                            onClick = { /* TODO: forgot password */ },
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Text(
                                text = "¿Olvidaste tu contraseña?",
                                fontSize = 12.sp,
                                color = AccentBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Login button
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = PrimaryBlue,
                            modifier = Modifier.size(42.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Button(
                            onClick = {
                                if (email.isNotEmpty() && password.isNotEmpty()) {
                                    isLoading = true
                                    scope.launch {
                                        try {
                                            val response = authService.login(LoginRequest(email, password))
                                            if (response.isSuccessful && response.body() != null) {
                                                val loginResponse = response.body()!!
                                                tokenManager.saveToken(loginResponse.token)
                                                tokenManager.saveRole(loginResponse.role)
                                                onLoginSuccess(loginResponse.role)
                                            } else {
                                                Toast.makeText(context, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text(
                                text = "Iniciar Sesión",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Divider with text
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(modifier = Modifier.weight(1f), color = BorderColor)
                        Text(
                            text = "  o continúa con  ",
                            fontSize = 12.sp,
                            color = TextMedium
                        )
                        Divider(modifier = Modifier.weight(1f), color = BorderColor)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Microsoft button
                    OutlinedButton(
                        onClick = { /* TODO: Microsoft login */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = TextDark
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                        enabled = !isLoading
                    ) {
                        // Microsoft logo squares (drawn with colored boxes)
                        Row(
                            modifier = Modifier.size(18.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(0.5.dp).background(Color(0xFFF25022)))
                                Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(0.5.dp).background(Color(0xFF00A4EF)))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(0.5.dp).background(Color(0xFF7FBA00)))
                                Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(0.5.dp).background(Color(0xFFFFB900)))
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Iniciar sesión con Microsoft",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer
            Text(
                text = "© 2025 NeuroInicial · Todos los derechos reservados",
                fontSize = 11.sp,
                color = Color(0x99FFFFFF),
                textAlign = TextAlign.Center
            )
        }
    }
}