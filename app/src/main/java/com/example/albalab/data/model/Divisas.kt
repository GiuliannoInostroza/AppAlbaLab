package com.example.albalab.data.model

import com.google.gson.annotations.SerializedName

// Respuesta de la API de respaldo https://open.er-api.com/v6/latest/USD.
// "rates" dice cuánto vale 1 dólar en cada moneda (por ejemplo "CLP" = 995.9)
data class Divisas(
    val result: String,
    @SerializedName("time_last_update_unix") val fechaUnix: Long,
    val rates: Map<String, Double>
)
