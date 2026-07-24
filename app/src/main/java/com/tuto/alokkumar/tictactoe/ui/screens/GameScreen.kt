package com.tuto.alokkumar.tictactoe.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.BoardStyle
import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.GameState
import com.tuto.alokkumar.tictactoe.ui.components.AnimatedLinesBackground
import com.tuto.alokkumar.tictactoe.ui.components.GameBoard
import com.tuto.alokkumar.tictactoe.ui.components.GameInfoSection
import com.tuto.alokkumar.tictactoe.ui.components.MyIcons
import com.tuto.alokkumar.tictactoe.ui.components.PauseScreen
import com.tuto.alokkumar.tictactoe.ui.components.ScoreBoard
import com.tuto.alokkumar.tictactoe.ui.theme.TicTacToeTheme
import com.tuto.alokkumar.tictactoe.viewModel.GameViewModel
import com.tuto.alokkumar.tictactoe.viewModel.GameViewModelFactory

/**
 * Main game execution screen.
 *
 * Exposes core state bindings, handles system lifecycle hooks (automatically pauses gameplay
 * when the application is minimized or stopped in background), and overrides system back button presses.
 *
 * @param modifier Modifier applied to the outer layout container.
 * @param mode GameMode setting (PvP or AI difficulty settings).
 * @param boardSize Dimensions configuration.
 * @param loadHistory Optional saved history payload used to resume ongoing matches.
 * @param onHome Callback trigger to navigate back to Menu.
 * @param onSettings Callback trigger to navigate to Settings.
 * @param viewModel State holding view model instance.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    mode: GameMode = GameMode.PVP,
    boardSize: BoardSize = BoardSize(),
    loadHistory: GameHistory? = null,
    onHome: () -> Unit = {},
    onSettings: () -> Unit = {},
    viewModel: GameViewModel = viewModel(
        factory = GameViewModelFactory(mode, boardSize, loadHistory)
    ),
) {
    val state by viewModel.state.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val activeLayer by viewModel.activeLayer.collectAsState()
    val bgAnimationEnabled by viewModel.bgAnimationEnabled.collectAsState()
    val boardStyle by viewModel.boardStyle.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    BackHandler(enabled = true) {
        if (!isPaused) viewModel.pauseGame() else viewModel.resumeGame()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) viewModel.pauseGame()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    GameContent(
        state = state,
        isAiThinking = isAiThinking,
        isPaused = isPaused,
        activeLayer = activeLayer,
        onPause = viewModel::pauseGame,
        onResume = viewModel::resumeGame,
        onRestart = viewModel::restartGame,
        onSetLayer = viewModel::setLayer,
        onCellClick = viewModel::onCellClicked,
        onHome = { viewModel.saveHistory(); onHome() },
        onSettings = { viewModel.saveHistory(); onSettings() },
        bgAnimationEnabled = bgAnimationEnabled,
        boardStyle = boardStyle,
        modifier = modifier
    )
}

/**
 * Responsive rendering layout containing scoreboard metrics, turn updates, interactive game cells,
 * layer selectors, and floating pause trigger overlays.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameContent(
    state: GameState,
    isAiThinking: Boolean,
    isPaused: Boolean,
    activeLayer: Int,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onSetLayer: (Int) -> Unit,
    onCellClick: (Int) -> Unit,
    onHome: () -> Unit,
    onSettings: () -> Unit,
    bgAnimationEnabled: Boolean = false,
    boardStyle: BoardStyle = BoardStyle.CLASSIC,
    modifier: Modifier = Modifier
) {
    Scaffold { padding ->
        BoxWithConstraints(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (bgAnimationEnabled) {
                AnimatedLinesBackground(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    chainCount = 6,
                    baseSpeed = 0.5f
                )
            }

            val isLandscape = maxWidth > maxHeight
            if (isPaused) {
                PauseScreen(
                    visible = true,
                    onPlay = onResume,
                    onRestart = onRestart,
                    onHome = onHome,
                    onSettings = onSettings
                )
            } else {
                if (isLandscape) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(0.4f)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            ScoreBoard(
                                state.xWins,
                                state.oWins,
                                state.draws,
                                modifier = Modifier.widthIn(max = 360.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            GameInfoSection(
                                currentPlayer = state.currentPlayer,
                                winner = state.winner,
                                isAiThinking = isAiThinking,
                                onRestart = onRestart,
                                modifier = Modifier.widthIn(max = 360.dp)
                            )
                        }
                        
                        Column(
                            modifier = Modifier
                                .weight(0.6f)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (state.boardSize.z > 1) {
                                LayerSelector(
                                    count = state.boardSize.z,
                                    selected = activeLayer,
                                    lastMoveLayer = state.lastMove?.let { it / (state.boardSize.x * state.boardSize.y) },
                                    onSelect = onSetLayer
                                )
                            }
                            Box(modifier = Modifier.widthIn(max = 400.dp)) {
                                GameBoard(
                                    board = state.board,
                                    boardSize = state.boardSize,
                                    activeLayer = activeLayer,
                                    winLine = state.winLine,
                                    lastMove = state.lastMove,
                                    onCellClick = onCellClick,
                                    boardStyle = boardStyle,
                                    winner = state.winner
                                )
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
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
                            isAiThinking = isAiThinking,
                            onRestart = onRestart
                        )

                        if (state.boardSize.z > 1) {
                            LayerSelector(
                                count = state.boardSize.z,
                                selected = activeLayer,
                                lastMoveLayer = state.lastMove?.let { it / (state.boardSize.x * state.boardSize.y) },
                                onSelect = onSetLayer
                            )
                        }

                        Box(modifier = Modifier.widthIn(max = 500.dp)) {
                            GameBoard(
                                board = state.board,
                                boardSize = state.boardSize,
                                activeLayer = activeLayer,
                                winLine = state.winLine,
                                lastMove = state.lastMove,
                                onCellClick = onCellClick,
                                boardStyle = boardStyle,
                                winner = state.winner
                            )
                        }
                    }
                }
            }

            // Floating Pause Button (Moved to top of Z-order)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    IconButton(
                        onClick = { if (isPaused) onResume() else onPause() },
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else MyIcons.Pause,
                            contentDescription = if (isPaused) "Resume" else "Pause",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
        }
    }
}

/**
 * Multi-layer controller for 3D playboards.
 * Displays horizontal selection chips for low layer stacks, transitioning to vertical selector panels
 * with quick-jump shortcuts for higher numbers of layers.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayerSelector(
    count: Int,
    selected: Int,
    lastMoveLayer: Int? = null,
    onSelect: (Int) -> Unit
) {
    if (count <= 3) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(count) { i ->
                val isSelected = selected == i
                val isLastMoveLayer = lastMoveLayer == i

                FilterChip(
                    selected = isSelected,
                    onClick = { onSelect(i) },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Layer ${i + 1}")
                            if (isLastMoveLayer) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 4.dp)
                                        .size(8.dp)
                                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                                )
                            }
                        }
                    },
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else null,
                    modifier = Modifier.padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    } else {
        var showAllLayers by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Layer DOWN
            IconButton(
                onClick = { if (selected > 0) onSelect(selected - 1) },
                enabled = selected > 0
            ) {
                Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = "Layer Down")
            }

            // Central Info Button & List Opener
            Surface(
                onClick = { showAllLayers = !showAllLayers },
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Layer ${selected + 1} / $count",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Black
                            )
                            if (lastMoveLayer == selected) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 6.dp)
                                        .size(6.dp)
                                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(3.dp))
                                )
                            }
                        }
                        if (lastMoveLayer != null && lastMoveLayer != selected) {
                            Text(
                                text = "Last Played: L${lastMoveLayer + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Icon(MyIcons.Layers, contentDescription = "Layers List", modifier = Modifier.size(20.dp))
                }

                DropdownMenu(
                    expanded = showAllLayers,
                    onDismissRequest = { showAllLayers = false }
                ) {
                    repeat(count) { i ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Layer ${i + 1}",
                                        fontWeight = if(selected == i) FontWeight.Bold else FontWeight.Normal,
                                        color = if(selected == i) MaterialTheme.colorScheme.primary else Color.Unspecified
                                    )
                                    if (lastMoveLayer == i) {
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            "(Last played here)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            },
                            onClick = {
                                onSelect(i)
                                showAllLayers = false
                            }
                        )
                    }
                }
            }

            // Layer UP
            IconButton(
                onClick = { if (selected < count - 1) onSelect(selected + 1) },
                enabled = selected < count - 1
            ) {
                Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = "Layer Up")
            }

            // Quick Jump to Last Played
            AnimatedVisibility(
                visible = lastMoveLayer != null && lastMoveLayer != selected,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                IconButton(
                    onClick = { lastMoveLayer?.let { onSelect(it) } },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = MyIcons.JumpToLast,
                        contentDescription = "Jump to Last Played",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun GameScreenPortraitPreview() {
    TicTacToeTheme {
        GameContent(
            state = GameState(
                board = List(27) { if (it == 4) 'X' else if (it == 13) 'O' else null },
                boardSize = BoardSize(3, 3, 3),
                lastMove = 13
            ),
            isAiThinking = false,
            isPaused = false,
            activeLayer = 1,
            onPause = {},
            onResume = {},
            onRestart = {},
            onSetLayer = {},
            onCellClick = {},
            onHome = {},
            onSettings = {}
        )
    }
}

@Preview(showBackground = true, device = "spec:width=891dp,height=411dp,orientation=landscape")
@Composable
fun GameScreenLandscapePreview() {
    TicTacToeTheme {
        GameContent(
            state = GameState(
                board = List(27) { if (it == 4) 'X' else if (it == 13) 'O' else null },
                boardSize = BoardSize(3, 3, 3),
                lastMove = 13
            ),
            isAiThinking = true,
            isPaused = false,
            activeLayer = 1,
            onPause = {},
            onResume = {},
            onRestart = {},
            onSetLayer = {},
            onCellClick = {},
            onHome = {},
            onSettings = {}
        )
    }
}
