package com.tuto.alokkumar.tictactoe

import android.content.pm.ActivityInfo
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tuto.alokkumar.tictactoe.core.navigation.BoardSizeNavType
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.Orientation
import com.tuto.alokkumar.tictactoe.ui.components.ImmersiveMode
import com.tuto.alokkumar.tictactoe.ui.screens.AboutScreen
import com.tuto.alokkumar.tictactoe.ui.screens.GameScreen
import com.tuto.alokkumar.tictactoe.ui.screens.HistoryScreen
import com.tuto.alokkumar.tictactoe.ui.screens.MenuScreen
import com.tuto.alokkumar.tictactoe.ui.screens.SettingsScreen
import com.tuto.alokkumar.tictactoe.ui.theme.TicTacToeTheme
import com.tuto.alokkumar.tictactoe.viewModel.GameViewModel
import com.tuto.alokkumar.tictactoe.viewModel.SettingsViewModel
import kotlin.reflect.typeOf

/**
 * Top-level Navigation Graph for the Tic Tac Toe application.
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val activity = LocalActivity.current
    
    val isBgmEnabled by settingsViewModel.bgmEnabled.collectAsStateWithLifecycle()
    val isImmersiveMode by settingsViewModel.immersiveMode.collectAsStateWithLifecycle()
    val orientationPreference by settingsViewModel.orientation.collectAsStateWithLifecycle()
    val theme by settingsViewModel.theme.collectAsStateWithLifecycle()
    val dynamicColor by settingsViewModel.dynamicColor.collectAsStateWithLifecycle()
    
    var selectedHistory: GameHistory? by remember { mutableStateOf(null) }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Handle immediate BGM playback when toggled ON in settings
    LaunchedEffect(isBgmEnabled) {
        if (isBgmEnabled) {
            settingsViewModel.playBgm(context)
        } else {
            settingsViewModel.pauseBgm()
        }
    }

    DisposableEffect(lifecycleOwner, isBgmEnabled) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (isBgmEnabled) settingsViewModel.playBgm(context)
                }
                Lifecycle.Event.ON_PAUSE -> {
                    settingsViewModel.pauseBgm()
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Trigger Android 11+ Immersive UI side-effects
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) ImmersiveMode(isImmersiveMode)

    // Handle screen orientation preference
    LaunchedEffect(orientationPreference) {
        activity?.requestedOrientation = when (orientationPreference) {
            Orientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            Orientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            Orientation.AUTO -> ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
            Orientation.SYSTEM -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    TicTacToeTheme(appTheme = theme, dynamicColor = dynamicColor) {
        NavHost(navController, startDestination = Route.Menu) {

            // Main Hub
            composable<Route.Menu> {
                MenuScreen(
                    onStartGame = { selectedMode, size -> navController.navigate(Route.Game(selectedMode, size)) },
                    onViewStats = { navController.navigate(Route.History) },
                    onExit = { activity?.finish() },
                    onPvpMode = { size -> navController.navigate(Route.Game(GameMode.PVP, size)) },
                    onSettings = { navController.navigate(Route.Settings) },
                    onAbout = { navController.navigate(Route.About) })
            }

            // Standard New Match Route
            composable<Route.Game>(
                typeMap = mapOf(typeOf<BoardSize>() to BoardSizeNavType)
            ) {
                GameScreen(
                    onHome = {
                        navController.navigate(Route.Menu) {
                            popUpTo(Route.Menu) { inclusive = true }
                        }
                    }, 
                    onSettings = { navController.navigate(Route.Settings) }, 
                    modifier = modifier,
                    viewModel = hiltViewModel()
                )
            }

            // Restore/Replay Past Match Route
            composable<Route.RestoreGame> {
                val currentHistory = selectedHistory
                
                LaunchedEffect(currentHistory) {
                    if (currentHistory == null) {
                        Toast.makeText(context, "No game to restore", Toast.LENGTH_SHORT).show()
                    }
                }

                if (currentHistory == null) return@composable

                val gameViewModel: GameViewModel = hiltViewModel()
                
                // Manually load history into ViewModel
                LaunchedEffect(currentHistory) {
                    gameViewModel.loadFromHistory(currentHistory)
                }

                GameScreen(
                    onHome = {
                        navController.navigate(Route.Menu) {
                            popUpTo(Route.Menu) { inclusive = true }
                        }
                    },
                    onSettings = { navController.navigate(Route.Settings) },
                    modifier = modifier,
                    viewModel = gameViewModel
                )
            }


            // Past Match Logs
            composable<Route.History> {
                HistoryScreen(
                    onItemClick = {
                        selectedHistory = it
                        navController.navigate(Route.RestoreGame)
                    }
                )
            }

            // Configuration Panels
            composable<Route.Settings> {
                SettingsScreen(onBack = { navController.popBackStack() })
            }

            composable<Route.About> {
                AboutScreen(onBack = { navController.popBackStack() })
            }

        }
    }
}