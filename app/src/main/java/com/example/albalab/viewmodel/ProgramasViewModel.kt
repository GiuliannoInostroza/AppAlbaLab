package com.example.albalab.viewmodel

import androidx.lifecycle.ViewModel
import com.example.albalab.data.model.Programa
import com.example.albalab.data.repository.ProgramaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Catálogo con filtro dinámico: la lista se filtra con cada letra que se escribe
class ProgramasViewModel : ViewModel() {

    private val todos = ProgramaRepository().obtenerProgramas()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda

    private val _programasFiltrados = MutableStateFlow(todos)
    val programasFiltrados: StateFlow<List<Programa>> = _programasFiltrados

    fun onBusquedaChange(texto: String) {
        _busqueda.value = texto
        _programasFiltrados.value = todos.filter { programa ->
            programa.nombre.contains(texto, ignoreCase = true) ||
                programa.categoria.contains(texto, ignoreCase = true) ||
                programa.descripcion.contains(texto, ignoreCase = true)
        }
    }
}
