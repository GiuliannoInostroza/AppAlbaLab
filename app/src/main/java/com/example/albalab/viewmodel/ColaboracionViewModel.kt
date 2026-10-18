package com.example.albalab.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.albalab.data.Notificaciones
import com.example.albalab.data.local.AppDatabase
import com.example.albalab.data.model.Colaboracion
import com.example.albalab.data.model.ColaboracionErrores
import com.example.albalab.data.model.ColaboracionUiState
import com.example.albalab.data.repository.ColaboracionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Formulario "Quiero colaborar" y CRUD de colaboraciones
class ColaboracionViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = ColaboracionRepository(
        AppDatabase.obtenerBaseDeDatos(application).colaboracionDao()
    )

    val tipos = listOf("Donación", "Voluntariado", "Alianza empresarial", "Compra de productos", "Capacitación")

    private val _formulario = MutableStateFlow(ColaboracionUiState())
    val formulario: StateFlow<ColaboracionUiState> = _formulario

    private val _colaboraciones = MutableStateFlow<List<Colaboracion>>(emptyList())
    val colaboraciones: StateFlow<List<Colaboracion>> = _colaboraciones

    private val _guardadoCorrectamente = MutableStateFlow(false)
    val guardadoCorrectamente: StateFlow<Boolean> = _guardadoCorrectamente

    private var correoCargado: String? = null

    // Escucha la tabla de Room: cada cambio actualiza la lista automáticamente
    fun cargarColaboraciones(correo: String) {
        if (correoCargado == correo) return
        correoCargado = correo
        viewModelScope.launch {
            repositorio.obtenerPorUsuario(correo).collect { lista ->
                _colaboraciones.value = lista
            }
        }
    }

    // Deja el formulario limpio; tipo y nombre pueden venir ya marcados
    fun nuevoFormulario(tipo: String = "", nombre: String = "") {
        _formulario.value = ColaboracionUiState(tipo = tipo, nombre = nombre)
        _guardadoCorrectamente.value = false
    }

    // Carga una colaboración existente en el formulario para editarla
    fun editar(colaboracion: Colaboracion) {
        _formulario.value = ColaboracionUiState(
            id = colaboracion.id,
            nombre = colaboracion.nombre,
            tipo = colaboracion.tipo,
            personas = colaboracion.personas.toString(),
            fecha = colaboracion.fecha,
            mensaje = colaboracion.mensaje,
            recurrente = colaboracion.recurrente,
            aceptaContacto = colaboracion.aceptaContacto
        )
        _guardadoCorrectamente.value = false
    }

    fun onNombreChange(valor: String) {
        _formulario.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onTipoChange(valor: String) {
        _formulario.update { it.copy(tipo = valor, errores = it.errores.copy(tipo = null)) }
    }

    // Solo acepta dígitos, igual que el formulario del profe
    fun onPersonasChange(valor: String) {
        if (valor.all { it.isDigit() }) {
            _formulario.update { it.copy(personas = valor, errores = it.errores.copy(personas = null)) }
        }
    }

    fun onFechaChange(valor: Long?) {
        _formulario.update { it.copy(fecha = valor, errores = it.errores.copy(fecha = null)) }
    }

    fun onMensajeChange(valor: String) {
        _formulario.update { it.copy(mensaje = valor, errores = it.errores.copy(mensaje = null)) }
    }

    fun onRecurrenteChange(valor: Boolean) {
        _formulario.update { it.copy(recurrente = valor) }
    }

    fun onAceptaContactoChange(valor: Boolean) {
        _formulario.update { it.copy(aceptaContacto = valor, errores = it.errores.copy(aceptaContacto = null)) }
    }

    // Reglas del formulario. La pantalla no valida nada: solo muestra estos errores
    fun validarFormulario(): Boolean {
        val datos = _formulario.value
        val personas = datos.personas.toIntOrNull()
        val unDia = 24 * 60 * 60 * 1000L

        var errorFecha: String? = null
        if (datos.fecha == null) {
            errorFecha = "Selecciona una fecha"
        } else if (datos.fecha < System.currentTimeMillis() - unDia) {
            errorFecha = "La fecha no puede ser anterior a hoy"
        }

        val errores = ColaboracionErrores(
            nombre = if (datos.nombre.trim().length < 3) "Escribe al menos 3 letras" else null,
            tipo = if (datos.tipo.isBlank()) "Elige cómo quieres colaborar" else null,
            personas = if (personas == null || personas < 1 || personas > 500) "Ingresa un número entre 1 y 500" else null,
            fecha = errorFecha,
            mensaje = if (datos.mensaje.trim().length < 10) "Cuéntanos un poco más (mínimo 10 caracteres)" else null,
            aceptaContacto = if (!datos.aceptaContacto) "Debes aceptar que te contactemos" else null
        )
        _formulario.update { it.copy(errores = errores) }
        return errores == ColaboracionErrores()
    }

    fun guardar(correoUsuario: String) {
        if (!validarFormulario()) return
        val datos = _formulario.value
        val esNueva = datos.id == 0
        viewModelScope.launch {
            repositorio.guardar(
                Colaboracion(
                    id = datos.id,
                    usuarioCorreo = correoUsuario,
                    nombre = datos.nombre.trim(),
                    tipo = datos.tipo,
                    personas = datos.personas.toInt(),
                    fecha = datos.fecha ?: System.currentTimeMillis(),
                    mensaje = datos.mensaje.trim(),
                    recurrente = datos.recurrente,
                    aceptaContacto = datos.aceptaContacto
                )
            )
            if (esNueva) {
                // Notificación local: recurso nativo extra (bonus track)
                Notificaciones.mostrar(
                    getApplication(),
                    "Colaboración registrada",
                    "Gracias. El equipo de Alba Lab te contactará pronto."
                )
            }
            _formulario.value = ColaboracionUiState(nombre = datos.nombre)
            _guardadoCorrectamente.value = true
        }
    }

    fun ocultarMensaje() {
        _guardadoCorrectamente.value = false
    }

    fun eliminar(colaboracion: Colaboracion) {
        viewModelScope.launch {
            repositorio.eliminar(colaboracion)
        }
    }
}
