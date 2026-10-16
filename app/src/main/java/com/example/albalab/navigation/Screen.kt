package com.example.albalab.navigation

// Sealed class con todas las rutas de la app (guía 10): evita errores al escribir las rutas
sealed class Screen(val route: String) {
    data object Carga : Screen("carga")
    data object Login : Screen("login")
    data object Registro : Screen("registro")
    data object Verificacion : Screen("verificacion")
    data object PruebaVida : Screen("prueba_vida")
    data object Inicio : Screen("inicio")
    data object Programas : Screen("programas")
    data object Colaborar : Screen("colaborar")
    data object MisColaboraciones : Screen("mis_colaboraciones")
    data object Perfil : Screen("perfil")
}
