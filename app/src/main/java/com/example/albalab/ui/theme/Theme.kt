package com.example.albalab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Esquema de colores oscuro de Material 3 (la app siempre usa modo oscuro)
private val TemaOscuro = darkColorScheme(
    primary = Violeta,
    onPrimary = TextoOscuro,
    primaryContainer = VioletaOscuro,
    onPrimaryContainer = VioletaClaro,
    secondary = Lila,
    onSecondary = TextoOscuro,
    secondaryContainer = LilaOscuro,
    onSecondaryContainer = BlancoSuave,
    tertiary = Indigo,
    onTertiary = TextoOscuro,
    tertiaryContainer = IndigoOscuro,
    onTertiaryContainer = BlancoSuave,
    background = Fondo,
    onBackground = BlancoSuave,
    surface = Fondo,
    onSurface = BlancoSuave,
    surfaceVariant = SuperficieAlta,
    onSurfaceVariant = GrisLavanda,
    surfaceContainerLowest = Fondo,
    surfaceContainerLow = Superficie,
    surfaceContainer = Superficie,
    surfaceContainerHigh = SuperficieAlta,
    surfaceContainerHighest = SuperficieMasAlta,
    outline = Contorno,
    outlineVariant = ContornoSuave,
    error = RojoError,
    onError = TextoOscuro,
    errorContainer = RojoErrorOscuro,
    onErrorContainer = BlancoSuave
)

@Composable
fun AppAlbaLabTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TemaOscuro,
        typography = Typography,
        content = content
    )
}
