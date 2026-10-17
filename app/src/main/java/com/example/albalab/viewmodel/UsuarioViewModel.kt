package com.example.albalab.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.albalab.data.local.AppDatabase
import com.example.albalab.data.local.SesionDataStore
import com.example.albalab.data.model.LoginErrores
import com.example.albalab.data.model.LoginUiState
import com.example.albalab.data.model.RegistroErrores
import com.example.albalab.data.model.RegistroUiState
import com.example.albalab.data.model.Usuario
import com.example.albalab.data.repository.UsuarioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Lógica de login, registro, sesión y foto de perfil
class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = UsuarioRepository(
        AppDatabase.obtenerBaseDeDatos(application).usuarioDao()
    )
    private val sesion = SesionDataStore(application)

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _registro = MutableStateFlow(RegistroUiState())
    val registro: StateFlow<RegistroUiState> = _registro

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando

    init {
        cargarSesion()
    }

    // Al abrir la app revisa si había una sesión guardada en DataStore
    private fun cargarSesion() {
        viewModelScope.launch {
            delay(600)
            val correo = sesion.correoSesion.first()
            if (correo != null) {
                _usuarioActual.value = repositorio.buscar(correo)
            }
            _cargando.value = false
        }
    }

    // ---------- Login ----------
    fun onLoginCorreoChange(valor: String) {
        _login.update { it.copy(correo = valor, errores = it.errores.copy(correo = null, general = null)) }
    }

    fun onLoginClaveChange(valor: String) {
        _login.update { it.copy(clave = valor, errores = it.errores.copy(clave = null, general = null)) }
    }

    fun validarLogin(): Boolean {
        val estado = _login.value
        val errores = LoginErrores(
            correo = if (!correoValido(estado.correo)) "Ingresa un correo válido" else null,
            clave = if (estado.clave.isBlank()) "Ingresa tu contraseña" else null
        )
        _login.update { it.copy(errores = errores) }
        return errores.correo == null && errores.clave == null
    }

    fun iniciarSesion(onExito: () -> Unit) {
        if (!validarLogin()) return
        viewModelScope.launch {
            val usuario = repositorio.validarCredenciales(_login.value.correo.trim(), _login.value.clave)
            if (usuario == null) {
                _login.update { it.copy(errores = it.errores.copy(general = "Correo o contraseña incorrectos")) }
            } else {
                sesion.guardarSesion(usuario.correo)
                _usuarioActual.value = usuario
                _login.value = LoginUiState()
                onExito()
            }
        }
    }

    // ---------- Registro ----------
    fun onNombreChange(valor: String) {
        _registro.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onCorreoChange(valor: String) {
        _registro.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onClaveChange(valor: String) {
        _registro.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun onConfirmarClaveChange(valor: String) {
        _registro.update { it.copy(confirmarClave = valor, errores = it.errores.copy(confirmarClave = null)) }
    }

    fun onAceptaTerminosChange(valor: Boolean) {
        _registro.update { it.copy(aceptaTerminos = valor, errores = it.errores.copy(aceptaTerminos = null)) }
    }

    // Validación de cada campo: si hay error se guarda el mensaje, si no queda en null
    fun validarRegistro(): Boolean {
        val estado = _registro.value
        val errores = RegistroErrores(
            nombre = if (estado.nombre.trim().length < 3) "El nombre debe tener al menos 3 letras" else null,
            correo = if (!correoValido(estado.correo)) "Ingresa un correo válido" else null,
            clave = if (estado.clave.length < 6 || !estado.clave.any { it.isDigit() }) "Mínimo 6 caracteres y al menos un número" else null,
            confirmarClave = if (estado.confirmarClave != estado.clave) "Las contraseñas no coinciden" else null,
            aceptaTerminos = if (!estado.aceptaTerminos) "Debes aceptar los términos" else null
        )
        _registro.update { it.copy(errores = errores) }
        return errores == RegistroErrores()
    }

    fun registrar(onExito: () -> Unit) {
        if (!validarRegistro()) return
        val estado = _registro.value
        viewModelScope.launch {
            val creado = repositorio.registrar(estado.nombre.trim(), estado.correo.trim(), estado.clave)
            if (!creado) {
                _registro.update { it.copy(errores = it.errores.copy(correo = "Ya existe una cuenta con este correo")) }
            } else {
                sesion.guardarSesion(estado.correo.trim())
                _usuarioActual.value = repositorio.buscar(estado.correo.trim())
                _registro.value = RegistroUiState()
                onExito()
            }
        }
    }

    // ---------- Verificación con contraseña ----------
    fun verificarClave(clave: String): Boolean {
        val usuario = _usuarioActual.value ?: return false
        return repositorio.cifrarClave(clave) == usuario.claveCifrada
    }

    // ---------- Perfil ----------
    fun actualizarFoto(uri: Uri) {
        val usuario = _usuarioActual.value ?: return
        viewModelScope.launch {
            repositorio.actualizarFoto(usuario, uri.toString())
            _usuarioActual.value = usuario.copy(fotoUri = uri.toString())
        }
    }

    fun cerrarSesion(onListo: () -> Unit) {
        viewModelScope.launch {
            sesion.cerrarSesion()
            _usuarioActual.value = null
            onListo()
        }
    }

    private fun correoValido(correo: String): Boolean {
        val texto = correo.trim()
        return texto.contains("@") && texto.substringAfter("@").contains(".")
    }
}
