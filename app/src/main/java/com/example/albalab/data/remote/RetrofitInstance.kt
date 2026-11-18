package com.example.albalab.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Cliente Retrofit de la API externa (guía 14). No usa token: es pública
object RetrofitInstance {

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://mindicador.cl")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // Respaldo: open.er-api.com (dólar y euro, sin UF ni UTM)
    val apiRespaldo: ApiRespaldoService by lazy {
        Retrofit.Builder()
            .baseUrl("https://open.er-api.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRespaldoService::class.java)
    }
}
