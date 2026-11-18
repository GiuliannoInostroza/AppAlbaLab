package com.example.albalab.viewmodel

import com.example.albalab.data.model.Indicador
import com.example.albalab.data.repository.IndicadorRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldContain
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class IndicadorViewModelTest : StringSpec({

    val fakeIndicadores = listOf(
        Indicador("uf", "Unidad de fomento (UF)", "2026-11-17", 41350.42),
        Indicador("dolar", "Dólar observado", "2026-11-17", 1001.17)
    )

    // viewModelScope usa el hilo principal de Android: en las pruebas se reemplaza por uno de prueba
    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    "indicadores debe contener los datos esperados después de fetchIndicadores()" {
        // Igual que la guía 15: una subclase falsa que sobrescribe la carga
        val testViewModel = object : IndicadorViewModel() {
            override fun fetchIndicadores() {
                _indicadores.value = fakeIndicadores
            }
        }
        runTest {
            testViewModel.fetchIndicadores()
            testViewModel.indicadores.value shouldContainExactly fakeIndicadores
        }
    }

    "al crearse debe pedir los indicadores al repositorio (simulado con MockK)" {
        val mockRepo = mockk<IndicadorRepository>()
        coEvery { mockRepo.getIndicadores() } returns fakeIndicadores
        runTest {
            val viewModel = IndicadorViewModel(mockRepo)
            viewModel.indicadores.value shouldContainExactly fakeIndicadores
            viewModel.error.value.shouldBeNull()
            viewModel.aviso.value.shouldBeNull()
        }
    }

    "si mindicador.cl está caído debe usar la fuente temporal y avisarlo" {
        val respaldo = listOf(
            Indicador("dolar", "Dólar", "2026-11-17", 995.95),
            Indicador("euro", "Euro", "2026-11-17", 1096.16)
        )
        val mockRepo = mockk<IndicadorRepository>()
        coEvery { mockRepo.getIndicadores() } throws IOException("mindicador caído")
        coEvery { mockRepo.getIndicadoresRespaldo() } returns respaldo
        runTest {
            val viewModel = IndicadorViewModel(mockRepo)
            viewModel.indicadores.value shouldContainExactly respaldo
            viewModel.aviso.value!! shouldContain "sin UF ni UTM"
            viewModel.error.value.shouldBeNull()
        }
    }

    "si mindicador.cl responde no debe pedir la fuente temporal" {
        val mockRepo = mockk<IndicadorRepository>()
        coEvery { mockRepo.getIndicadores() } returns fakeIndicadores
        runTest {
            IndicadorViewModel(mockRepo)
            coVerify(exactly = 0) { mockRepo.getIndicadoresRespaldo() }
        }
    }

    "si no hay internet (ni mindicador ni la fuente temporal) debe mostrar un mensaje de error" {
        val mockRepo = mockk<IndicadorRepository>()
        coEvery { mockRepo.getIndicadores() } throws IOException("sin internet")
        coEvery { mockRepo.getIndicadoresRespaldo() } throws IOException("sin internet")
        runTest {
            val viewModel = IndicadorViewModel(mockRepo)
            viewModel.error.value.shouldNotBeNull()
        }
    }
})
