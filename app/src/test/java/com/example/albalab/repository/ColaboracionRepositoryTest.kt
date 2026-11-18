package com.example.albalab.repository

import com.example.albalab.data.model.Colaboracion
import com.example.albalab.data.remote.MicroserviciosService
import com.example.albalab.data.remote.RetrofitMicroservicios
import com.example.albalab.data.repository.ColaboracionRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class ColaboracionRepositoryTest : StringSpec({

    val nueva = Colaboracion(
        id = 0, usuarioCorreo = "ana@correo.cl", nombre = "Ana", tipo = "Donación",
        cantidad = 20000, fecha = 0L, mensaje = "Quiero ayudar", recurrente = false, aceptaContacto = true
    )
    val existente = nueva.copy(id = 7, estado = "RECIBIDA")

    beforeTest { RetrofitMicroservicios.token = "abc" }

    "obtenerPorUsuario() debe devolver la lista del microservicio" {
        val mockApi = mockk<MicroserviciosService>()
        coEvery { mockApi.getSolicitudes("Bearer abc", "ana@correo.cl") } returns listOf(existente)
        val repo = ColaboracionRepository(mockApi)
        runTest {
            repo.obtenerPorUsuario("ana@correo.cl") shouldContainExactly listOf(existente)
        }
    }

    "guardar() con id 0 debe crear la solicitud (POST)" {
        val mockApi = mockk<MicroserviciosService>()
        coEvery { mockApi.crearSolicitud(any(), any()) } returns existente
        val repo = ColaboracionRepository(mockApi)
        runTest {
            repo.guardar(nueva)
            coVerify(exactly = 1) { mockApi.crearSolicitud("Bearer abc", nueva) }
            coVerify(exactly = 0) { mockApi.actualizarSolicitud(any(), any(), any()) }
        }
    }

    "guardar() con id distinto de 0 debe actualizar la solicitud (PUT)" {
        val mockApi = mockk<MicroserviciosService>()
        coEvery { mockApi.actualizarSolicitud(any(), any(), any()) } returns existente
        val repo = ColaboracionRepository(mockApi)
        runTest {
            repo.guardar(existente)
            coVerify(exactly = 1) { mockApi.actualizarSolicitud("Bearer abc", 7, existente) }
            coVerify(exactly = 0) { mockApi.crearSolicitud(any(), any()) }
        }
    }

    "eliminar() debe borrar la solicitud por su id (DELETE)" {
        val mockApi = mockk<MicroserviciosService>()
        coEvery { mockApi.eliminarSolicitud(any(), any()) } returns Unit
        val repo = ColaboracionRepository(mockApi)
        runTest {
            repo.eliminar(existente)
            coVerify(exactly = 1) { mockApi.eliminarSolicitud("Bearer abc", 7) }
        }
    }
})
