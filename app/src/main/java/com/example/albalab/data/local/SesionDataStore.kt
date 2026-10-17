package com.example.albalab.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "sesion"
)

// Guarda el correo del usuario que inició sesión (mismo patrón que EstadoDataStore de la guía 12)
class SesionDataStore(private val context: Context) {

    companion object {
        private val CORREO_SESION = stringPreferencesKey("correo_sesion")
    }

    val correoSesion: Flow<String?> =
        context.dataStore.data.map { preferencias ->
            preferencias[CORREO_SESION]
        }

    suspend fun guardarSesion(correo: String) {
        context.dataStore.edit { preferencias ->
            preferencias[CORREO_SESION] = correo
        }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit { preferencias ->
            preferencias.remove(CORREO_SESION)
        }
    }
}
