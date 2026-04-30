package com.example.studybotia.network

import com.example.studybotia.model.IARequest
import com.example.studybotia.model.IAResponse
import com.example.studybotia.model.LoginRequest
import com.example.studybotia.model.LoginResponse
import com.example.studybotia.model.RegisterRequest
import com.example.studybotia.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("user/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("AI/chat")
    suspend fun preguntarIA(
        @Body request: IARequest
    ): Response<IAResponse>
}