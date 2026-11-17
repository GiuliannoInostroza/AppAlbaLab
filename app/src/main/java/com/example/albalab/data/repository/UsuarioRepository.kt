package com.example.albalab.data.repository

import com.example.albalab.data.local.UsuarioDao
import com.example.albalab.data.model.Credenciales
import com.example.albalab.data.model.Usuario
import com.example.albalab.data.remote.MicroserviciosService
import com.example.albalab.data.remote.RetrofitMicroservicios
import java.security.MessageDigest

// La cuenta vive en el auth-service; Room guarda una copia local (nombre, foto y hash de la clave)
open class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val api: MicroserviciosService = RetrofitMicroservicios.api
) {

    // Devuelve false si el correo ya está registrado (en el servidor o en el teléfono)
    open suspend fun registrar(nombre: String, correo: String, clave: String): Boolean {
        if (usuarioDao.buscarPorCorreo(correo) != null) {
            return false
        }
        val respuesta = api.registrar(Credenciales(correo, clave))
        if (respuesta.message != "Usuario creado exitosamente!") {
            return false
        }
        usuarioDao.insertar(
            Usuario(
                nombre = nombre,
                correo = correo,
                claveCifrada = cifrarClave(clave)
            )
        )
        return true
    }

    // El auth-service valida la clave y entrega el token JWT.
    // Si el usuario no está en el teléfono, se crea su copia local
    open suspend fun iniciarSesion(correo: String, clave: String): Usuario? {
        val respuesta = api.login(Credenciales(correo, clave))
        if (respuesta.status != "ok") {
            return null
        }
        RetrofitMicroservicios.token = respuesta.token
        val local = usuarioDao.buscarPorCorreo(correo)
        if (local != null) {
            return local
        }
        usuarioDao.insertar(
            Usuario(
                nombre = correo.substringBefore("@"),
                correo = correo,
                claveCifrada = cifrarClave(clave)
            )
        )
        return usuarioDao.buscarPorCorreo(correo)
    }

    open suspend fun buscar(correo: String): Usuario? {
        return usuarioDao.buscarPorCorreo(correo)
    }

    open suspend fun actualizarFoto(usuario: Usuario, fotoUri: String) {
        usuarioDao.actualizar(usuario.copy(fotoUri = fotoUri))
    }

    // SHA-256: convierte la clave en un código de 64 caracteres que no se puede revertir
    fun cifrarClave(clave: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(clave.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
