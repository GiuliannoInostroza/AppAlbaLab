package com.example.albalab.repository

import com.example.albalab.data.model.Divisas
import com.example.albalab.data.model.Indicador
import com.example.albalab.data.model.Indicadores
import com.example.albalab.data.remote.ApiRespaldoService
import com.example.albalab.data.remote.ApiService
import com.example.albalab.data.repository.IndicadorRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
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
        val repo = IndicadorRepository(mockApi, mockk())
        // 4. Ejecutamos el test
        runTest {
            val result = repo.getIndicadores()
            result shouldContainExactly listOf(uf, dolar, utm)
        }
    }

    "getIndicadoresRespaldo() debe calcular el dólar y el euro en pesos chilenos" {
        // 1 dólar = 1000 CLP y 1 dólar = 0,8 EUR, entonces 1 euro = 1250 CLP
        val mockRespaldo = mockk<ApiRespaldoService>()
        coEvery { mockRespaldo.getDivisas() } returns Divisas(
            result = "success",
            fechaUnix = 1794873600L,   // 17-11-2026 00:00 UTC
            rates = mapOf("CLP" to 1000.0, "EUR" to 0.8, "USD" to 1.0)
        )
        val repo = IndicadorRepository(mockk(), mockRespaldo)
        runTest {
            val result = repo.getIndicadoresRespaldo()
            result.map { it.codigo } shouldContainExactly listOf("dolar", "euro")
            result[0].valor shouldBe 1000.0
            result[1].valor shouldBe (1250.0 plusOrMinus 0.001)
            result[0].fecha shouldBe "2026-11-17"
        }
    }
})
