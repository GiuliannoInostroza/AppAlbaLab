package com.example.albalab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.albalab.data.model.Programa
import com.example.albalab.data.remote.RetrofitMicroservicios
import com.example.albalab.data.repository.ProgramaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Catálogo con filtro dinámico: los programas llegan del contenidos-service
// y la lista se filtra con cada letra que se escribe
class ProgramasViewModel(
    private val repositorio: ProgramaRepository = ProgramaRepository()
) : ViewModel() {

    private var todos: List<Programa> = emptyList()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda

    private val _programasFiltrados = MutableStateFlow<List<Programa>>(emptyList())
    val programasFiltrados: StateFlow<List<Programa>> = _programasFiltrados

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Se llama al abrir la pantalla (ya con sesión iniciada, porque el gateway pide token)
    fun cargarProgramas() {
        viewModelScope.launch {
            try {
                todos = repositorio.obtenerProgramas()
                _error.value = null
                onBusquedaChange(_busqueda.value)
            } catch (e: Exception) {
                _error.value = RetrofitMicroservicios.mensajeDeError(e)
            }
        }
    }

    fun onBusquedaChange(texto: String) {
        _busqueda.value = texto
        _programasFiltrados.value = todos.filter { programa ->
            programa.nombre.contains(texto, ignoreCase = true) ||
                programa.categoria.contains(texto, ignoreCase = true) ||
                programa.descripcion.contains(texto, ignoreCase = true)
        }
    }
}
