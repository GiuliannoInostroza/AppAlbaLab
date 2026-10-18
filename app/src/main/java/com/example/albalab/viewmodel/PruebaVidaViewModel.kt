package com.example.albalab.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.abs

// Lógica de la prueba de vida: pide 3 gestos al azar y revisa si la persona los hace
class PruebaVidaViewModel : ViewModel() {

    private val todosLosGestos = listOf(
        "Parpadea",
        "Sonríe",
        "Gira la cabeza hacia un lado",
        "Mueve la cabeza hacia arriba o abajo"
    )

    // shuffled() desordena la lista y take(3) toma 3: cada intento es distinto
    private val _gestos = MutableStateFlow(todosLosGestos.shuffled().take(3))
    val gestos: StateFlow<List<String>> = _gestos

    private val _pasoActual = MutableStateFlow(0)
    val pasoActual: StateFlow<Int> = _pasoActual

    private val _mensaje = MutableStateFlow("Ubica tu rostro frente a la cámara")
    val mensaje: StateFlow<String> = _mensaje

    private val _completada = MutableStateFlow(false)
    val completada: StateFlow<Boolean> = _completada

    // Para el parpadeo hay que ver primero los ojos cerrados y luego abiertos
    private var ojosCerradosVistos = false

    fun reiniciar() {
        _gestos.value = todosLosGestos.shuffled().take(3)
        _pasoActual.value = 0
        _completada.value = false
        ojosCerradosVistos = false
        _mensaje.value = "Ubica tu rostro frente a la cámara"
    }

    // Se llama con cada cuadro de la cámara. Las probabilidades van de 0 a 1 (ML Kit)
    fun procesarRostro(
        cantidadRostros: Int,
        ojoIzquierdo: Float?,
        ojoDerecho: Float?,
        sonrisa: Float?,
        giroLateral: Float,
        giroVertical: Float
    ) {
        if (_completada.value) return

        if (cantidadRostros == 0) {
            _mensaje.value = "No se ve ningún rostro"
            return
        }
        if (cantidadRostros > 1) {
            // Si aparece otra persona, la prueba vuelve a empezar
            _mensaje.value = "Solo debe haber una persona"
            _pasoActual.value = 0
            ojosCerradosVistos = false
            return
        }

        val gesto = _gestos.value[_pasoActual.value]
        _mensaje.value = gesto

        val cumplido = when (gesto) {
            "Parpadea" -> {
                val izquierdo = ojoIzquierdo ?: 1f
                val derecho = ojoDerecho ?: 1f
                if (izquierdo < 0.2f && derecho < 0.2f) {
                    ojosCerradosVistos = true
                }
                ojosCerradosVistos && izquierdo > 0.7f && derecho > 0.7f
            }
            "Sonríe" -> (sonrisa ?: 0f) > 0.8f
            "Gira la cabeza hacia un lado" -> abs(giroLateral) > 25f
            else -> abs(giroVertical) > 15f
        }

        if (cumplido) {
            ojosCerradosVistos = false
            if (_pasoActual.value == 2) {
                _completada.value = true
                _mensaje.value = "Prueba de vida superada"
            } else {
                _pasoActual.value = _pasoActual.value + 1
            }
        }
    }
}
