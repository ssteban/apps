package com.example.neuroinicial.red

import com.example.neuroinicial.models.LoginRequest
import com.example.neuroinicial.models.LoginResponse
import com.example.neuroinicial.models.TestResultsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login") // I'll assume this is the path, user can correct if different
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("user/obtener_pruebas")
    suspend fun obtenerPruebas(@Header("Authorization") token: String): Response<TestResultsResponse>
}
