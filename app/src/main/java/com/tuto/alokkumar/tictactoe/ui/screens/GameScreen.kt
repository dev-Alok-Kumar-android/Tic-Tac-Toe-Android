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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuto.alokkumar.tictactoe.R
import com.tuto.alokkumar.tictactoe.data.BoardStyle
import com.tuto.alokkumar.tictactoe.data.GameState
import com.tuto.alokkumar.tictactoe.ui.components.AnimatedLinesBackground
import com.tuto.alokkumar.tictactoe.ui.components.GameBoard
import com.tuto.alokkumar.tictactoe.ui.components.GameInfoSection
import com.tuto.alokkumar.tictactoe.ui.components.MyIcons
import com.tuto.alokkumar.tictactoe.ui.components.PauseScreen
import com.tuto.alokkumar.tictactoe.ui.components.ScoreBoard
import com.tuto.alokkumar.tictactoe.ui.components.VictoryEffects
import com.tuto.alokkumar.tictactoe.viewModel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    onHome: () -> Unit = {},
    onSettings: () -> Unit = {},
    viewModel: GameViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isOpponentThinking by viewModel.isOpponentThinking.collectAsStateWithLifecycle()
    val isPaused by viewModel.isPaused.collectAsStateWithLifecycle()
    val activeLayer by viewModel.activeLayer.collectAsStateWithLifecycle()
    val bgAnimationEnabled by viewModel.bgAnimationEnabled.collectAsStateWithLifecycle()
    val boardStyle by viewModel.boardStyle.collectAsStateWithLifecycle()
    
    val p1Symbol by viewModel.p1Symbol.collectAsStateWithLifecycle()
    val p2Symbol by viewModel.p2Symbol.collectAsStateWithLifecycle()
    val p1Color by viewModel.p1Color.collectAsStateWithLifecycle()
    val p2Color by viewModel.p2Color.collectAsStateWithLifecycle()
    val p1Name by viewModel.p1Name.collectAsStateWithLifecycle()
    val p2Name by viewModel.p2Name.collectAsStateWithLifecycle()
    val humanSymbol by viewModel.humanSymbol.collectAsStateWithLifecycle()

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
        isOpponentThinking = isOpponentThinking,
        isPaused = isPaused,
        isVsAI = viewModel.isVsAI,
        humanSymbol = humanSymbol,
        p1Symbol = p1Symbol,
        p2Symbol = p2Symbol,
        p1Color = p1Color,
        p2Color = p2Color,
        p1Name = p1Name,
        p2Name = p2Name,
        activeLayer = activeLayer,
        onPause = viewModel::pauseGame,
        onResume = viewModel::resumeGame,
        onRestart = viewModel::restartGame,
        onSetLayer = viewModel::setLayer,
        onCellClick = viewModel::onCellClicked,
        onHome = { onHome() },
        onSettings = { onSettings() },
        bgAnimationEnabled = bgAnimationEnabled,
        boardStyle = boardStyle,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameContent(
    state: GameState,
    isOpponentThinking: Boolean,
    isPaused: Boolean,
    isVsAI: Boolean,
    humanSymbol: String,
    p1Symbol: String,
    p2Symbol: String,
    p1Color: Long,
    p2Color: Long,
    p1Name: String,
    p2Name: String,
    activeLayer: Int,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onSetLayer: (Int) -> Unit,
    onCellClick: (Int) -> Unit,
    onHome: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
    bgAnimationEnabled: Boolean = false,
    boardStyle: BoardStyle = BoardStyle.CLASSIC
) {
    Scaffold { padding ->
        BoxWithConstraints(
            contentAlignment = Alignment.Center,
            modifier = modifier.fillMaxSize().padding(padding)
        ) {
            if (bgAnimationEnabled) {
                AnimatedLinesBackground(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    chainCount = 6,
                    baseSpeed = 0.5f
                )
            }

            if (state.winner != null && state.winner != "D") {
                VictoryEffects()
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
                val isAiTurn = isVsAI && state.currentPlayer != humanSymbol && state.winner == null
                
                if (isLandscape) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(0.4f).verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            ScoreBoard(
                                xWins = state.xWins,
                                oWins = state.oWins,
                                draws = state.draws,
                                p1Name = p1Name,
                                p2Name = p2Name,
                                p1Symbol = p1Symbol,
                                p2Symbol = p2Symbol,
                                isPlayerOAI = isVsAI && humanSymbol == p1Symbol,
                                modifier = Modifier.widthIn(max = 360.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            GameInfoSection(
                                currentPlayerSymbol = state.currentPlayer,
                                winnerSymbol = state.winner,
                                isAiTurn = isAiTurn,
                                p1Name = p1Name,
                                p2Name = p2Name,
                                p1Symbol = p1Symbol,
                                p2Symbol = p2Symbol,
                                onRestart = onRestart,
                                modifier = Modifier.widthIn(max = 360.dp)
                            )
                        }
                        
                        Column(
                            modifier = Modifier.weight(0.6f).verticalScroll(rememberScrollState()),
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
                                    p1Symbol = p1Symbol,
                                    p2Symbol = p2Symbol,
                                    p1Color = Color(p1Color),
                                    p2Color = Color(p2Color),
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
                        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ScoreBoard(
                            xWins = state.xWins,
                            oWins = state.oWins,
                            draws = state.draws,
                            p1Name = p1Name,
                            p2Name = p2Name,
                            p1Symbol = p1Symbol,
                            p2Symbol = p2Symbol,
                            isPlayerOAI = isVsAI && humanSymbol == p1Symbol,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        GameInfoSection(
                            currentPlayerSymbol = state.currentPlayer,
                            winnerSymbol = state.winner,
                            isAiTurn = isAiTurn,
                            p1Name = p1Name,
                            p2Name = p2Name,
                            p1Symbol = p1Symbol,
                            p2Symbol = p2Symbol,
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
                                p1Symbol = p1Symbol,
                                p2Symbol = p2Symbol,
                                p1Color = Color(p1Color),
                                p2Color = Color(p2Color),
                                lastMove = state.lastMove,
                                onCellClick = onCellClick,
                                boardStyle = boardStyle,
                                winner = state.winner
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = { if (isPaused) onResume() else onPause() },
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        RoundedCornerShape(12.dp)
                    )
                ) {
                    Icon(
                        imageVector = if (isPaused) MyIcons.PlayArrow else MyIcons.Pause,
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
                                imageVector = MyIcons.PlayArrow,
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
                Icon(MyIcons.KeyboardArrowDown, contentDescription = stringResource(R.string.layer_down))
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
                    Icon(MyIcons.Layers, contentDescription = stringResource(R.string.layers_list), modifier = Modifier.size(20.dp))
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
                Icon(MyIcons.KeyboardArrowUp, contentDescription = stringResource(R.string.layer_up))
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
                        contentDescription = stringResource(R.string.jump_to_last),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
