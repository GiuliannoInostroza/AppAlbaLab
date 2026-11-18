package com.example.albalab.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.albalab.data.model.Indicador
import com.example.albalab.ui.screens.IndicadoresScreen
import com.example.albalab.viewmodel.IndicadorViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class IndicadoresScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun el_nombre_de_cada_indicador_debe_aparecer_en_pantalla() {
        // Simulamos los datos que el ViewModel entregaría
        val fakeIndicadores = listOf(
            Indicador("uf", "Unidad de fomento (UF)", "2026-11-17", 41350.42),
            Indicador("dolar", "Dólar observado", "2026-11-17", 1001.17)
        )
        // Subclase falsa de IndicadorViewModel con StateFlow simulado
        val fakeViewModel = object : IndicadorViewModel() {
            override val indicadores = MutableStateFlow(fakeIndicadores)
            override val error = MutableStateFlow<String?>(null)
        }
        // Renderizamos la pantalla con el ViewModel falso
        composeRule.setContent {
            IndicadoresScreen(viewModel = fakeViewModel)
        }
        // Validamos que los nombres se muestran correctamente en la UI
        composeRule.onNodeWithText("Unidad de fomento (UF)").assertIsDisplayed()
        composeRule.onNodeWithText("Dólar observado").assertIsDisplayed()
    }
}
