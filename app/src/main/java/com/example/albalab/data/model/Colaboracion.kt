package com.example.albalab.data.model

// Solicitud de colaboración. Ahora se guarda en el vinculacion-service (persistencia remota)
data class Colaboracion(
    val id: Int = 0,
    val usuarioCorreo: String,      // A qué usuario pertenece
    val nombre: String,             // Persona o empresa que colabora
    val tipo: String,               // Donación, Voluntariado, etc.
    val cantidad: Int,              // Monto, productos o personas, según el tipo
    val fecha: Long,                // Fecha de contacto preferida (milisegundos)
    val mensaje: String,
    val recurrente: Boolean,        // Aporte mensual
    val aceptaContacto: Boolean,
    val estado: String = ""         // Lo asigna el servidor: parte en RECIBIDA
)
