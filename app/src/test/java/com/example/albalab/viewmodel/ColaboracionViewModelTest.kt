package com.example.albalab.viewmodel

import android.app.Application
import com.example.albalab.data.Notificaciones
import com.example.albalab.data.model.Colaboracion
import com.example.albalab.data.repository.ColaboracionRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.Runs
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ColaboracionViewModelTest : StringSpec({

    val correo = "ana@correo.cl"
    val unDia = 24 * 60 * 60 * 1000L
    lateinit var repo: ColaboracionRepository

    fun crearViewModel() = ColaboracionViewModel(mockk<Application>(relaxed = true), repo)

    // Llena el formulario con datos válidos
    fun llenarFormulario(viewModel: ColaboracionViewModel, tipo: String, cantidad: String, fecha: Long) {
        viewModel.onNombreChange("Empresa Uno")
        viewModel.onTipoChange(tipo)
        viewModel.onCantidadChange(cantidad)
        viewModel.onFechaChange(fecha)
        viewModel.onMensajeChange("Queremos apoyar los talleres")
        viewModel.onRecurrenteChange(true)
        viewModel.onAceptaContactoChange(true)
    }

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repo = mockk()
        // Las notificaciones son de Android: se simulan para que no se muestren de verdad
        mockkObject(Notificaciones)
        every { Notificaciones.mostrar(any(), any(), any(), any()) } just Runs
        every { Notificaciones.programarRecordatorio(any(), any(), any(), any()) } just Runs
    }
    afterTest {
        unmockkObject(Notificaciones)
        Dispatchers.resetMain()
    }

    "un formulario vacío no es válido y marca todos los errores" {
        val viewModel = crearViewModel()
        viewModel.validarFormulario() shouldBe false
        val errores = viewModel.formulario.value.errores
        errores.nombre.shouldNotBeNull()
        errores.tipo.shouldNotBeNull()
        errores.fecha.shouldNotBeNull()
        errores.mensaje.shouldNotBeNull()
        errores.aceptaContacto.shouldNotBeNull()
    }

    "una donación menor a 1.000 CLP no es válida" {
        val viewModel = crearViewModel()
        llenarFormulario(viewModel, "Donación", "500", System.currentTimeMillis())
        viewModel.validarFormulario() shouldBe false
        viewModel.formulario.value.errores.cantidad shouldBe "Ingresa un monto entre 1.000 y 10.000.000 CLP"
    }

    "en voluntariado la cantidad debe estar entre 1 y 500" {
        val viewModel = crearViewModel()
        llenarFormulario(viewModel, "Voluntariado", "0", System.currentTimeMillis())
        viewModel.validarFormulario() shouldBe false
        viewModel.formulario.value.errores.cantidad shouldBe "Ingresa un número entre 1 y 500"
    }

    "la fecha no puede ser anterior a hoy" {
        val viewModel = crearViewModel()
        llenarFormulario(viewModel, "Donación", "20000", System.currentTimeMillis() - 3 * unDia)
        viewModel.validarFormulario() shouldBe false
        viewModel.formulario.value.errores.fecha shouldBe "La fecha no puede ser anterior a hoy"
    }

    "un formulario completo y correcto es válido" {
        val viewModel = crearViewModel()
        llenarFormulario(viewModel, "Donación", "20000", System.currentTimeMillis())
        viewModel.validarFormulario() shouldBe true
    }

    "la cantidad solo acepta números y se borra al cambiar el tipo" {
        val viewModel = crearViewModel()
        viewModel.onTipoChange("Donación")
        viewModel.onCantidadChange("123")
        viewModel.onCantidadChange("12a")
        viewModel.formulario.value.cantidad shouldBe "123"
        viewModel.onTipoChange("Voluntariado")
        viewModel.formulario.value.cantidad shouldBe ""
    }

    "las etiquetas y descripciones cambian según el tipo" {
        val viewModel = crearViewModel()
        viewModel.etiquetaCantidad("Donación") shouldBe "Monto a donar (CLP)"
        viewModel.etiquetaCantidad("Compra de productos") shouldBe "Cantidad de productos"
        viewModel.etiquetaCantidad("Voluntariado") shouldBe "Personas que participan"
        viewModel.etiquetaRecurrente("Donación") shouldBe "Donación mensual"
        viewModel.etiquetaRecurrente("Compra de productos") shouldBe "Pedido mensual"
        viewModel.etiquetaRecurrente("") shouldBe "Colaboración mensual"
        viewModel.etiquetaRecurrente("Capacitación") shouldBe "Participar todos los meses"
        viewModel.describirCantidad("Donación", 20000) shouldBe "\$20000 CLP"
        viewModel.describirCantidad("Compra de productos", 3) shouldBe "3 producto(s)"
        viewModel.describirCantidad("Voluntariado", 5) shouldBe "5 persona(s)"
    }

    "nuevoFormulario() y editar() deben preparar el formulario" {
        val viewModel = crearViewModel()
        viewModel.nuevoFormulario(tipo = "Capacitación", nombre = "Ana")
        viewModel.formulario.value.tipo shouldBe "Capacitación"
        viewModel.formulario.value.nombre shouldBe "Ana"
        val existente = Colaboracion(4, correo, "Ana", "Donación", 5000, 0L, "Mensaje de prueba", true, true)
        viewModel.editar(existente)
        viewModel.formulario.value.id shouldBe 4
        viewModel.formulario.value.cantidad shouldBe "5000"
        viewModel.guardadoCorrectamente.value shouldBe false
    }

    "cargarColaboraciones() debe traer la lista del microservicio" {
        val lista = listOf(Colaboracion(1, correo, "Ana", "Donación", 5000, 0L, "Mensaje", false, true, "RECIBIDA"))
        coEvery { repo.obtenerPorUsuario(correo) } returns lista
        val viewModel = crearViewModel()
        viewModel.cargarColaboraciones(correo)
        viewModel.colaboraciones.value shouldContainExactly lista
        viewModel.error.value.shouldBeNull()
    }

    "guardar() una colaboración nueva debe enviarla, notificar y recargar la lista" {
        coEvery { repo.guardar(any()) } just Runs
        coEvery { repo.obtenerPorUsuario(correo) } returns emptyList()
        val viewModel = crearViewModel()
        llenarFormulario(viewModel, "Donación", "20000", System.currentTimeMillis() + 2 * unDia)
        viewModel.guardar(correo)
        viewModel.guardadoCorrectamente.value shouldBe true
        coVerify { repo.guardar(match { it.id == 0 && it.cantidad == 20000 && it.usuarioCorreo == correo }) }
        verify(exactly = 1) { Notificaciones.mostrar(any(), any(), any(), any()) }
        coVerify { repo.obtenerPorUsuario(correo) }
        viewModel.ocultarMensaje()
        viewModel.guardadoCorrectamente.value shouldBe false
    }

    "guardar() con fecha de hoy programa el recordatorio para hoy" {
        coEvery { repo.guardar(any()) } just Runs
        coEvery { repo.obtenerPorUsuario(correo) } returns emptyList()
        val viewModel = crearViewModel()
        llenarFormulario(viewModel, "Voluntariado", "3", System.currentTimeMillis() - 1000)
        viewModel.guardar(correo)
        verify { Notificaciones.programarRecordatorio(any(), any(), match { it.startsWith("Hoy") }, 20_000L) }
    }

    "guardar() al editar no debe volver a notificar" {
        coEvery { repo.guardar(any()) } just Runs
        coEvery { repo.obtenerPorUsuario(correo) } returns emptyList()
        val viewModel = crearViewModel()
        viewModel.editar(Colaboracion(9, correo, "Ana", "Donación", 5000, 0L, "Mensaje", false, true))
        llenarFormulario(viewModel, "Donación", "30000", System.currentTimeMillis())
        viewModel.guardar(correo)
        coVerify { repo.guardar(match { it.id == 9 }) }
        verify(exactly = 0) { Notificaciones.mostrar(any(), any(), any(), any()) }
    }

    "guardar() con el formulario inválido no llama al microservicio" {
        val viewModel = crearViewModel()
        viewModel.guardar(correo)
        coVerify(exactly = 0) { repo.guardar(any()) }
    }

    "si el microservicio no responde al guardar debe mostrar el error" {
        coEvery { repo.guardar(any()) } throws IOException("apagado")
        val viewModel = crearViewModel()
        llenarFormulario(viewModel, "Donación", "20000", System.currentTimeMillis())
        viewModel.guardar(correo)
        viewModel.error.value!! shouldContain "No se pudo conectar"
        viewModel.guardadoCorrectamente.value shouldBe false
    }

    "si falla al cargar la lista debe mostrar el error" {
        coEvery { repo.obtenerPorUsuario(correo) } throws IOException("apagado")
        val viewModel = crearViewModel()
        viewModel.cargarColaboraciones(correo)
        viewModel.error.value.shouldNotBeNull()
    }

    "eliminar() debe borrar en el microservicio y recargar la lista" {
        val colaboracion = Colaboracion(3, correo, "Ana", "Donación", 5000, 0L, "Mensaje", false, true)
        coEvery { repo.eliminar(colaboracion) } just Runs
        coEvery { repo.obtenerPorUsuario(correo) } returns emptyList()
        val viewModel = crearViewModel()
        viewModel.eliminar(colaboracion)
        coVerify { repo.eliminar(colaboracion) }
        coVerify { repo.obtenerPorUsuario(correo) }
    }

    "si falla al eliminar debe mostrar el error" {
        val colaboracion = Colaboracion(3, correo, "Ana", "Donación", 5000, 0L, "Mensaje", false, true)
        coEvery { repo.eliminar(colaboracion) } throws IOException("apagado")
        val viewModel = crearViewModel()
        viewModel.eliminar(colaboracion)
        viewModel.error.value.shouldNotBeNull()
    }
})
