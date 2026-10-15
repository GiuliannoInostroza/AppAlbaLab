package com.example.albalab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Esquema de colores oscuro de Material 3 (la app siempre usa modo oscuro)
private val TemaOscuro = darkColorScheme(
    primary = Violeta,
    onPrimary = NegroOled,
    primaryContainer = VioletaOscuro,
    onPrimaryContainer = VioletaClaro,
    secondary = Lila,
    onSecondary = NegroOled,
    secondaryContainer = LilaOscuro,
    onSecondaryContainer = BlancoSuave,
    tertiary = Indigo,
    onTertiary = NegroOled,
    tertiaryContainer = IndigoOscuro,
    onTertiaryContainer = BlancoSuave,
    background = NegroOled,
    onBackground = BlancoSuave,
    surface = NegroOled,
    onSurface = BlancoSuave,
    surfaceVariant = SuperficieAlta,
    onSurfaceVariant = GrisLavanda,
    surfaceContainerLowest = NegroOled,
    surfaceContainerLow = Superficie,
    surfaceContainer = Superficie,
    surfaceContainerHigh = SuperficieAlta,
    surfaceContainerHighest = SuperficieMasAlta,
    outline = Contorno,
    outlineVariant = ContornoSuave,
    error = RojoError,
    onError = NegroOled,
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
