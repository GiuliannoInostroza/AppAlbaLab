package com.example.albalab.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.albalab.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.albalab.navigation.Screen
import com.example.albalab.ui.components.BarraInferior
import com.example.albalab.ui.components.BarraSuperior
import com.example.albalab.ui.components.TarjetaInfo
import com.example.albalab.ui.utils.obtenerWindowSizeClass
import com.example.albalab.viewmodel.ColaboracionViewModel
import com.example.albalab.viewmodel.MainViewModel
import com.example.albalab.viewmodel.UsuarioViewModel
import com.example.albalab.ui.theme.AppAlbaLabTheme

@Composable
fun InicioScreen(
    mainViewModel: MainViewModel = viewModel(),
    usuarioViewModel: UsuarioViewModel = viewModel(),
    colaboracionViewModel: ColaboracionViewModel = viewModel()
) {
    val usuario by usuarioViewModel.usuarioActual.collectAsState()
    val windowSizeClass = obtenerWindowSizeClass()

    // Prepara el formulario con el nombre del usuario y abre "Colaborar"
    fun irAColaborar() {
        colaboracionViewModel.nuevoFormulario(nombre = usuario?.nombre ?: "")
        mainViewModel.navigateTo(Screen.Colaborar, popUpTo = Screen.Inicio)
    }

    Scaffold(
        topBar = { BarraSuperior(titulo = "Alba Lab Conecta") },
        bottomBar = {
            BarraInferior(rutaActual = Screen.Inicio.route) { destino ->
                mainViewModel.navigateTo(destino, popUpTo = Screen.Inicio)
            }
        }
    ) { innerPadding ->
        // Guía 9: según el ancho de la pantalla se muestra un diseño u otro
        when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Compact -> InicioCompacta(
                modifier = Modifier.padding(innerPadding),
                nombre = usuario?.nombre ?: "",
                onColaborar = { irAColaborar() }
            )
            else -> InicioExpandida(
                modifier = Modifier.padding(innerPadding),
                nombre = usuario?.nombre ?: "",
                onColaborar = { irAColaborar() }
            )
        }
    }
}

// Teléfono en vertical: todo en una columna
@Composable
fun InicioCompacta(modifier: Modifier, nombre: String, onColaborar: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Saludo(nombre)
        SeccionProposito()
        BotonColaborar(onColaborar)
        SeccionModalidades()
        SeccionImpacto()
    }
}

// Tablet o teléfono girado: dos columnas lado a lado
@Composable
fun InicioExpandida(modifier: Modifier, nombre: String, onColaborar: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Saludo(nombre)
            SeccionProposito()
            BotonColaborar(onColaborar)
            SeccionImpacto()
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp)
        ) {
            SeccionModalidades()
        }
    }
}

@Composable
fun Saludo(nombre: String) {
    Text(
        text = "Hola, $nombre",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun BotonColaborar(onColaborar: () -> Unit) {
    Button(
        onClick = onColaborar,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text("Quiero colaborar")
    }
}

// Logo y propósito de la organización
@Composable
fun SeccionProposito() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_alba_lab),
            contentDescription = "Logo de Alba Lab Conecta",
            modifier = Modifier.size(120.dp)
        )
        Text(
            text = "Inclusión que se construye en conjunto",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "CORPALIV promueve la autonomía y la inclusión social y laboral de personas con discapacidad múltiple, a través de la Escuela Especial Jan Van Dijk y la Red Sociolaboral Alba Lab.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// Las 5 formas de colaborar con la organización
@Composable
fun SeccionModalidades() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Cómo puedes colaborar",
            style = MaterialTheme.typography.titleLarge
        )
        TarjetaInfo(
            icono = Icons.Filled.Favorite,
            titulo = "Donación",
            texto = "Tu aporte financia materiales de taller y apoyo pedagógico."
        )
        TarjetaInfo(
            icono = Icons.Filled.VolunteerActivism,
            titulo = "Voluntariado",
            texto = "Comparte una jornada de actividades con los estudiantes."
        )
        TarjetaInfo(
            icono = Icons.Filled.Handshake,
            titulo = "Alianza empresarial",
            texto = "Abre puestos de trabajo inclusivos en tu empresa."
        )
        TarjetaInfo(
            icono = Icons.Filled.Storefront,
            titulo = "Compra de productos",
            texto = "Elige productos hechos en los talleres de producción."
        )
        TarjetaInfo(
            icono = Icons.Filled.School,
            titulo = "Capacitación",
            texto = "Charlas para que tu equipo aprenda a incluir."
        )
    }
}

// Historia de impacto (caso ilustrativo y anonimizado, como pide el caso)
@Composable
fun SeccionImpacto() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Historia de impacto",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Después de dos años en el taller de preparación para el empleo, una participante trabaja hoy media jornada en una empresa aliada.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Caso ilustrativo y anonimizado",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview(name = "Compacta", widthDp = 360, heightDp = 800)
@Composable
fun PreviewCompacta() {
    AppAlbaLabTheme {
        InicioCompacta(modifier = Modifier, nombre = "Camila", onColaborar = {})
    }
}

@Preview(name = "Expandida", widthDp = 900, heightDp = 600)
@Composable
fun PreviewExpandida() {
    AppAlbaLabTheme {
        InicioExpandida(modifier = Modifier, nombre = "Camila", onColaborar = {})
    }
}
