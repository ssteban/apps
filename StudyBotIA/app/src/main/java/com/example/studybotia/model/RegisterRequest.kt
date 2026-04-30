package com.example.studybotia.model

data class RegisterRequest(
    val username: String,
    val email: String,
    val tipo_usuario: String,
    val contrasena: String
)