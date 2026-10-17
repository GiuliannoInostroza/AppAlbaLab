package com.example.albalab.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.albalab.navigation.Screen

// NavigationBar con las 4 secciones principales (guía 10)
@Composable
fun BarraInferior(
    rutaActual: String,
    onNavegar: (Screen) -> Unit
) {
    val items = listOf(Screen.Inicio, Screen.Programas, Screen.Colaborar, Screen.Perfil)

    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
        items.forEach { screen ->
            val titulo = when (screen) {
                Screen.Inicio -> "Inicio"
                Screen.Programas -> "Programas"
                Screen.Colaborar -> "Colaborar"
                else -> "Perfil"
            }
            val icono = when (screen) {
                Screen.Inicio -> Icons.Filled.Home
                Screen.Programas -> Icons.Filled.Apps
                Screen.Colaborar -> Icons.Filled.VolunteerActivism
                else -> Icons.Filled.Person
            }
            NavigationBarItem(
                selected = rutaActual == screen.route,
                onClick = { onNavegar(screen) },
                icon = { Icon(imageVector = icono, contentDescription = titulo) },
                label = { Text(titulo) }
            )
        }
    }
}
