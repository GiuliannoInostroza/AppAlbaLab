package com.example.albalab.data.model

// Estado del formulario de login (guía 11: un data class con los campos y otro con los errores)
data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val errores: LoginErrores = LoginErrores()
)

data class LoginErrores(
    val correo: String? = null,
    val clave: String? = null,
    val general: String? = null     // Ej: correo o contraseña incorrectos
)

// Estado del formulario de registro
data class RegistroUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val confirmarClave: String = "",
    val aceptaTerminos: Boolean = false,
    val errores: RegistroErrores = RegistroErrores()
)

data class RegistroErrores(
    val nombre: String? = null,
    val correo: String? = null,
    val clave: String? = null,
    val confirmarClave: String? = null,
    val aceptaTerminos: String? = null
)
