package com.example.albalab.ui.screens

import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.albalab.data.Biometria
import com.example.albalab.navigation.Screen
import com.example.albalab.ui.components.BarraSuperior
import com.example.albalab.ui.components.CampoTexto
import com.example.albalab.viewmodel.MainViewModel
import com.example.albalab.viewmodel.UsuarioViewModel

// Verificación de identidad después del login: 4 formas de confirmar que es la persona
@Composable
fun VerificacionScreen(
    mainViewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val context = LocalContext.current
    val activity = context as FragmentActivity
    val usuario by usuarioViewModel.usuarioActual.collectAsState()

    var mostrarClave by rememberSaveable { mutableStateOf(false) }
    var clave by rememberSaveable { mutableStateOf("") }
    var errorClave by remember { mutableStateOf<String?>(null) }

    fun irAlInicio() {
        mainViewModel.navigateTo(Screen.Inicio, popUpTo = Screen.Verificacion, inclusive = true)
    }

    fun mostrarError(texto: String) {
        Toast.makeText(context, texto, Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = { BarraSuperior(titulo = "Verifica tu identidad") }
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
                modifier = Modifier.widthIn(max = 560.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Hola, ${usuario?.nombre ?: ""}. Elige cómo confirmar que eres tú.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OpcionVerificacion(
                    icono = Icons.Filled.Face,
                    titulo = "Prueba de vida",
                    texto = "Haz 3 gestos frente a la cámara. Una foto no puede hacerlos.",
                    recomendada = true,
                    onClick = { mainViewModel.navigateTo(Screen.PruebaVida) }
                )
                OpcionVerificacion(
                    icono = Icons.Filled.PhoneAndroid,
                    titulo = "Verificación fácil",
                    texto = "Usa el rostro, la huella o el PIN de tu teléfono.",
                    onClick = {
                        Biometria.autenticar(
                            activity,
                            BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL,
                            "Verificación fácil",
                            onExito = { irAlInicio() },
                            onError = { mostrarError(it) }
                        )
                    }
                )
                OpcionVerificacion(
                    icono = Icons.Filled.Fingerprint,
                    titulo = "Huella digital",
                    texto = "Solo con el sensor de huella.",
                    onClick = {
                        Biometria.autenticar(
                            activity,
                            BiometricManager.Authenticators.BIOMETRIC_STRONG,
                            "Huella digital",
                            onExito = { irAlInicio() },
                            onError = { mostrarError(it) }
                        )
                    }
                )
                OpcionVerificacion(
                    icono = Icons.Filled.Password,
                    titulo = "Contraseña de la cuenta",
                    texto = "Vuelve a escribir tu contraseña.",
                    onClick = { mostrarClave = !mostrarClave }
                )
                AnimatedVisibility(visible = mostrarClave) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        CampoTexto(
                            valor = clave,
                            onValorChange = {
                                clave = it
                                errorClave = null
                            },
                            etiqueta = "Contraseña",
                            icono = Icons.Filled.Lock,
                            error = errorClave,
                            esClave = true
                        )
                        Button(
                            onClick = {
                                if (usuarioViewModel.verificarClave(clave)) {
                                    irAlInicio()
                                } else {
                                    errorClave = "Contraseña incorrecta"
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Confirmar")
                        }
                    }
                }
                TextButton(
                    onClick = {
                        usuarioViewModel.cerrarSesion {
                            mainViewModel.navigateTo(Screen.Login, popUpTo = Screen.Verificacion, inclusive = true)
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Usar otra cuenta")
                }
            }
        }
    }
}

// Tarjeta de cada opción. La recomendada se destaca con otro color y una etiqueta
@Composable
private fun OpcionVerificacion(
    icono: ImageVector,
    titulo: String,
    texto: String,
    onClick: () -> Unit,
    recomendada: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (recomendada) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = titulo, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = texto,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (recomendada) {
                Text(
                    text = "Recomendada",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
