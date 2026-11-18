package com.example.albalab.viewmodel

import com.example.albalab.data.model.Programa
import com.example.albalab.data.repository.ProgramaRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ProgramasViewModelTest : StringSpec({

    val taller = Programa(1, "Preparación para el empleo", "Programa", "Talleres de trabajo", "Materiales", "Donación")
    val regalos = Programa(2, "Regalos corporativos", "Producto", "Packs para empresas", "Insumos", "Compra de productos")

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    "cargarProgramas() debe mostrar todos los programas del microservicio" {
        val mockRepo = mockk<ProgramaRepository>()
        coEvery { mockRepo.obtenerProgramas() } returns listOf(taller, regalos)
        val viewModel = ProgramasViewModel(mockRepo)
        viewModel.cargarProgramas()
        viewModel.programasFiltrados.value shouldContainExactly listOf(taller, regalos)
        viewModel.error.value.shouldBeNull()
    }

    "onBusquedaChange() debe filtrar por nombre, categoría o descripción" {
        val mockRepo = mockk<ProgramaRepository>()
        coEvery { mockRepo.obtenerProgramas() } returns listOf(taller, regalos)
        val viewModel = ProgramasViewModel(mockRepo)
        viewModel.cargarProgramas()

        viewModel.onBusquedaChange("producto")
        viewModel.programasFiltrados.value shouldContainExactly listOf(regalos)
        viewModel.busqueda.value shouldBe "producto"

        viewModel.onBusquedaChange("talleres")
        viewModel.programasFiltrados.value shouldContainExactly listOf(taller)

        viewModel.onBusquedaChange("xyz")
        viewModel.programasFiltrados.value shouldBe emptyList()
    }

    "si el servidor no responde debe mostrar el error de conexión" {
        val mockRepo = mockk<ProgramaRepository>()
        coEvery { mockRepo.obtenerProgramas() } throws IOException("apagado")
        val viewModel = ProgramasViewModel(mockRepo)
        viewModel.cargarProgramas()
        viewModel.error.value!! shouldContain "No se pudo conectar"
    }

    "si el token venció (401) debe pedir volver a iniciar sesión" {
        val mockRepo = mockk<ProgramaRepository>()
        coEvery { mockRepo.obtenerProgramas() } throws HttpException(Response.error<Any>(401, "".toResponseBody(null)))
        val viewModel = ProgramasViewModel(mockRepo)
        viewModel.cargarProgramas()
        viewModel.error.value!! shouldContain "Tu sesión expiró"
    }
})
