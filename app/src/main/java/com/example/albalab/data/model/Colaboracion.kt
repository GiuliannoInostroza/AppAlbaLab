package com.example.albalab.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla "colaboraciones": cada registro de interés que hace un usuario
@Entity(tableName = "colaboraciones")
data class Colaboracion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioCorreo: String,      // A qué usuario pertenece
    val nombre: String,             // Persona o empresa que colabora
    val tipo: String,               // Donación, Voluntariado, etc.
    val cantidad: Int,              // Monto, productos o personas, según el tipo
    val fecha: Long,                // Fecha de contacto preferida (milisegundos)
    val mensaje: String,
    val recurrente: Boolean,        // Aporte mensual
    val aceptaContacto: Boolean
)
