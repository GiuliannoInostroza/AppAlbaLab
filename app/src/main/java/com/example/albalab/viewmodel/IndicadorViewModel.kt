package com.example.albalab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.albalab.data.model.Indicador
import com.example.albalab.data.repository.IndicadorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Igual que PostViewModel de la guía 14: pide los datos al crearse y los expone en un StateFlow.
// Es "open" para que las pruebas (guía 15) puedan crear una versión falsa
open class IndicadorViewModel(
    private val repository: IndicadorRepository = IndicadorRepository()
) : ViewModel() {

    protected val _indicadores = MutableStateFlow<List<Indicador>>(value = emptyList())
    open val indicadores: StateFlow<List<Indicador>> = _indicadores

    protected val _error = MutableStateFlow<String?>(null)
    open val error: StateFlow<String?> = _error

    init {
        fetchIndicadores()
    }

    open fun fetchIndicadores() {
        viewModelScope.launch {
            try {
                _indicadores.value = repository.getIndicadores()
                _error.value = null
            } catch (e: Exception) {
                println("Error al obtener datos: ${e.localizedMessage}")
                _error.value = "No se pudieron cargar los indicadores. Revisa tu conexión a internet."
            }
        }
    }
}
