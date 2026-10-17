package com.example.albalab.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.albalab.R
import com.example.albalab.navigation.Screen
import com.example.albalab.ui.components.CampoTexto
import com.example.albalab.viewmodel.MainViewModel
import com.example.albalab.viewmodel.UsuarioViewModel

// Formulario 1: inicio de sesión
@Composable
fun LoginScreen(
    mainViewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val estado by usuarioViewModel.login.collectAsState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // widthIn evita que el formulario se estire demasiado en tablets o en horizontal
            Column(
                modifier = Modifier.widthIn(max = 520.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_alba_lab),
                    contentDescription = "Logo de Alba Lab Conecta",
                    modifier = Modifier.size(110.dp)
                )
                Text(text = "Bienvenido", style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = "Ingresa para conocer y apoyar a CORPALIV",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                CampoTexto(
                    valor = estado.correo,
                    onValorChange = usuarioViewModel::onLoginCorreoChange,
                    etiqueta = "Correo electrónico",
                    icono = Icons.Filled.Email,
                    error = estado.errores.correo,
                    teclado = KeyboardType.Email
                )
                CampoTexto(
                    valor = estado.clave,
                    onValorChange = usuarioViewModel::onLoginClaveChange,
                    etiqueta = "Contraseña",
                    icono = Icons.Filled.Lock,
                    error = estado.errores.clave,
                    esClave = true,
                    teclado = KeyboardType.Password
                )
                AnimatedVisibility(visible = estado.errores.general != null) {
                    Text(
                        text = estado.errores.general ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Button(
                    onClick = {
                        usuarioViewModel.iniciarSesion {
                            mainViewModel.navigateTo(Screen.Verificacion, popUpTo = Screen.Login, inclusive = true)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Ingresar")
                }
                TextButton(onClick = { mainViewModel.navigateTo(Screen.Registro) }) {
                    Text("¿No tienes cuenta? Regístrate")
                }
            }
        }
    }
}
