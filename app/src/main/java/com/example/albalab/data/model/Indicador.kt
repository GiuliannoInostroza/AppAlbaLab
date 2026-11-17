package com.example.albalab.data.model

// Un indicador económico tal como lo entrega la API externa mindicador.cl
data class Indicador(
    val codigo: String,
    val nombre: String,
    val fecha: String,
    val valor: Double
)

// Respuesta de https://mindicador.cl/api: trae muchos indicadores, la app usa tres
data class Indicadores(
    val uf: Indicador,
    val dolar: Indicador,
    val utm: Indicador
)
