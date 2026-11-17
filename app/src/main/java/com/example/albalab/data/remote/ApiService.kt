package com.example.albalab.data.remote

import com.example.albalab.data.model.Indicadores
import retrofit2.http.GET

// API externa (guía 14): indicadores económicos del día
interface ApiService {

    @GET(value = "/api")
    suspend fun getIndicadores(): Indicadores
}
