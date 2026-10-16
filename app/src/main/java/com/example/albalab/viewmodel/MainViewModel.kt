package com.example.albalab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.albalab.navigation.NavigationEvent
import com.example.albalab.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// Centraliza la navegación: las pantallas piden ir a otra y AppNavigation lo ejecuta (guía 10)
class MainViewModel : ViewModel() {

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    // Emite el evento para ir a una pantalla
    fun navigateTo(screen: Screen, popUpTo: Screen? = null, inclusive: Boolean = false) {
        viewModelScope.launch {
            _navigationEvents.emit(
                NavigationEvent.NavigateTo(
                    route = screen,
                    popUpToRoute = popUpTo,
                    inclusive = inclusive,
                    singleTop = true
                )
            )
        }
    }

    // Emite el evento para volver atrás
    fun navigateBack() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }
}
