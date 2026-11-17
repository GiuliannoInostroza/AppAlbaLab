package com.example.albalab.data.model

// Cuerpo que se envía a /auth/login y /auth/register del auth-service
data class Credenciales(
    val email: String,
    val password: String
)

// Respuesta de /auth/login: status "ok" o "error" y el token JWT
data class RespuestaLogin(
    val status: String,
    val token: String
)

// Respuesta de /auth/register
data class RespuestaRegistro(
    val message: String
)
