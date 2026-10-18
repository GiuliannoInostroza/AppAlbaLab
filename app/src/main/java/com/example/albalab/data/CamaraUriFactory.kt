package com.example.albalab.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

// Crea el archivo donde la cámara guarda la foto (guía 13)
object CamaraUriFactory {

    fun crearUri(context: Context): Uri {
        val cameraDir = File(context.cacheDir, "camera")
        if (!cameraDir.exists() && !cameraDir.mkdirs()) {
            throw IllegalStateException("No se pudo crear el directorio de la cámara")
        }
        val photoFile = File.createTempFile("perfil_", ".jpg", cameraDir)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
    }

    // Copia la imagen elegida en la galería a la carpeta de la app,
    // así se sigue viendo después de cerrar y abrir la app
    fun copiarDesdeGaleria(context: Context, uriGaleria: Uri): Uri {
        val destino = crearUri(context)
        context.contentResolver.openInputStream(uriGaleria)?.use { entrada ->
            context.contentResolver.openOutputStream(destino)?.use { salida ->
                entrada.copyTo(salida)
            }
        }
        return destino
    }
}
