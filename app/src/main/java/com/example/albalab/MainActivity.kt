package com.example.albalab

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.example.albalab.data.Notificaciones
import com.example.albalab.navigation.AppNavigation
import com.example.albalab.ui.theme.AppAlbaLabTheme

// FragmentActivity (en vez de ComponentActivity) porque el diálogo de biometría la necesita
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Barras del sistema en modo oscuro para que se vean sobre el fondo negro
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        Notificaciones.crearCanal(this)
        setContent {
            AppAlbaLabTheme {
                AppNavigation()
            }
        }
    }
}
