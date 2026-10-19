package com.example.albalab.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.albalab.navigation.Screen
import com.example.albalab.ui.components.BarraSuperior
import com.example.albalab.ui.components.CampoTexto
import com.example.albalab.viewmodel.MainViewModel
import com.example.albalab.viewmodel.UsuarioViewModel

// Formulario 2: registro de usuario (guía 11). La clave se guarda cifrada en Room
@Composable
fun RegistroScreen(
    mainViewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val estado by usuarioViewModel.registro.collectAsState()

    Scaffold(
        topBar = { BarraSuperior(titulo = "Crear cuenta", onVolver = { mainViewModel.navigateBack() }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .imePadding() // deja espacio para el teclado y así no tapa los campos ni el botón
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 520.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CampoTexto(
                    valor = estado.nombre,
                    onValorChange = usuarioViewModel::onNombreChange,
                    etiqueta = "Nombre",
                    icono = Icons.Filled.Person,
                    error = estado.errores.nombre
                )
                CampoTexto(
                    valor = estado.correo,
                    onValorChange = usuarioViewModel::onCorreoChange,
                    etiqueta = "Correo electrónico",
                    icono = Icons.Filled.Email,
                    error = estado.errores.correo,
                    teclado = KeyboardType.Email
                )
                CampoTexto(
                    valor = estado.clave,
                    onValorChange = usuarioViewModel::onClaveChange,
                    etiqueta = "Contraseña",
                    icono = Icons.Filled.Lock,
                    error = estado.errores.clave,
                    esClave = true,
                    teclado = KeyboardType.Password
                )
                CampoTexto(
                    valor = estado.confirmarClave,
                    onValorChange = usuarioViewModel::onConfirmarClaveChange,
                    etiqueta = "Repite la contraseña",
                    icono = Icons.Filled.LockReset,
                    error = estado.errores.confirmarClave,
                    esClave = true,
                    teclado = KeyboardType.Password
                )
                // Checkbox: aceptar términos
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = estado.aceptaTerminos,
                        onCheckedChange = usuarioViewModel::onAceptaTerminosChange
                    )
                    Text("Acepto los términos y condiciones")
                }
                AnimatedVisibility(visible = estado.errores.aceptaTerminos != null) {
                    Text(
                        text = estado.errores.aceptaTerminos ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
                Button(
                    onClick = {
                        usuarioViewModel.registrar {
                            mainViewModel.navigateTo(Screen.Verificacion, popUpTo = Screen.Login, inclusive = true)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Crear cuenta")
                }
            }
        }
    }
}
