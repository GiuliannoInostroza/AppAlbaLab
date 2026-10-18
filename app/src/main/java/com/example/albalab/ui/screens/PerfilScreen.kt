package com.example.albalab.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.albalab.data.CamaraUriFactory
import com.example.albalab.navigation.Screen
import com.example.albalab.ui.components.BarraInferior
import com.example.albalab.ui.components.BarraSuperior
import com.example.albalab.ui.components.ImagenInteligente
import com.example.albalab.viewmodel.ColaboracionViewModel
import com.example.albalab.viewmodel.MainViewModel
import com.example.albalab.viewmodel.UsuarioViewModel

// Perfil con foto desde la galería o la cámara (guía 13): los 2 recursos nativos obligatorios
@Composable
fun PerfilScreen(
    mainViewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel,
    colaboracionViewModel: ColaboracionViewModel
) {
    val context = LocalContext.current
    val usuario by usuarioViewModel.usuarioActual.collectAsState()
    val colaboraciones by colaboracionViewModel.colaboraciones.collectAsState()

    var uriPendienteCamara by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(usuario) {
        usuario?.let { colaboracionViewModel.cargarColaboraciones(it.correo) }
    }

    // Galería: devuelve la URI de la imagen elegida (o null si se cancela)
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uriSeleccionado ->
        if (uriSeleccionado != null) {
            try {
                val copia = CamaraUriFactory.copiarDesdeGaleria(context, uriSeleccionado)
                usuarioViewModel.actualizarFoto(copia)
            } catch (e: Exception) {
                Toast.makeText(context, "No se pudo cargar la imagen", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Cámara: guarda la foto en la URI que le pasamos y avisa si salió bien
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        val uri = uriPendienteCamara
        if (exito && uri != null) {
            usuarioViewModel.actualizarFoto(uri)
        }
        uriPendienteCamara = null
    }

    fun abrirCamara() {
        try {
            val uri = CamaraUriFactory.crearUri(context)
            uriPendienteCamara = uri
            camaraLauncher.launch(uri)
        } catch (e: Exception) {
            Toast.makeText(context, "Error al abrir la cámara: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Solicita el permiso si todavía no está concedido
    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            abrirCamara()
        } else {
            Toast.makeText(context, "Debes conceder el permiso de cámara en Ajustes.", Toast.LENGTH_LONG).show()
        }
    }

    fun tomarFotografia() {
        val permisoConcedido = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (permisoConcedido) {
            abrirCamara()
        } else {
            permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = { BarraSuperior(titulo = "Mi perfil") },
        bottomBar = {
            BarraInferior(rutaActual = Screen.Perfil.route) { destino ->
                mainViewModel.navigateTo(destino, popUpTo = Screen.Inicio)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 520.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ImagenInteligente(imagenUri = usuario?.fotoUri?.let { Uri.parse(it) })
                Text(text = usuario?.nombre ?: "", style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = usuario?.correo ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { galeriaLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Collections, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Elegir desde galería")
                }
                OutlinedButton(
                    onClick = { tomarFotografia() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tomar fotografía")
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Mis registros", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "${colaboraciones.size} colaboración(es)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(onClick = { mainViewModel.navigateTo(Screen.MisColaboraciones) }) {
                            Text("Ver")
                        }
                    }
                }
                OutlinedButton(
                    onClick = {
                        usuarioViewModel.cerrarSesion {
                            mainViewModel.navigateTo(Screen.Login, popUpTo = Screen.Inicio, inclusive = true)
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar sesión")
                }
            }
        }
    }
}
