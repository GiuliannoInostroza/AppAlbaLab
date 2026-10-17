package com.example.albalab.data.repository

import com.example.albalab.data.local.UsuarioDao
import com.example.albalab.data.model.Usuario
import java.security.MessageDigest

// Obtiene y guarda usuarios. Aquí se cifra la clave antes de guardarla
class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    // Devuelve false si el correo ya está registrado
    suspend fun registrar(nombre: String, correo: String, clave: String): Boolean {
        if (usuarioDao.buscarPorCorreo(correo) != null) {
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

    // Devuelve el usuario si el correo existe y el hash de la clave coincide
    suspend fun validarCredenciales(correo: String, clave: String): Usuario? {
        val usuario = usuarioDao.buscarPorCorreo(correo) ?: return null
        return if (usuario.claveCifrada == cifrarClave(clave)) usuario else null
    }

    suspend fun buscar(correo: String): Usuario? {
        return usuarioDao.buscarPorCorreo(correo)
    }

    suspend fun actualizarFoto(usuario: Usuario, fotoUri: String) {
        usuarioDao.actualizar(usuario.copy(fotoUri = fotoUri))
    }

    // SHA-256: convierte la clave en un código de 64 caracteres que no se puede revertir
    fun cifrarClave(clave: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(clave.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
