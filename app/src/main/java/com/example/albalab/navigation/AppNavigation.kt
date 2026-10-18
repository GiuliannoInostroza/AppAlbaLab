package com.example.albalab.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.albalab.ui.screens.CargaScreen
import com.example.albalab.ui.screens.ColaborarScreen
import com.example.albalab.ui.screens.InicioScreen
import com.example.albalab.ui.screens.LoginScreen
import com.example.albalab.ui.screens.MisColaboracionesScreen
import com.example.albalab.ui.screens.PerfilScreen
import com.example.albalab.ui.screens.ProgramasScreen
import com.example.albalab.ui.screens.PruebaVidaScreen
import com.example.albalab.ui.screens.RegistroScreen
import com.example.albalab.ui.screens.VerificacionScreen
import com.example.albalab.viewmodel.ColaboracionViewModel
import com.example.albalab.viewmodel.MainViewModel
import com.example.albalab.viewmodel.ProgramasViewModel
import com.example.albalab.viewmodel.UsuarioViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Aquí se crean los ViewModels una sola vez y se comparten entre pantallas (guía 11)
    val mainViewModel: MainViewModel = viewModel()
    val usuarioViewModel: UsuarioViewModel = viewModel()
    val colaboracionViewModel: ColaboracionViewModel = viewModel()
    val programasViewModel: ProgramasViewModel = viewModel()

    // Escucha los eventos de navegación que emite el MainViewModel (guía 10)
    LaunchedEffect(Unit) {
        mainViewModel.navigationEvents.collectLatest { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.route.route) {
                        event.popUpToRoute?.let {
                            popUpTo(it.route) {
                                inclusive = event.inclusive
                            }
                        }
                        launchSingleTop = event.singleTop
                    }
                }
                is NavigationEvent.PopBackStack -> navController.popBackStack()
            }
        }
    }

    // Transiciones rápidas entre pantallas: aparecer + escala (220 ms) y desvanecer (150 ms)
    NavHost(
        navController = navController,
        startDestination = Screen.Carga.route,
        enterTransition = { fadeIn(tween(220)) + scaleIn(initialScale = 0.96f, animationSpec = tween(220)) },
        exitTransition = { fadeOut(tween(150)) },
        popEnterTransition = { fadeIn(tween(220)) },
        popExitTransition = { fadeOut(tween(150)) + scaleOut(targetScale = 0.96f, animationSpec = tween(150)) }
    ) {
        composable(Screen.Carga.route) {
            CargaScreen(mainViewModel, usuarioViewModel)
        }
        composable(Screen.Login.route) {
            LoginScreen(mainViewModel, usuarioViewModel)
        }
        composable(Screen.Registro.route) {
            RegistroScreen(mainViewModel, usuarioViewModel)
        }
        composable(Screen.Verificacion.route) {
            VerificacionScreen(mainViewModel, usuarioViewModel)
        }
        composable(Screen.PruebaVida.route) {
            PruebaVidaScreen(mainViewModel)
        }
        composable(Screen.Inicio.route) {
            InicioScreen(mainViewModel, usuarioViewModel, colaboracionViewModel)
        }
        composable(Screen.Programas.route) {
            ProgramasScreen(mainViewModel, programasViewModel, colaboracionViewModel, usuarioViewModel)
        }
        composable(Screen.Colaborar.route) {
            ColaborarScreen(mainViewModel, usuarioViewModel, colaboracionViewModel)
        }
        composable(Screen.MisColaboraciones.route) {
            MisColaboracionesScreen(mainViewModel, usuarioViewModel, colaboracionViewModel)
        }
        composable(Screen.Perfil.route) {
            PerfilScreen(mainViewModel, usuarioViewModel, colaboracionViewModel)
        }
    }
}
