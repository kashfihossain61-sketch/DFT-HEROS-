package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.TournamentViewModel

sealed class Screen {
    object Login : Screen()
    object Dashboard : Screen()
    object AdminDashboard : Screen()
    object Settings : Screen()
    data class TournamentDetails(val tournamentId: String) : Screen()
    data class RegisterTeam(val tournamentId: String) : Screen()
    data class GroupChat(val groupId: String) : Screen()
}

class MainActivity : ComponentActivity() {

    private val viewModel: TournamentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            MyApplicationTheme(darkTheme = isDarkMode) {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
                val currentUser by viewModel.currentUser.collectAsState()

                // Auto-redirect if user auth state changes
                LaunchedEffect(currentUser) {
                    if (currentUser == null) {
                        currentScreen = Screen.Login
                    } else if (currentScreen is Screen.Login) {
                        currentScreen = Screen.Dashboard
                    }
                }

                when (val screen = currentScreen) {
                    is Screen.Login -> {
                        LoginScreen(
                            viewModel = viewModel,
                            onLoginSuccess = {
                                currentScreen = Screen.Dashboard
                            }
                        )
                    }
                    is Screen.Dashboard -> {
                        MainDashboard(
                            viewModel = viewModel,
                            onNavigateToTournament = { id ->
                                currentScreen = Screen.TournamentDetails(id)
                            },
                            onNavigateToChat = { id ->
                                currentScreen = Screen.GroupChat(id)
                            },
                            onNavigateToAdmin = {
                                currentScreen = Screen.AdminDashboard
                            },
                            onNavigateToSettings = {
                                currentScreen = Screen.Settings
                            },
                            onLogout = {
                                currentScreen = Screen.Login
                            }
                        )
                    }
                    is Screen.AdminDashboard -> {
                        AdminDashboardScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                currentScreen = Screen.Dashboard
                            }
                        )
                    }
                    is Screen.Settings -> {
                        SettingsScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                currentScreen = Screen.Dashboard
                            },
                            onLogout = {
                                currentScreen = Screen.Login
                            }
                        )
                    }
                    is Screen.TournamentDetails -> {
                        TournamentDetailsScreen(
                            tournamentId = screen.tournamentId,
                            viewModel = viewModel,
                            onNavigateToRegisterTeam = { id ->
                                currentScreen = Screen.RegisterTeam(id)
                            },
                            onNavigateBack = {
                                currentScreen = Screen.Dashboard
                            }
                        )
                    }
                    is Screen.RegisterTeam -> {
                        TeamRegistrationScreen(
                            tournamentId = screen.tournamentId,
                            viewModel = viewModel,
                            onRegistrationSuccess = {
                                currentScreen = Screen.TournamentDetails(screen.tournamentId)
                            },
                            onNavigateBack = {
                                currentScreen = Screen.TournamentDetails(screen.tournamentId)
                            }
                        )
                    }
                    is Screen.GroupChat -> {
                        GroupChatScreen(
                            groupId = screen.groupId,
                            viewModel = viewModel,
                            onNavigateBack = {
                                currentScreen = Screen.Dashboard
                            }
                        )
                    }
                }
            }
        }
    }
}
