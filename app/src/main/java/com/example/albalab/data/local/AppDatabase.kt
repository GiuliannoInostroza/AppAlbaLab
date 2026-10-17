package com.example.albalab.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.albalab.data.model.Colaboracion
import com.example.albalab.data.model.Usuario

// Base de datos SQLite de la app, creada con Room
@Database(entities = [Usuario::class, Colaboracion::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun colaboracionDao(): ColaboracionDao

    companion object {
        // Una sola instancia para toda la app
        @Volatile
        private var INSTANCIA: AppDatabase? = null

        fun obtenerBaseDeDatos(context: Context): AppDatabase {
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "alba_lab.db"
                ).build()
                INSTANCIA = instancia
                instancia
            }
        }
    }
}
