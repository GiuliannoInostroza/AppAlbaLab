package com.example.albalab.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.albalab.R

// Notificaciones locales (bonus track)
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

    fun mostrar(context: Context, titulo: String, texto: String) {
        // Desde Android 13 hay que tener el permiso concedido; si no, no se muestra
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(1, notificacion)
    }
}
