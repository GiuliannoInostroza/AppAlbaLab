package com.example.albalab.repository

import com.example.albalab.data.model.Programa
import com.example.albalab.data.remote.MicroserviciosService
import com.example.albalab.data.remote.RetrofitMicroservicios
import com.example.albalab.data.repository.ProgramaRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class ProgramaRepositoryTest : StringSpec({

    "obtenerProgramas() debe pedir los programas enviando el token" {
        val fakeProgramas = listOf(
            Programa(1, "Programa 1", "Programa", "Descripción 1", "Destino 1", "Donación"),
            Programa(2, "Producto 1", "Producto", "Descripción 2", "Destino 2", "Compra de productos")
        )
        RetrofitMicroservicios.token = "abc"
        val mockApi = mockk<MicroserviciosService>()
        // Solo responde si el token llega como "Bearer abc"
        coEvery { mockApi.getProgramas("Bearer abc") } returns fakeProgramas
        val repo = ProgramaRepository(mockApi)
        runTest {
            repo.obtenerProgramas() shouldContainExactly fakeProgramas
        }
    }
})
