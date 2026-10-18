package com.example.albalab.data.model

// Estado del formulario "Quiero colaborar": texto, número, fecha, lista, switch y checkbox
data class ColaboracionUiState(
    val id: Int = 0,                    // 0 = nueva; otro valor = se está editando
    val nombre: String = "",
    val tipo: String = "",
    val personas: String = "",          // Se escribe como texto y se valida como número
    val fecha: Long? = null,
    val mensaje: String = "",
    val recurrente: Boolean = false,
    val aceptaContacto: Boolean = false,
    val errores: ColaboracionErrores = ColaboracionErrores()
)

data class ColaboracionErrores(
    val nombre: String? = null,
    val tipo: String? = null,
    val personas: String? = null,
    val fecha: String? = null,
    val mensaje: String? = null,
    val aceptaContacto: String? = null
)
