package com.example.albalab.data.remote

import com.example.albalab.data.model.Colaboracion
import com.example.albalab.data.model.Credenciales
import com.example.albalab.data.model.Programa
import com.example.albalab.data.model.RespuestaLogin
import com.example.albalab.data.model.RespuestaRegistro
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Endpoints de nuestros microservicios. Todos pasan por el api-gateway (puerto 9090)
interface MicroserviciosService {

    // auth-service: no piden token
    @POST("auth/register")
    suspend fun registrar(@Body credenciales: Credenciales): RespuestaRegistro

    @POST("auth/login")
    suspend fun login(@Body credenciales: Credenciales): RespuestaLogin

    // contenidos-service
    @GET("programas")
    suspend fun getProgramas(@Header("Authorization") token: String): List<Programa>

    // vinculacion-service: CRUD de las solicitudes de colaboración
    @GET("solicitudes/usuario/{correo}")
    suspend fun getSolicitudes(
        @Header("Authorization") token: String,
        @Path("correo") correo: String
    ): List<Colaboracion>

    @POST("solicitudes")
    suspend fun crearSolicitud(
        @Header("Authorization") token: String,
        @Body colaboracion: Colaboracion
    ): Colaboracion

    @PUT("solicitudes/{id}")
    suspend fun actualizarSolicitud(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body colaboracion: Colaboracion
    ): Colaboracion

    @DELETE("solicitudes/{id}")
    suspend fun eliminarSolicitud(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    )
}
