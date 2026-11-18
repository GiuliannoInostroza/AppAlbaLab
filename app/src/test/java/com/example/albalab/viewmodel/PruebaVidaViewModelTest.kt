package com.example.albalab.viewmodel

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

// Simula lo que ML Kit entrega en cada cuadro para el gesto pedido
private fun cumplirGesto(viewModel: PruebaVidaViewModel) {
    when (viewModel.gestos.value[viewModel.pasoActual.value]) {
        "Parpadea" -> {
            viewModel.procesarRostro(1, 0.1f, 0.1f, 0f, 0f, 0f)   // ojos cerrados
            viewModel.procesarRostro(1, 0.9f, 0.9f, 0f, 0f, 0f)   // ojos abiertos
        }
        "Sonríe" -> viewModel.procesarRostro(1, 0.9f, 0.9f, 0.95f, 0f, 0f)
        "Gira la cabeza hacia un lado" -> viewModel.procesarRostro(1, 0.9f, 0.9f, 0f, 30f, 0f)
        else -> viewModel.procesarRostro(1, 0.9f, 0.9f, 0f, 0f, 20f)
    }
}

class PruebaVidaViewModelTest : StringSpec({

    "debe pedir 3 gestos distintos" {
        val viewModel = PruebaVidaViewModel()
        viewModel.gestos.value shouldHaveSize 3
        viewModel.gestos.value.toSet() shouldHaveSize 3
    }

    "sin rostro debe avisar y no avanzar" {
        val viewModel = PruebaVidaViewModel()
        viewModel.procesarRostro(0, null, null, null, 0f, 0f)
        viewModel.mensaje.value shouldBe "No se ve ningún rostro"
        viewModel.pasoActual.value shouldBe 0
    }

    "si aparece otra persona la prueba vuelve al primer gesto" {
        val viewModel = PruebaVidaViewModel()
        cumplirGesto(viewModel)
        viewModel.pasoActual.value shouldBe 1
        viewModel.procesarRostro(2, 0.9f, 0.9f, 0f, 0f, 0f)
        viewModel.mensaje.value shouldBe "Solo debe haber una persona"
        viewModel.pasoActual.value shouldBe 0
    }

    "un gesto que no se cumple no hace avanzar" {
        val viewModel = PruebaVidaViewModel()
        viewModel.procesarRostro(1, 0.9f, 0.9f, 0f, 0f, 0f)
        viewModel.pasoActual.value shouldBe 0
        viewModel.mensaje.value shouldBe viewModel.gestos.value[0]
    }

    "al cumplir los 3 gestos la prueba queda superada (se repite para probar todos los gestos)" {
        val viewModel = PruebaVidaViewModel()
        repeat(10) {
            viewModel.reiniciar()
            repeat(3) { cumplirGesto(viewModel) }
            viewModel.completada.value shouldBe true
            viewModel.mensaje.value shouldBe "Prueba de vida superada"
            // Ya superada, los cuadros siguientes no cambian nada
            viewModel.procesarRostro(0, null, null, null, 0f, 0f)
            viewModel.mensaje.value shouldBe "Prueba de vida superada"
        }
    }
})
