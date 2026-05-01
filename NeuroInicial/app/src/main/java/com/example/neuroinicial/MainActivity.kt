package com.example.neuroinicial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import com.example.neuroinicial.data.TokenManager
import com.example.neuroinicial.pantallas.*
import com.example.neuroinicial.pantallas.admin.*
import com.example.neuroinicial.pantallas.docente.*
import com.example.neuroinicial.pantallas.psicologo.*
import com.example.neuroinicial.pantallas.s_admin.*
import com.example.neuroinicial.ui.theme.NeuroInicialTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeuroInicialTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val tokenManager = remember { TokenManager(context) }
                
                val onLogout = {
                    tokenManager.clear()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }

                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen {
                            val token = tokenManager.getToken()
                            val role = tokenManager.getRole()
                            
                            if (token != null && role != null) {
                                navigateBasedOnRole(navController, role)
                            } else {
                                navController.navigate("login") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        }
                    }
                    composable("login") {
                        LoginScreen { role ->
                            navigateBasedOnRole(navController, role)
                        }
                    }

                    // --- DOCENTE ROUTES ---
                    composable("docente_inicio") {
                        MainLayout(navController, "Inicio", "DOCENTE", onLogout) { DocenteInicioScreen() }
                    }
                    composable("docente_disgrafia") {
                        MainLayout(navController, "Disgrafia", "DOCENTE", onLogout) { DisgrafiaScreen() }
                    }
                    composable("docente_dislexia") {
                        MainLayout(navController, "Dislexia", "DOCENTE", onLogout) { DislexiaScreen() }
                    }
                    composable("docente_tdah") {
                        MainLayout(navController, "TDAH", "DOCENTE", onLogout) { TdahScreen() }
                    }

                    // --- PSICOLOGO ROUTES ---
                    composable("psicologo_inicio") {
                        MainLayout(navController, "Inicio", "PSICOLOGO", onLogout) { PsicologoInicioScreen(navController) }
                    }
                    composable("psicologo_reportes") {
                        MainLayout(navController, "Reportes", "PSICOLOGO", onLogout) { ReportesScreen(navController) }
                    }
                    composable("psicologo_resultados") {
                        MainLayout(navController, "Resultados", "PSICOLOGO", onLogout) { ResultadosScreen(navController) }
                    }

                    // --- ADMIN ROUTES ---
                    composable("admin_inicio") {
                        MainLayout(navController, "Inicio", "ADMIN", onLogout) { AdminInicioScreen() }
                    }

                    // --- S_ADMIN ROUTES ---
                    composable("s_admin_inicio") {
                        MainLayout(navController, "Inicio", "S_ADMIN", onLogout) { SAdminInicioScreen() }
                    }
                }
            }
        }
    }

    private fun navigateBasedOnRole(navController: androidx.navigation.NavController, role: String) {
        val destination = when (role.uppercase()) {
            "ADMINISTRADOR" -> "admin_inicio"
            "S_ADMINISTRADOR" -> "s_admin_inicio"
            "DOCENTE" -> "docente_inicio"
            "PSICOLOGO" -> "psicologo_inicio"
            else -> "login"
        }
        navController.navigate(destination) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000) // 2 seconds delay
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.neuro),
                contentDescription = "App Logo",
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "NeuroInicial",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black
            )
        }
    }
}