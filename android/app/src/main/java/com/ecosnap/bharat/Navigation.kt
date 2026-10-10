package com.ecosnap.bharat

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
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (val screen = currentScreen) {
            is Screen.Home -> {
                HomeScreen(
                    repository = repository,
                    onNavigateToScan = { currentScreen = Screen.Scan },
                    onNavigateToScrapRates = { currentScreen = Screen.ScrapRates },
                    onNavigateToSociety = { currentScreen = Screen.Society },
                    onNavigateToAuth = { currentScreen = Screen.Auth },
                    onNavigateToChat = { currentScreen = Screen.Chat },
                    onNavigateToHistory = { currentScreen = Screen.History },
                    onItemSelected = { itemId -> currentScreen = Screen.Diagnostic(itemId) }
                )
            }
            is Screen.Scan -> {
                ScanScreen(
                    repository = repository,
                    onBack = { currentScreen = Screen.Home },
                    onPhotoCaptured = { itemId -> currentScreen = Screen.Diagnostic(itemId) }
                )
            }
            is Screen.Diagnostic -> {
                DiagnosticScreen(
                    itemId = screen.itemId,
                    repository = repository,
                    onBack = { currentScreen = Screen.Home },
                    onViewSolutions = { itemId -> currentScreen = Screen.Solutions(itemId) }
                )
            }
            is Screen.Solutions -> {
                SolutionsScreen(
                    itemId = screen.itemId,
                    repository = repository,
                    onBack = { currentScreen = Screen.Diagnostic(screen.itemId) },
                    onImpactLogged = { currentScreen = Screen.Home }
                )
            }
            is Screen.ScrapRates -> {
                ScrapRatesScreen(
                    repository = repository,
                    onBack = { currentScreen = Screen.Home }
                )
            }
            is Screen.Society -> {
                SocietyScreen(
                    repository = repository,
                    onBack = { currentScreen = Screen.Home }
                )
            }
            is Screen.Auth -> {
                AuthScreen(
                    repository = repository,
                    onBack = { currentScreen = Screen.Home },
                    onAuthSuccess = { currentScreen = Screen.Home }
                )
            }
            is Screen.Chat -> {
                AIChatScreen(
                    repository = repository,
                    onBack = { currentScreen = Screen.Home }
                )
            }
            is Screen.History -> {
                HistoryScreen(
                    repository = repository,
                    onBack = { currentScreen = Screen.Home },
                    onNavigateToScan = { currentScreen = Screen.Scan },
                    onNavigateToChat = { currentScreen = Screen.Chat }
                )
            }
        }
    }
}
