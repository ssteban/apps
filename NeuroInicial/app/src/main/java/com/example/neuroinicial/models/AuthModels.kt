package com.example.neuroinicial.models

import com.google.gson.annotations.SerializedName

// Regular Login Request
data class LoginRequest(
    @SerializedName("correo") val correo: String,
    @SerializedName("contraseña") val contrasena: String
)

// Regular Login Response
data class LoginResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val role: String, // DOCENTE, PSICOLOGO, etc.
    @SerializedName("token") val token: String
)

// Microsoft Login Model (Prepared for future use)
data class MicrosoftLoginRequest(
    @SerializedName("correo") val correo: String,
    @SerializedName("token") val token: String
)

// --- TEST RESULTS MODELS ---

data class TestResult(
    @SerializedName("id_resultado") val idResultado: Int,
    @SerializedName("nombre_prueba") val nombrePrueba: String,
    @SerializedName("nombre_curso") val nombreCurso: String,
    @SerializedName("nombre_estudiante") val nombreEstudiante: String,
    @SerializedName("genero") val genero: String,
    @SerializedName("edad") val edad: Int,
    @SerializedName("respuestas") val respuestas: Map<String, Any>,
    @SerializedName("resultado_modelo") val resultadoModelo: String,
    @SerializedName("probabilidad") val probabilidad: Double,
    @SerializedName("fecha") val fecha: String
)

data class TestResultsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<TestResult>
)
