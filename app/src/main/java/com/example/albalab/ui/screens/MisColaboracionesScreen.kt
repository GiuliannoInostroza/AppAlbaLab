package com.example.albalab.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.example.albalab.data.model.Colaboracion
import com.example.albalab.navigation.Screen
import com.example.albalab.ui.components.BarraSuperior
import com.example.albalab.ui.utils.formatearFecha
import com.example.albalab.viewmodel.ColaboracionViewModel
import com.example.albalab.viewmodel.MainViewModel
import com.example.albalab.viewmodel.UsuarioViewModel

// Lista de colaboraciones guardadas en Room: leer, editar y eliminar (CRUD)
@Composable
fun MisColaboracionesScreen(
    mainViewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel,
    colaboracionViewModel: ColaboracionViewModel
) {
    val usuario by usuarioViewModel.usuarioActual.collectAsState()
    val lista by colaboracionViewModel.colaboraciones.collectAsState()
    var aEliminar by remember { mutableStateOf<Colaboracion?>(null) }

    LaunchedEffect(usuario) {
        usuario?.let { colaboracionViewModel.cargarColaboraciones(it.correo) }
    }

    Scaffold(
        topBar = { BarraSuperior(titulo = "Mis registros", onVolver = { mainViewModel.navigateBack() }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 640.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (lista.isEmpty()) {
                    Icon(
                        imageVector = Icons.Filled.Inbox,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text("Aún no tienes registros", style = MaterialTheme.typography.titleMedium)
                    Button(onClick = {
                        colaboracionViewModel.nuevoFormulario(nombre = usuario?.nombre ?: "")
                        mainViewModel.navigateTo(Screen.Colaborar, popUpTo = Screen.Inicio)
                    }) {
                        Text("Registrar una colaboración")
                    }
                } else {
                    Text(
                        text = "${lista.size} registro(s)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    lista.forEach { colaboracion ->
                        TarjetaColaboracion(
                            colaboracion = colaboracion,
                            detalle = colaboracionViewModel.describirCantidad(colaboracion.tipo, colaboracion.cantidad),
                            onEditar = {
                                colaboracionViewModel.editar(colaboracion)
                                mainViewModel.navigateTo(Screen.Colaborar, popUpTo = Screen.Inicio)
                            },
                            onEliminar = { aEliminar = colaboracion }
                        )
                    }
                }
            }
        }
    }

    // Confirmación antes de borrar
    aEliminar?.let { colaboracion ->
        AlertDialog(
            onDismissRequest = { aEliminar = null },
            title = { Text("Eliminar registro") },
            text = { Text("¿Seguro que quieres eliminar la colaboración de tipo ${colaboracion.tipo}?") },
            confirmButton = {
                TextButton(onClick = {
                    colaboracionViewModel.eliminar(colaboracion)
                    aEliminar = null
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { aEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun TarjetaColaboracion(
    colaboracion: Colaboracion,
    detalle: String,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = colaboracion.tipo,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEditar) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
            Text(text = colaboracion.nombre, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "Contacto: ${formatearFecha(colaboracion.fecha)} - $detalle",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (colaboracion.recurrente) {
                Text(
                    text = "Colaboración mensual",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = colaboracion.mensaje,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
