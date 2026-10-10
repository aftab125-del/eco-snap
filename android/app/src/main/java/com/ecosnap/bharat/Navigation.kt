package com.ecosnap.bharat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ecosnap.bharat.data.DataRepository
import com.ecosnap.bharat.ui.auth.AuthScreen
import com.ecosnap.bharat.ui.chat.AIChatScreen
import com.ecosnap.bharat.ui.diagnostic.DiagnosticScreen
import com.ecosnap.bharat.ui.history.HistoryScreen
import com.ecosnap.bharat.ui.home.HomeScreen
import com.ecosnap.bharat.ui.scan.ScanScreen
import com.ecosnap.bharat.ui.scrap.ScrapRatesScreen
import com.ecosnap.bharat.ui.society.SocietyScreen
import com.ecosnap.bharat.ui.solutions.SolutionsScreen

sealed class Screen {
    object Home : Screen()
    object Scan : Screen()
    data class Diagnostic(val itemId: String) : Screen()
    data class Solutions(val itemId: String) : Screen()
    object ScrapRates : Screen()
    object Society : Screen()
    object Auth : Screen()
    object Chat : Screen()
    object History : Screen()
}

@Composable
fun MainNavigation() {
    val repository = remember { DataRepository() }
    val backStack = remember { mutableStateListOf<Screen>(Screen.Home) }
    val currentScreen = backStack.lastOrNull() ?: Screen.Home

    fun navigateTo(screen: Screen) {
        backStack.add(screen)
    }

    fun navigateBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.size - 1)
        }
    }

    // Android Hardware/System Navigation Bar Back Button Handler
    BackHandler(enabled = backStack.size > 1) {
        navigateBack()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (val screen = currentScreen) {
            is Screen.Home -> {
                HomeScreen(
                    repository = repository,
                    onNavigateToScan = { navigateTo(Screen.Scan) },
                    onNavigateToScrapRates = { navigateTo(Screen.ScrapRates) },
                    onNavigateToSociety = { navigateTo(Screen.Society) },
                    onNavigateToAuth = { navigateTo(Screen.Auth) },
                    onNavigateToChat = { navigateTo(Screen.Chat) },
                    onNavigateToHistory = { navigateTo(Screen.History) },
                    onItemSelected = { itemId -> navigateTo(Screen.Diagnostic(itemId)) }
                )
            }
            is Screen.Scan -> {
                ScanScreen(
                    repository = repository,
                    onBack = { navigateBack() },
                    onPhotoCaptured = { itemId -> navigateTo(Screen.Diagnostic(itemId)) }
                )
            }
            is Screen.Diagnostic -> {
                DiagnosticScreen(
                    itemId = screen.itemId,
                    repository = repository,
                    onBack = { navigateBack() },
                    onViewSolutions = { itemId -> navigateTo(Screen.Solutions(itemId)) }
                )
            }
            is Screen.Solutions -> {
                SolutionsScreen(
                    itemId = screen.itemId,
                    repository = repository,
                    onBack = { navigateBack() },
                    onImpactLogged = {
                        backStack.clear()
                        backStack.add(Screen.Home)
                    }
                )
            }
            is Screen.ScrapRates -> {
                ScrapRatesScreen(
                    repository = repository,
                    onBack = { navigateBack() }
                )
            }
            is Screen.Society -> {
                SocietyScreen(
                    repository = repository,
                    onBack = { navigateBack() }
                )
            }
            is Screen.Auth -> {
                AuthScreen(
                    repository = repository,
                    onBack = { navigateBack() },
                    onAuthSuccess = { navigateBack() }
                )
            }
            is Screen.Chat -> {
                AIChatScreen(
                    repository = repository,
                    onBack = { navigateBack() }
                )
            }
            is Screen.History -> {
                HistoryScreen(
                    repository = repository,
                    onBack = { navigateBack() },
                    onNavigateToScan = { navigateTo(Screen.Scan) },
                    onNavigateToChat = { navigateTo(Screen.Chat) }
                )
            }
        }
    }
}
