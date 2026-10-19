package com.example.albalab.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.albalab.MainActivity
import com.example.albalab.R
import java.util.concurrent.TimeUnit

// Notificaciones locales (bonus track): una al instante y un recordatorio programado
object Notificaciones {

    private const val CANAL_ID = "colaboraciones"

    // Android 8+ exige un canal para mostrar notificaciones
    fun crearCanal(context: Context) {
        val canal = NotificationChannel(
            CANAL_ID,
            "Colaboraciones",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        canal.description = "Avisos cuando registras una colaboración"
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(canal)
    }

    fun mostrar(context: Context, titulo: String, texto: String, id: Int = 1) {
        // Desde Android 13 hay que tener el permiso concedido; si no, no se muestra
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        // Al tocar la notificación se abre la app
        val abrirApp = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))  // texto largo completo
            .setContentIntent(abrirApp)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(id, notificacion)
    }

    // Programa un recordatorio con WorkManager: llega aunque el usuario esté en otra app o la cierre
    fun programarRecordatorio(context: Context, titulo: String, texto: String, retrasoMillis: Long) {
        val trabajo = OneTimeWorkRequestBuilder<RecordatorioWorker>()
            .setInitialDelay(retrasoMillis, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("titulo" to titulo, "texto" to texto))
            .build()
        WorkManager.getInstance(context).enqueue(trabajo)
    }
}
