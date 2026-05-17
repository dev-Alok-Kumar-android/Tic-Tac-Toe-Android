package com.tuto.alokkumar.tictactoe.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.ui.components.GameBoard3D
import com.tuto.alokkumar.tictactoe.ui.components.GameInfoSection
import com.tuto.alokkumar.tictactoe.ui.components.PauseScreen
import com.tuto.alokkumar.tictactoe.ui.components.ScoreBoard
import com.tuto.alokkumar.tictactoe.viewModel.GameViewModel3D

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen3D(
    modifier: Modifier = Modifier,
    difficulty: GameMode,
    onHome: () -> Unit,
    onSettings: () -> Unit,
    viewModel: GameViewModel3D = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val state3D by viewModel.state.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // 🔙 Handle back press
    BackHandler(enabled = true) {
        if (!isPaused) {
            viewModel.pauseGame()
        } else {
            viewModel.resumeGame()
        }
    }

    // 💤 Pause when app goes to background
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    viewModel.pauseGame()
                }

                Lifecycle.Event.ON_RESUME -> {
                    viewModel.resumeGame()
                }

                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Tic Tac Toe") }, navigationIcon = {
                IconButton(onClick = {
                    if (!isPaused) {
                        viewModel.pauseGame()
                    } else {
                        viewModel.resumeGame()
                    }
                }) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Menu,
                        contentDescription = if (isPaused) "Resume" else "Pause",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            })
        }) {
        BoxWithConstraints(
            contentAlignment = Alignment.Center, modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {
            val isLandscape = maxWidth > maxHeight
            if (isPaused) {
                PauseScreen(visible = true, onPlay = {
                    viewModel.resumeGame()
                }, onRestart = {
                    viewModel.restartGame()
                }, onHome = {
                    onHome()
                }, onSettings = {
                    onSettings()
                })
            } else {
                if (isLandscape) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ScoreBoard(
                                xWins = state.xWins, oWins = state.oWins, draws = state.draws
                            )
                            GameInfoSection(
                                currentPlayer = state.currentPlayer,
                                winner = state.winner,
                                isAiThinking = false,
                                onRestart = viewModel::restartGame
                            )


                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                                Button(
                                    onClick = viewModel::prevLayer,
                                    enabled = state3D.activeLayer > 0
                                ) {
                                    Text("⬇ Layer")
                                }

                                Button(
                                    onClick = viewModel::nextLayer,
                                    enabled = state3D.activeLayer < 2
                                ) {
                                    Text("⬆ Layer")
                                }
                            }

                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            GameBoard3D(
                                board = state3D.board,
                                winLine = state3D.winLine,
                                winner = state3D.winner,
                                activeLayer = state3D.activeLayer,
                                onCellClick = { layer, index2D ->
                                    viewModel.onCellClicked(layer, index2D)
                                })
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
                            isAiThinking = false,
                            onRestart = viewModel::restartGame
                        )
                        GameBoard3D(
                            board = state3D.board,
                            winLine = state3D.winLine,
                            winner = state3D.winner,
                            activeLayer = state3D.activeLayer,
                            onCellClick = { layer, index2D ->
                                viewModel.onCellClicked(layer, index2D)
                            })

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                            Button(
                                onClick = viewModel::prevLayer, enabled = state3D.activeLayer > 0
                            ) {
                                Text("⬇ Layer")
                            }

                            Button(
                                onClick = viewModel::nextLayer, enabled = state3D.activeLayer < 2
                            ) {
                                Text("⬆ Layer")
                            }
                        }


                    }
                }
            }
        }
    }
}