package com.example.albalab.data.repository

import com.example.albalab.data.model.Indicador
import com.example.albalab.data.remote.ApiService
import com.example.albalab.data.remote.RetrofitInstance

// Obtiene los indicadores desde la API externa (guía 14)
open class IndicadorRepository(private val api: ApiService = RetrofitInstance.api) {

    open suspend fun getIndicadores(): List<Indicador> {
        val respuesta = api.getIndicadores()
        return listOf(respuesta.uf, respuesta.dolar, respuesta.utm)
    }
}
