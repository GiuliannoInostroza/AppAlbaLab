package com.example.albalab.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.albalab.navigation.Screen
import com.example.albalab.ui.components.BarraInferior
import com.example.albalab.ui.components.BarraSuperior
import com.example.albalab.ui.components.CampoTexto
import com.example.albalab.ui.components.MensajeExito
import com.example.albalab.ui.utils.formatearFecha
import com.example.albalab.viewmodel.ColaboracionViewModel
import com.example.albalab.viewmodel.MainViewModel
import com.example.albalab.viewmodel.UsuarioViewModel
import kotlinx.coroutines.delay

// Formulario 3 "Quiero colaborar": String, Int, fecha, lista, switch, checkbox y botón
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColaborarScreen(
    mainViewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel,
    colaboracionViewModel: ColaboracionViewModel
) {
    val context = LocalContext.current
    val formulario by colaboracionViewModel.formulario.collectAsState()
    val guardado by colaboracionViewModel.guardadoCorrectamente.collectAsState()
    val usuario by usuarioViewModel.usuarioActual.collectAsState()
    val errores = formulario.errores
    val editando = formulario.id != 0

    var mostrarDatePicker by rememberSaveable { mutableStateOf(false) }
    var tipoExpandido by remember { mutableStateOf(false) }

    // Desde Android 13 hay que pedir permiso para mostrar notificaciones
    val permisoNotificaciones = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // El mensaje de éxito se oculta solo después de 2,5 segundos (guía 12)
    LaunchedEffect(guardado) {
        if (guardado) {
            delay(2500)
            colaboracionViewModel.ocultarMensaje()
        }
    }

    // El botón cambia de color con animación cuando se acepta ser contactado
    val colorBoton by animateColorAsState(
        targetValue = if (formulario.aceptaContacto) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
        animationSpec = tween(200),
        label = "Color del botón"
    )

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = if (editando) "Editar colaboración" else "Quiero colaborar",
                acciones = {
                    IconButton(onClick = { mainViewModel.navigateTo(Screen.MisColaboraciones) }) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Mis registros")
                    }
                }
            )
        },
        bottomBar = {
            BarraInferior(rutaActual = Screen.Colaborar.route) { destino ->
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
                modifier = Modifier.widthIn(max = 560.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "Cuéntanos cómo quieres apoyar", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "El equipo de vinculación revisará tu registro.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                // String
                CampoTexto(
                    valor = formulario.nombre,
                    onValorChange = colaboracionViewModel::onNombreChange,
                    etiqueta = "Nombre o empresa",
                    icono = Icons.Filled.Person,
                    error = errores.nombre
                )

                // Lista desplegable (igual que la categoría del formulario del profe)
                ExposedDropdownMenuBox(
                    expanded = tipoExpandido,
                    onExpandedChange = { tipoExpandido = !tipoExpandido }
                ) {
                    OutlinedTextField(
                        value = formulario.tipo,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de colaboración") },
                        leadingIcon = { Icon(Icons.Filled.VolunteerActivism, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExpandido) },
                        isError = errores.tipo != null,
                        supportingText = { errores.tipo?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = tipoExpandido,
                        onDismissRequest = { tipoExpandido = false }
                    ) {
                        colaboracionViewModel.tipos.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    colaboracionViewModel.onTipoChange(tipo)
                                    tipoExpandido = false
                                }
                            )
                        }
                    }
                }

                // Int
                CampoTexto(
                    valor = formulario.personas,
                    onValorChange = colaboracionViewModel::onPersonasChange,
                    etiqueta = "Personas que participan",
                    icono = Icons.Filled.Groups,
                    error = errores.personas,
                    teclado = KeyboardType.Number
                )

                // Date: campo de solo lectura que abre el DatePicker
                OutlinedTextField(
                    value = formulario.fecha?.let { formatearFecha(it) } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de contacto") },
                    leadingIcon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { mostrarDatePicker = true }) {
                            Icon(Icons.Filled.EditCalendar, contentDescription = "Elegir fecha")
                        }
                    },
                    isError = errores.fecha != null,
                    supportingText = { errores.fecha?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth()
                )

                CampoTexto(
                    valor = formulario.mensaje,
                    onValorChange = colaboracionViewModel::onMensajeChange,
                    etiqueta = "Mensaje",
                    icono = Icons.AutoMirrored.Filled.Message,
                    error = errores.mensaje,
                    lineas = 3
                )

                // Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Aporte recurrente")
                        Text(
                            text = "Repetir la colaboración cada mes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = formulario.recurrente,
                        onCheckedChange = colaboracionViewModel::onRecurrenteChange
                    )
                }

                // Checkbox
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = formulario.aceptaContacto,
                        onCheckedChange = colaboracionViewModel::onAceptaContactoChange
                    )
                    Text("Acepto que el equipo de Alba Lab me contacte")
                }
                AnimatedVisibility(visible = errores.aceptaContacto != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = errores.aceptaContacto ?: "", color = MaterialTheme.colorScheme.error)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { usuario?.let { colaboracionViewModel.guardar(it.correo) } },
                    colors = ButtonDefaults.buttonColors(containerColor = colorBoton),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (editando) "Guardar cambios" else "Enviar registro")
                }
                MensajeExito(visible = guardado, texto = "Registro guardado. Puedes verlo en Mis registros.")
                if (editando) {
                    TextButton(
                        onClick = { colaboracionViewModel.nuevoFormulario(nombre = usuario?.nombre ?: "") },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Cancelar edición")
                    }
                }
            }
        }
    }

    // Diálogo del calendario (mismo patrón del formulario del profe)
    if (mostrarDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = formulario.fecha)
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    colaboracionViewModel.onFechaChange(datePickerState.selectedDateMillis)
                    mostrarDatePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
