package com.example.albalab.repository

import com.example.albalab.data.model.Indicador
import com.example.albalab.data.model.Indicadores
import com.example.albalab.data.remote.ApiService
import com.example.albalab.data.repository.IndicadorRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class IndicadorRepositoryTest : StringSpec({

    "getIndicadores() debe devolver la UF, el dólar y la UTM en ese orden" {
        // 1. Simulamos la respuesta de la API externa
        val uf = Indicador("uf", "Unidad de fomento (UF)", "2026-11-17", 41350.42)
        val dolar = Indicador("dolar", "Dólar observado", "2026-11-17", 1001.17)
        val utm = Indicador("utm", "Unidad Tributaria Mensual (UTM)", "2026-11-01", 69542.0)
        // 2. Creamos un mock de ApiService
        val mockApi = mockk<ApiService>()
        coEvery { mockApi.getIndicadores() } returns Indicadores(uf, dolar, utm)
        // 3. Le pasamos el mock al repositorio
        val repo = IndicadorRepository(mockApi)
        // 4. Ejecutamos el test
        runTest {
            val result = repo.getIndicadores()
            result shouldContainExactly listOf(uf, dolar, utm)
        }
    }
})
