package com.example.albalab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.albalab.ui.screens.InicioScreen
import com.example.albalab.ui.theme.AppAlbaLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppAlbaLabTheme {
                InicioScreen()
            }
        }
    }
}
