package com.example.albalab.data.repository

import com.example.albalab.data.local.ColaboracionDao
import com.example.albalab.data.model.Colaboracion
import kotlinx.coroutines.flow.Flow

// Obtiene y guarda las colaboraciones usando el DAO de Room
class ColaboracionRepository(private val colaboracionDao: ColaboracionDao) {

    fun obtenerPorUsuario(correo: String): Flow<List<Colaboracion>> {
        return colaboracionDao.obtenerPorUsuario(correo)
    }

    // Si el id es 0 es nueva (insertar); si no, ya existe (actualizar)
    suspend fun guardar(colaboracion: Colaboracion) {
        if (colaboracion.id == 0) {
            colaboracionDao.insertar(colaboracion)
        } else {
            colaboracionDao.actualizar(colaboracion)
        }
    }

    suspend fun eliminar(colaboracion: Colaboracion) {
        colaboracionDao.eliminar(colaboracion)
    }
}
