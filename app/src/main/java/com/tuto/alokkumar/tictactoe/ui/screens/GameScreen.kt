package com.tuto.alokkumar.tictactoe.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.GameState
import com.tuto.alokkumar.tictactoe.data.PreferencesManager
import com.tuto.alokkumar.tictactoe.ui.components.GameBoard
import com.tuto.alokkumar.tictactoe.ui.components.GameInfoSection
import com.tuto.alokkumar.tictactoe.ui.components.ScoreBoard
import com.tuto.alokkumar.tictactoe.viewModel.GameViewModel
import com.tuto.alokkumar.tictactoe.viewModel.GameViewModelFactory

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    mode: GameMode = GameMode.PVP,
    loadHistory: GameHistory? = null,
    onHome: () -> Unit = {},
    onSettings: () -> Unit = {},
    viewModel: GameViewModel = viewModel(
        factory = GameViewModelFactory(mode, loadHistory)
    )
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    var isPaused by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val prefs = remember { PreferencesManager(context) }

    // 🔙 Handle back press
    BackHandler(enabled = true) {
        if (!isPaused) {
            isPaused = true
            viewModel.pauseGame()
        } else {
            isPaused = false
            viewModel.pauseGame()
        }
    }

    // 💤 Pause when app goes to background
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    isPaused = true
                    viewModel.pauseGame()
                }
                Lifecycle.Event.ON_RESUME -> {
                    // Resume only if user didn’t manually pause
                    if (!isPaused) viewModel.resumeGame()
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 🎨 UI Layout
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = {
                isPaused = true
                viewModel.pauseGame()
            }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Pause",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        BoxWithConstraints(contentAlignment = Alignment.Center) {
            val isLandscape = maxWidth > maxHeight
            if (isPaused) {
                PauseScreen(
                    visible = true,
                    onPlay = {
                        isPaused = false
                        viewModel.resumeGame()
                    },
                    onRestart = {
                        isPaused = false
                        viewModel.restartGame()
                    },
                    onHome = {
                        isPaused = false
                        viewModel.saveHistory(prefs)
                        onHome()
                    },
                    onSettings = {
                        isPaused = false
                        viewModel.saveHistory(prefs)
                        onSettings()
                    }
                )
            } else {
                if (isLandscape) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        GameInfoSection(
                            currentPlayer = state.currentPlayer,
                            winner = state.winner,
                            onRestart = viewModel::restartGame
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            ScoreBoard(
                                xWins = state.xWins,
                                oWins = state.oWins,
                                draws = state.draws
                            )
                            GameBoard(
                                board = state.board,
                                onCellClick = viewModel::onCellClicked
                            )
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        ScoreBoard(
                            xWins = state.xWins,
                            oWins = state.oWins,
                            draws = state.draws,
                            modifier = Modifier.fillMaxWidth()
                        )
                        GameInfoSection(
                            currentPlayer = state.currentPlayer,
                            winner = state.winner,
                            onRestart = viewModel::restartGame
                        )
                        GameBoard(
                            board = state.board,
                            onCellClick = viewModel::onCellClicked
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GamePreview() {
    GameScreen(mode = GameMode.HARD)
}
