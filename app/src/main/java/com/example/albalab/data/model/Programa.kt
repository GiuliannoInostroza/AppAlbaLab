package com.example.albalab.data.model

// Representa un programa, producto o servicio de CORPALIV / Alba Lab
data class Programa(
    val id: Int,                  // Identificador único
    val nombre: String,           // Nombre visible
    val categoria: String,        // "Programa", "Producto" o "Servicio RSE"
    val descripcion: String,      // Qué es y a quién beneficia
    val destinoAporte: String     // En qué se usa el aporte del colaborador
)
