package com.example.albalab.data.repository

import com.example.albalab.data.model.Programa
import com.example.albalab.data.remote.MicroserviciosService
import com.example.albalab.data.remote.RetrofitMicroservicios

// Obtiene los programas desde el contenidos-service
open class ProgramaRepository(
    private val api: MicroserviciosService = RetrofitMicroservicios.api
) {

    open suspend fun obtenerProgramas(): List<Programa> {
        return api.getProgramas(RetrofitMicroservicios.bearer())
    }
}
