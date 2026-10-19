package com.example.albalab.data

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

// Tarea que Android ejecuta en el momento programado, aunque la app esté cerrada.
// Lo único que hace es mostrar la notificación con el título y texto que recibió
class RecordatorioWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val titulo = inputData.getString("titulo") ?: return Result.failure()
        val texto = inputData.getString("texto") ?: ""
        Notificaciones.mostrar(applicationContext, titulo, texto, id = 2)
        return Result.success()
    }
}
