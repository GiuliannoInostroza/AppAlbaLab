package com.example.albalab.data.remote

import com.example.albalab.data.model.Divisas
import retrofit2.http.GET

// API externa de respaldo: solo se usa si mindicador.cl no responde
interface ApiRespaldoService {

    @GET(value = "/v6/latest/USD")
    suspend fun getDivisas(): Divisas
}
