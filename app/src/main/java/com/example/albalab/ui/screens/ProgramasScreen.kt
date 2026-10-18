package com.example.albalab.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.example.albalab.data.model.Programa
import com.example.albalab.navigation.Screen
import com.example.albalab.ui.components.BarraInferior
import com.example.albalab.ui.components.BarraSuperior
import com.example.albalab.viewmodel.ColaboracionViewModel
import com.example.albalab.viewmodel.MainViewModel
import com.example.albalab.viewmodel.ProgramasViewModel
import com.example.albalab.viewmodel.UsuarioViewModel

// Catálogo de programas, productos y servicios con filtro dinámico (bonus track)
@Composable
fun ProgramasScreen(
    mainViewModel: MainViewModel,
    programasViewModel: ProgramasViewModel,
    colaboracionViewModel: ColaboracionViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val busqueda by programasViewModel.busqueda.collectAsState()
    val programas by programasViewModel.programasFiltrados.collectAsState()
    val usuario by usuarioViewModel.usuarioActual.collectAsState()

    Scaffold(
        topBar = { BarraSuperior(titulo = "Programas y productos") },
        bottomBar = {
            BarraInferior(rutaActual = Screen.Programas.route) { destino ->
                mainViewModel.navigateTo(destino, popUpTo = Screen.Inicio)
            }
        }
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cada letra que se escribe llama al ViewModel, que filtra la lista
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = programasViewModel::onBusquedaChange,
                    label = { Text("Buscar programa o producto") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    trailingIcon = {
                        if (busqueda.isNotEmpty()) {
                            IconButton(onClick = { programasViewModel.onBusquedaChange("") }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Borrar búsqueda")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "${programas.size} resultado(s)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (programas.isEmpty()) {
                    Text(
                        text = "No hay resultados para \"$busqueda\"",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                programas.forEach { programa ->
                    TarjetaPrograma(
                        programa = programa,
                        onColaborar = {
                            colaboracionViewModel.nuevoFormulario(
                                tipo = programa.tipoColaboracion,
                                nombre = usuario?.nombre ?: ""
                            )
                            mainViewModel.navigateTo(Screen.Colaborar, popUpTo = Screen.Inicio)
                        }
                    )
                }
            }
        }
    }
}

// Tarjeta que se expande al tocarla para mostrar el destino del aporte
@Composable
private fun TarjetaPrograma(programa: Programa, onColaborar: () -> Unit) {
    var expandida by rememberSaveable(programa.id) { mutableStateOf(false) }
    val rotacion by animateFloatAsState(
        targetValue = if (expandida) 180f else 0f,
        animationSpec = tween(200),
        label = "Flecha"
    )
    val icono = when (programa.categoria) {
        "Programa" -> Icons.Filled.School
        "Producto" -> Icons.Filled.Storefront
        else -> Icons.Filled.Handshake
    }

    Card(
        onClick = { expandida = !expandida },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = programa.nombre, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = programa.categoria,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = if (expandida) "Contraer" else "Expandir",
                    modifier = Modifier.rotate(rotacion)
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = programa.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            AnimatedVisibility(
                visible = expandida,
                enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Destino del aporte", style = MaterialTheme.typography.titleSmall)
                    Text(text = programa.destinoAporte, style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = onColaborar, modifier = Modifier.fillMaxWidth()) {
                        Text("Quiero colaborar")
                    }
                }
            }
        }
    }
}
