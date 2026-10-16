package com.example.albalab.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla "usuarios" de Room. La clave nunca se guarda en texto: se guarda su hash SHA-256
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val correo: String,
    val claveCifrada: String,
    val fotoUri: String? = null
)
