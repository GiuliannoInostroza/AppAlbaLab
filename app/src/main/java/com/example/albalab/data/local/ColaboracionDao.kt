package com.example.albalab.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.albalab.data.model.Colaboracion
import kotlinx.coroutines.flow.Flow

// CRUD de colaboraciones: insertar, leer, actualizar y eliminar
@Dao
interface ColaboracionDao {

    @Insert
    suspend fun insertar(colaboracion: Colaboracion)

    @Update
    suspend fun actualizar(colaboracion: Colaboracion)

    @Delete
    suspend fun eliminar(colaboracion: Colaboracion)

    // Flow: cada vez que cambia la tabla, la lista se emite de nuevo y la pantalla se actualiza sola
    @Query("SELECT * FROM colaboraciones WHERE usuarioCorreo = :correo ORDER BY fecha ASC")
    fun obtenerPorUsuario(correo: String): Flow<List<Colaboracion>>
}
