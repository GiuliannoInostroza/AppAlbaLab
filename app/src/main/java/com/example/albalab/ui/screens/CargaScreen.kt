package com.example.albalab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.albalab.R
import com.example.albalab.navigation.Screen
import com.example.albalab.viewmodel.MainViewModel
import com.example.albalab.viewmodel.UsuarioViewModel

// Pantalla de carga: mientras se revisa la sesión guardada muestra un indicador (guía 12)
@Composable
fun CargaScreen(
    mainViewModel: MainViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val cargando by usuarioViewModel.cargando.collectAsState()
    val usuario by usuarioViewModel.usuarioActual.collectAsState()

    // Cuando termina de cargar decide a dónde ir
    LaunchedEffect(cargando) {
        if (!cargando) {
            if (usuario != null) {
                mainViewModel.navigateTo(Screen.Verificacion, popUpTo = Screen.Carga, inclusive = true)
            } else {
                mainViewModel.navigateTo(Screen.Login, popUpTo = Screen.Carga, inclusive = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_alba_lab),
            contentDescription = "Logo de Alba Lab Conecta",
            modifier = Modifier.size(140.dp)
        )
        Text(
            text = "Alba Lab Conecta",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}
