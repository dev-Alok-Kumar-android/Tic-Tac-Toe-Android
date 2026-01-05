package com.tuto.alokkumar.tictactoe

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tuto.alokkumar.tictactoe.core.pref.Preferences
import com.tuto.alokkumar.tictactoe.core.sound.Sound
import com.tuto.alokkumar.tictactoe.data.AppTheme
import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.ui.components.ImmersiveMode
import com.tuto.alokkumar.tictactoe.ui.screens.AboutScreen
import com.tuto.alokkumar.tictactoe.ui.screens.GameScreen
import com.tuto.alokkumar.tictactoe.ui.screens.HistoryScreen
import com.tuto.alokkumar.tictactoe.ui.screens.MenuScreen
import com.tuto.alokkumar.tictactoe.ui.screens.SettingsScreen
import com.tuto.alokkumar.tictactoe.ui.theme.TicTacToeTheme
import com.tuto.alokkumar.tictactoe.viewModel.GameViewModel
import com.tuto.alokkumar.tictactoe.viewModel.GameViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val activity = LocalActivity.current
    val isBgmEnabled by Preferences.bgmEnabledFlow.collectAsState(initial = false)
    val mode by Preferences.gameModeFlow.collectAsState(initial = GameMode.PVP)
    val isSoundEnabled by Preferences.soundEnabledFlow.collectAsState(initial = false)
    val isImmersiveMode by Preferences.immersiveFlow.collectAsState(initial = false)
    val theme by Preferences.themeFlow.collectAsState(initial = AppTheme.SYSTEM)
    val dynamicColor by Preferences.dynamicColorFlow.collectAsState(initial = false)
    val histories by Preferences.getGameHistoryFlow().collectAsState(initial = emptyList())
    var selectedHistory: GameHistory? by remember { mutableStateOf(null) }

    Sound.setBgmEnabled(isBgmEnabled)
    Sound.setSoundEnabled(isSoundEnabled)
    LaunchedEffect(isBgmEnabled) {
        if (isBgmEnabled) {
            Sound.playBgm(context)
        } else {
            Sound.stopBgm()
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) ImmersiveMode(isImmersiveMode)

    TicTacToeTheme(appTheme = theme, dynamicColor = dynamicColor) {
        NavHost(navController, startDestination = "menu") {

            composable("menu") {
                MenuScreen(
                    onStartGame = { navController.navigate("game/${mode.name}") },
                    onViewStats = { navController.navigate("history") },
                    onExit = { activity?.finish() },
                    onPvpMode = { navController.navigate("game/${GameMode.PVP.name}") },
                    onSettings = { navController.navigate("settings") },
                    onAbout = { navController.navigate("about") })
            }

            composable("game/{mode}") { backStackEntry ->
                val modeString = backStackEntry.arguments?.getString("mode") ?: "PVP"
                val gameMode = GameMode.valueOf(modeString)

                GameScreen(
                    mode = gameMode, onHome = {
                        navController.navigate("menu") {
                            popUpTo("menu") {
                                inclusive = true
                            }
                        }
                    }, onSettings = { navController.navigate("settings") }, modifier = modifier
                )
            }

            composable("game/restore") {
                val context = LocalContext.current
                LaunchedEffect(selectedHistory) {
                    if (selectedHistory == null) {
                        Toast.makeText(context, "No game to restore", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Restored Game", Toast.LENGTH_SHORT).show()
                    }
                }

                if (selectedHistory == null) return@composable

                val gameMode = selectedHistory?.mode ?: GameMode.PVP
                val gameViewModel: GameViewModel = viewModel(
                    factory = GameViewModelFactory(mode, selectedHistory)
                )
                GameScreen(
                    mode = gameMode,
                    loadHistory = selectedHistory,
                    onHome = {
                        navController.navigate("menu") {
                            popUpTo("menu") { inclusive = true }
                        }
                    },
                    onSettings = { navController.navigate("settings") },
                    modifier = modifier,
                    viewModel = gameViewModel
                )
            }


            composable("history") {
                val scope = rememberCoroutineScope()
                HistoryScreen(
                    histories,
                    onClear = { scope.launch { Preferences.clearAllGameHistory() } },
                    onItemClick = {
                        selectedHistory = it
                        navController.navigate("game/restore")

                    },
                    onItemClear = {
                        scope.launch {
                            Preferences.removeGameHistory(it)
                        }
                    })
            }

            composable("settings") {
                SettingsScreen(
                    onBack = { navController.popBackStack() })
            }

            composable("about") {
                AboutScreen(onBack = { navController.popBackStack() })
            }

        }
    }
}