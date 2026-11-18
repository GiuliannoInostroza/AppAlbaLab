package com.example.albalab.data.repository

import com.example.albalab.data.model.Colaboracion
import com.example.albalab.data.remote.MicroserviciosService
import com.example.albalab.data.remote.RetrofitMicroservicios

// CRUD de colaboraciones contra el vinculacion-service
open class ColaboracionRepository(
    private val api: MicroserviciosService = RetrofitMicroservicios.api
) {

    open suspend fun obtenerPorUsuario(correo: String): List<Colaboracion> {
        return api.getSolicitudes(RetrofitMicroservicios.bearer(), correo)
    }

    // Si el id es 0 es nueva (POST); si no, ya existe (PUT)
    open suspend fun guardar(colaboracion: Colaboracion) {
        if (colaboracion.id == 0) {
            api.crearSolicitud(RetrofitMicroservicios.bearer(), colaboracion)
        } else {
            api.actualizarSolicitud(RetrofitMicroservicios.bearer(), colaboracion.id, colaboracion)
        }
    }

    open suspend fun eliminar(colaboracion: Colaboracion) {
        api.eliminarSolicitud(RetrofitMicroservicios.bearer(), colaboracion.id)
    }
}
