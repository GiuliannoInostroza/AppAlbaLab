package com.example.albalab.data.repository

import com.example.albalab.data.model.Indicador
import com.example.albalab.data.remote.ApiRespaldoService
import com.example.albalab.data.remote.ApiService
import com.example.albalab.data.remote.RetrofitInstance
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Obtiene los indicadores desde la API externa (guía 14)
open class IndicadorRepository(
    private val api: ApiService = RetrofitInstance.api,
    private val apiRespaldo: ApiRespaldoService = RetrofitInstance.apiRespaldo
) {

    // Fuente principal: mindicador.cl (UF, dólar y UTM)
    open suspend fun getIndicadores(): List<Indicador> {
        val respuesta = api.getIndicadores()
        return listOf(respuesta.uf, respuesta.dolar, respuesta.utm)
    }

    // Fuente temporal: open.er-api.com. Solo trae monedas, así que se calcula
    // cuántos pesos chilenos vale 1 dólar y 1 euro
    open suspend fun getIndicadoresRespaldo(): List<Indicador> {
        val respuesta = apiRespaldo.getDivisas()
        val pesosPorDolar = respuesta.rates.getValue("CLP")
        val eurosPorDolar = respuesta.rates.getValue("EUR")
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        formato.timeZone = TimeZone.getTimeZone("UTC")
        val fecha = formato.format(Date(respuesta.fechaUnix * 1000))
        return listOf(
            Indicador("dolar", "Dólar", fecha, pesosPorDolar),
            Indicador("euro", "Euro", fecha, pesosPorDolar / eurosPorDolar)
        )
    }
}
