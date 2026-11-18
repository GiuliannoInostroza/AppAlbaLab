package com.example.albalab.repository

import com.example.albalab.data.local.UsuarioDao
import com.example.albalab.data.model.Credenciales
import com.example.albalab.data.model.RespuestaLogin
import com.example.albalab.data.model.RespuestaRegistro
import com.example.albalab.data.model.Usuario
import com.example.albalab.data.remote.MicroserviciosService
import com.example.albalab.data.remote.RetrofitMicroservicios
import com.example.albalab.data.repository.UsuarioRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import kotlinx.coroutines.test.runTest

class UsuarioRepositoryTest : StringSpec({

    val correo = "ana@correo.cl"
    val clave = "Clave123"
    val usuario = Usuario(id = 1, nombre = "Ana", correo = correo, claveCifrada = "hash")

    "cifrarClave() debe devolver el hash SHA-256 de la clave" {
        val repo = UsuarioRepository(mockk(), mockk())
        repo.cifrarClave("abc") shouldBe "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"
    }

    "registrar() debe devolver false si el correo ya está en el teléfono" {
        val dao = mockk<UsuarioDao>()
        val api = mockk<MicroserviciosService>()
        coEvery { dao.buscarPorCorreo(correo) } returns usuario
        val repo = UsuarioRepository(dao, api)
        runTest {
            repo.registrar("Ana", correo, clave) shouldBe false
            coVerify(exactly = 0) { api.registrar(any()) }
        }
    }

    "registrar() debe devolver false si el auth-service dice que el usuario ya existe" {
        val dao = mockk<UsuarioDao>()
        val api = mockk<MicroserviciosService>()
        coEvery { dao.buscarPorCorreo(correo) } returns null
        coEvery { api.registrar(Credenciales(correo, clave)) } returns RespuestaRegistro("Usuario ya existe!")
        val repo = UsuarioRepository(dao, api)
        runTest {
            repo.registrar("Ana", correo, clave) shouldBe false
            coVerify(exactly = 0) { dao.insertar(any()) }
        }
    }

    "registrar() debe crear la cuenta en el servidor y guardar la copia local con la clave cifrada" {
        val dao = mockk<UsuarioDao>()
        val api = mockk<MicroserviciosService>()
        coEvery { dao.buscarPorCorreo(correo) } returns null
        coEvery { dao.insertar(any()) } just Runs
        coEvery { api.registrar(Credenciales(correo, clave)) } returns RespuestaRegistro("Usuario creado exitosamente!")
        val repo = UsuarioRepository(dao, api)
        runTest {
            repo.registrar("Ana", correo, clave) shouldBe true
            coVerify { dao.insertar(Usuario(nombre = "Ana", correo = correo, claveCifrada = repo.cifrarClave(clave))) }
        }
    }

    "iniciarSesion() debe devolver null si la clave es incorrecta" {
        val api = mockk<MicroserviciosService>()
        coEvery { api.login(any()) } returns RespuestaLogin("error", "")
        val repo = UsuarioRepository(mockk(), api)
        runTest {
            repo.iniciarSesion(correo, "mala").shouldBeNull()
        }
    }

    "iniciarSesion() debe guardar el token y devolver el usuario local" {
        val dao = mockk<UsuarioDao>()
        val api = mockk<MicroserviciosService>()
        coEvery { api.login(Credenciales(correo, clave)) } returns RespuestaLogin("ok", "token123")
        coEvery { dao.buscarPorCorreo(correo) } returns usuario
        val repo = UsuarioRepository(dao, api)
        runTest {
            repo.iniciarSesion(correo, clave) shouldBe usuario
            RetrofitMicroservicios.token shouldBe "token123"
        }
    }

    "iniciarSesion() debe crear la copia local si el usuario no estaba en el teléfono" {
        val dao = mockk<UsuarioDao>()
        val api = mockk<MicroserviciosService>()
        coEvery { api.login(any()) } returns RespuestaLogin("ok", "token123")
        // La primera búsqueda no lo encuentra; después de insertarlo, sí
        coEvery { dao.buscarPorCorreo(correo) } returnsMany listOf(null, usuario)
        coEvery { dao.insertar(any()) } just Runs
        val repo = UsuarioRepository(dao, api)
        runTest {
            repo.iniciarSesion(correo, clave) shouldBe usuario
            coVerify { dao.insertar(Usuario(nombre = "ana", correo = correo, claveCifrada = repo.cifrarClave(clave))) }
        }
    }

    "buscar() y actualizarFoto() deben usar el DAO de Room" {
        val dao = mockk<UsuarioDao>()
        coEvery { dao.buscarPorCorreo(correo) } returns usuario
        coEvery { dao.actualizar(any()) } just Runs
        val repo = UsuarioRepository(dao, mockk())
        runTest {
            repo.buscar(correo) shouldBe usuario
            repo.actualizarFoto(usuario, "content://foto")
            coVerify { dao.actualizar(usuario.copy(fotoUri = "content://foto")) }
        }
    }
})
