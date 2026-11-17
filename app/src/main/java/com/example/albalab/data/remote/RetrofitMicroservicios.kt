package com.example.albalab.data.remote

import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Cliente Retrofit de nuestros microservicios.
// 10.0.2.2 es la dirección con que el emulador llega al computador (localhost del PC)
object RetrofitMicroservicios {

    // Token JWT que entrega el login. Se envía en cada llamada como "Bearer <token>"
    var token: String = ""

    val api: MicroserviciosService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:9090/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MicroserviciosService::class.java)
    }

    fun bearer(): String {
        return "Bearer $token"
    }

    // Mensaje para el usuario según el error: 401 = el token venció (dura 1 hora)
    fun mensajeDeError(e: Exception): String {
        return if (e is HttpException && e.code() == 401) {
            "Tu sesión expiró. Cierra sesión y vuelve a entrar."
        } else {
            "No se pudo conectar con el servidor. Revisa que los microservicios estén encendidos."
        }
    }
}
