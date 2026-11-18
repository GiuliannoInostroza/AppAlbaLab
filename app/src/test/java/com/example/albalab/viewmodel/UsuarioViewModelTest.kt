package com.example.albalab.viewmodel

import android.app.Application
import android.net.Uri
import com.example.albalab.data.local.SesionDataStore
import com.example.albalab.data.model.Usuario
import com.example.albalab.data.remote.RetrofitMicroservicios
import com.example.albalab.data.repository.UsuarioRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class UsuarioViewModelTest : StringSpec({

    val correo = "ana@correo.cl"
    val clave = "Clave123"
    val usuario = Usuario(id = 1, nombre = "Ana", correo = correo, claveCifrada = "hash")

    lateinit var dispatcher: TestDispatcher
    lateinit var repo: UsuarioRepository
    lateinit var sesion: SesionDataStore

    // Crea el ViewModel con un repositorio y una sesión simulados (sin Room ni DataStore reales)
    fun crearViewModel(correoGuardado: String? = null, tokenGuardado: String? = null): UsuarioViewModel {
        every { sesion.correoSesion } returns flowOf(correoGuardado)
        every { sesion.tokenSesion } returns flowOf(tokenGuardado)
        val viewModel = UsuarioViewModel(mockk<Application>(relaxed = true), repo, sesion)
        dispatcher.scheduler.advanceUntilIdle()   // termina la pantalla de carga (delay de 600 ms)
        return viewModel
    }

    beforeTest {
        dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
        repo = mockk()
        sesion = mockk()
        coEvery { sesion.guardarSesion(any(), any()) } just Runs
        coEvery { sesion.cerrarSesion() } just Runs
        RetrofitMicroservicios.token = ""
    }
    afterTest { Dispatchers.resetMain() }

    "sin sesión guardada no debe haber usuario y la carga termina" {
        val viewModel = crearViewModel()
        viewModel.usuarioActual.value.shouldBeNull()
        viewModel.cargando.value shouldBe false
    }

    "con sesión guardada debe recuperar el usuario y su token" {
        coEvery { repo.buscar(correo) } returns usuario
        val viewModel = crearViewModel(correoGuardado = correo, tokenGuardado = "token123")
        viewModel.usuarioActual.value shouldBe usuario
        RetrofitMicroservicios.token shouldBe "token123"
    }

    "validarLogin() debe marcar el correo inválido y la clave vacía" {
        val viewModel = crearViewModel()
        viewModel.onLoginCorreoChange("correo-malo")
        viewModel.onLoginClaveChange("")
        viewModel.validarLogin() shouldBe false
        viewModel.login.value.errores.correo.shouldNotBeNull()
        viewModel.login.value.errores.clave.shouldNotBeNull()
    }

    "iniciarSesion() correcto debe guardar la sesión con el token" {
        coEvery { repo.iniciarSesion(correo, clave) } coAnswers {
            RetrofitMicroservicios.token = "token123"
            usuario
        }
        val viewModel = crearViewModel()
        var entro = false
        viewModel.onLoginCorreoChange(correo)
        viewModel.onLoginClaveChange(clave)
        viewModel.iniciarSesion { entro = true }
        entro shouldBe true
        viewModel.usuarioActual.value shouldBe usuario
        coVerify { sesion.guardarSesion(correo, "token123") }
    }

    "iniciarSesion() con clave incorrecta debe mostrar el error" {
        coEvery { repo.iniciarSesion(any(), any()) } returns null
        val viewModel = crearViewModel()
        viewModel.onLoginCorreoChange(correo)
        viewModel.onLoginClaveChange("mala")
        viewModel.iniciarSesion { }
        viewModel.login.value.errores.general shouldBe "Correo o contraseña incorrectos"
    }

    "iniciarSesion() sin servidor debe mostrar el error de conexión" {
        coEvery { repo.iniciarSesion(any(), any()) } throws IOException("apagado")
        val viewModel = crearViewModel()
        viewModel.onLoginCorreoChange(correo)
        viewModel.onLoginClaveChange(clave)
        viewModel.iniciarSesion { }
        viewModel.login.value.errores.general!! shouldContain "No se pudo conectar"
    }

    "validarRegistro() debe revisar cada campo del formulario" {
        val viewModel = crearViewModel()
        viewModel.onNombreChange("An")
        viewModel.onCorreoChange("sin-arroba")
        viewModel.onClaveChange("abc")
        viewModel.onConfirmarClaveChange("otra")
        viewModel.onAceptaTerminosChange(false)
        viewModel.validarRegistro() shouldBe false
        val errores = viewModel.registro.value.errores
        errores.nombre.shouldNotBeNull()
        errores.correo.shouldNotBeNull()
        errores.clave.shouldNotBeNull()
        errores.confirmarClave.shouldNotBeNull()
        errores.aceptaTerminos.shouldNotBeNull()
    }

    "registrar() correcto debe crear la cuenta e iniciar sesión" {
        coEvery { repo.registrar("Ana", correo, clave) } returns true
        coEvery { repo.iniciarSesion(correo, clave) } returns usuario
        val viewModel = crearViewModel()
        viewModel.onNombreChange("Ana")
        viewModel.onCorreoChange(correo)
        viewModel.onClaveChange(clave)
        viewModel.onConfirmarClaveChange(clave)
        viewModel.onAceptaTerminosChange(true)
        var listo = false
        viewModel.registrar { listo = true }
        listo shouldBe true
        viewModel.usuarioActual.value shouldBe usuario
    }

    "registrar() con un correo ya usado debe avisarlo" {
        coEvery { repo.registrar(any(), any(), any()) } returns false
        val viewModel = crearViewModel()
        viewModel.onNombreChange("Ana")
        viewModel.onCorreoChange(correo)
        viewModel.onClaveChange(clave)
        viewModel.onConfirmarClaveChange(clave)
        viewModel.onAceptaTerminosChange(true)
        viewModel.registrar { }
        viewModel.registro.value.errores.correo shouldBe "Ya existe una cuenta con este correo"
    }

    "registrar() sin servidor debe mostrar el error de conexión" {
        coEvery { repo.registrar(any(), any(), any()) } throws IOException("apagado")
        val viewModel = crearViewModel()
        viewModel.onNombreChange("Ana")
        viewModel.onCorreoChange(correo)
        viewModel.onClaveChange(clave)
        viewModel.onConfirmarClaveChange(clave)
        viewModel.onAceptaTerminosChange(true)
        viewModel.registrar { }
        viewModel.registro.value.errores.general!! shouldContain "No se pudo conectar"
    }

    "verificarClave() debe comparar el hash de la clave con el guardado" {
        coEvery { repo.buscar(correo) } returns usuario
        every { repo.cifrarClave(clave) } returns "hash"
        every { repo.cifrarClave("mala") } returns "otro"
        val viewModel = crearViewModel(correoGuardado = correo, tokenGuardado = "t")
        viewModel.verificarClave(clave) shouldBe true
        viewModel.verificarClave("mala") shouldBe false
    }

    "actualizarFoto() y cerrarSesion() deben actualizar el usuario y borrar el token" {
        coEvery { repo.buscar(correo) } returns usuario
        coEvery { repo.actualizarFoto(any(), any()) } just Runs
        val viewModel = crearViewModel(correoGuardado = correo, tokenGuardado = "t")
        viewModel.actualizarFoto(mockk<Uri>())
        coVerify { repo.actualizarFoto(usuario, any()) }
        var cerro = false
        viewModel.cerrarSesion { cerro = true }
        cerro shouldBe true
        viewModel.usuarioActual.value.shouldBeNull()
        RetrofitMicroservicios.token shouldBe ""
    }
})
