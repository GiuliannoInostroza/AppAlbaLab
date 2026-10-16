package com.example.albalab.navigation

// Los posibles eventos de navegación que emite el MainViewModel (guía 10)
sealed class NavigationEvent {

    // Ir a una pantalla. popUpToRoute borra de la pila las pantallas hasta esa ruta
    data class NavigateTo(
        val route: Screen,
        val popUpToRoute: Screen? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false
    ) : NavigationEvent()

    // Volver a la pantalla anterior
    object PopBackStack : NavigationEvent()
}
