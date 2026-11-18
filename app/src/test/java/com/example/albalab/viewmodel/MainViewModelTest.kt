package com.example.albalab.viewmodel

import com.example.albalab.navigation.NavigationEvent
import com.example.albalab.navigation.Screen
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest : StringSpec({

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    "navigateTo() y navigateBack() deben emitir los eventos de navegación en orden" {
        runTest {
            val viewModel = MainViewModel()
            val eventos = mutableListOf<NavigationEvent>()
            // Escuchamos los eventos como lo hace AppNavigation
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.navigationEvents.toList(eventos)
            }
            viewModel.navigateTo(Screen.Inicio, popUpTo = Screen.Login, inclusive = true)
            viewModel.navigateBack()
            eventos shouldContainExactly listOf(
                NavigationEvent.NavigateTo(
                    route = Screen.Inicio,
                    popUpToRoute = Screen.Login,
                    inclusive = true,
                    singleTop = true
                ),
                NavigationEvent.PopBackStack
            )
        }
    }
})
