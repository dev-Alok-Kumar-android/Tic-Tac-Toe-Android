package com.tuto.alokkumar.tictactoe.ui.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuto.alokkumar.tictactoe.R
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.BoardStyle
import com.tuto.alokkumar.tictactoe.data.FirstMoveBehavior
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.ui.components.MyIcons
import com.tuto.alokkumar.tictactoe.ui.components.NumberPicker
import com.tuto.alokkumar.tictactoe.ui.components.SettingSelector
import com.tuto.alokkumar.tictactoe.viewModel.MenuViewModel

/**
 * Main application landing menu page displaying navigation buttons to different modes and panels.
 *
 * Implements double-back-tap confirmation and custom [AlertDialog] prompts to securely handle exit requests.
 */
@Composable
fun MenuScreen(
    onStartGame: (GameMode, BoardSize) -> Unit,
    onViewStats: () -> Unit,
    onExit: () -> Unit,
    onAbout: () -> Unit = {},
    onSettings: () -> Unit,
    onPvpMode: (BoardSize) -> Unit,
    viewModel: MenuViewModel = hiltViewModel()
) {
    val boardSize by viewModel.boardSize.collectAsStateWithLifecycle()
    val boardStyle by viewModel.boardStyle.collectAsStateWithLifecycle()
    val gameMode by viewModel.gameMode.collectAsStateWithLifecycle()
    val firstMoveBehavior by viewModel.firstMoveBehavior.collectAsStateWithLifecycle()
    val aiStrength by viewModel.aiStrength.collectAsStateWithLifecycle()

    MenuScreenContent(
        boardSize = boardSize,
        boardStyle = boardStyle,
        gameMode = gameMode,
        firstMoveBehavior = firstMoveBehavior,
        aiStrength = aiStrength,
        onStartGame = { onStartGame(gameMode, boardSize) },
        onViewStats = onViewStats,
        onExit = onExit,
        onAbout = onAbout,
        onSettings = onSettings,
        onPvpMode = { onPvpMode(boardSize) },
        onBoardSizeChange = { size -> viewModel.setBoardSize(size) },
        onBoardStyleChange = { viewModel.setBoardStyle(it) },
        onGameModeChange = { viewModel.setGameMode(it) },
        onFirstMoveBehaviorChange = { viewModel.setFirstMoveBehavior(it) }
    )
}

@Composable
fun MenuScreenContent(
    boardSize: BoardSize,
    boardStyle: BoardStyle,
    gameMode: GameMode,
    firstMoveBehavior: FirstMoveBehavior,
    aiStrength: Int,
    onStartGame: () -> Unit,
    onViewStats: () -> Unit,
    onExit: () -> Unit,
    onAbout: () -> Unit,
    onSettings: () -> Unit,
    onPvpMode: () -> Unit,
    onBoardSizeChange: (BoardSize) -> Unit,
    onBoardStyleChange: (BoardStyle) -> Unit,
    onGameModeChange: (GameMode) -> Unit,
    onFirstMoveBehaviorChange: (FirstMoveBehavior) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var showExitDialog by remember { mutableStateOf(false) }
    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    BackHandler {
        if (showExitDialog) {
            onExit()
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastBackPressTime < 1200) {
                onExit()
            } else {
                showExitDialog = true
                lastBackPressTime = currentTime
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(stringResource(R.string.exit_game_title)) },
            text = { Text(stringResource(R.string.exit_game_message)) },
            confirmButton = {
                TextButton(onClick = { onExit() }) {
                    Text(stringResource(R.string.exit_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column: Branding + Setup
                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tic Tac Toe",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        QuickSetupCard(
                            boardSize = boardSize,
                            boardStyle = boardStyle,
                            selectedGameMode = gameMode,
                            firstMoveBehavior = firstMoveBehavior,
                            aiStrength = aiStrength,
                            onBoardSizeChange = onBoardSizeChange,
                            onBoardStyleChange = onBoardStyleChange,
                            onGameModeChange = onGameModeChange,
                            onFirstMoveBehaviorChange = onFirstMoveBehaviorChange
                        )
                    }

                    // Right Column: Main Actions + Bottom Nav
                    Column(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        MainActionButtons(
                            onStartGame = onStartGame,
                            onPvpMode = onPvpMode
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        BottomNavigation(
                            onAbout = onAbout,
                            onSettings = onSettings,
                            onViewStats = onViewStats
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(top = 32.dp, start = 24.dp, end = 24.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.app_name).uppercase(),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            letterSpacing = 4.sp
                        )
                        Text(
                            text = stringResource(R.string.edition_subtitle),
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }

                    // Quick Setup Card
                    QuickSetupCard(
                        boardSize = boardSize,
                        boardStyle = boardStyle,
                        selectedGameMode = gameMode,
                        firstMoveBehavior = firstMoveBehavior,
                        aiStrength = aiStrength,
                        onBoardSizeChange = onBoardSizeChange,
                        onBoardStyleChange = onBoardStyleChange,
                        onGameModeChange = onGameModeChange,
                        onFirstMoveBehaviorChange = onFirstMoveBehaviorChange
                    )

                    // Main action buttons
                    MainActionButtons(
                        onStartGame = onStartGame,
                        onPvpMode = onPvpMode
                    )

                    // Bottom navigation
                    BottomNavigation(
                        onAbout = onAbout,
                        onSettings = onSettings,
                        onViewStats = onViewStats
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickSetupCard(
    boardSize: BoardSize,
    boardStyle: BoardStyle,
    selectedGameMode: GameMode,
    firstMoveBehavior: FirstMoveBehavior,
    aiStrength: Int,
    onBoardSizeChange: (BoardSize) -> Unit,
    onBoardStyleChange: (BoardStyle) -> Unit,
    onGameModeChange: (GameMode) -> Unit,
    onFirstMoveBehaviorChange: (FirstMoveBehavior) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                stringResource(R.string.quick_setup),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NumberPicker(
                        label = stringResource(R.string.rows),
                        value = boardSize.y,
                        onValueChange = { onBoardSizeChange(boardSize.copy(y = it)) },
                        range = 3..10,
                        modifier = Modifier.weight(1f)
                    )
                    NumberPicker(
                        label = stringResource(R.string.cols),
                        value = boardSize.x,
                        onValueChange = { onBoardSizeChange(boardSize.copy(x = it)) },
                        range = 3..10,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NumberPicker(
                        label = stringResource(R.string.layers),
                        value = boardSize.z,
                        onValueChange = { onBoardSizeChange(boardSize.copy(z = it)) },
                        range = 1..10,
                        modifier = Modifier.weight(1f)
                    )
                    NumberPicker(
                        label = stringResource(R.string.to_win),
                        value = boardSize.winCondition,
                        onValueChange = { onBoardSizeChange(boardSize.copy(winCondition = it)) },
                        range = 3..maxOf(3, maxOf(boardSize.x, boardSize.y, boardSize.z)),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (selectedGameMode == GameMode.HARD || selectedGameMode == GameMode.IMPOSSIBLE) {
                 val strengthText = if (selectedGameMode == GameMode.IMPOSSIBLE) "100" else aiStrength.toString()
                 Text(
                    text = stringResource(R.string.ai_skill_level, strengthText.toInt()),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            SettingSelector(
                title = stringResource(R.string.style),
                dataList = BoardStyle.entries,
                selected = boardStyle,
                onItemSelect = { onBoardStyleChange(it as BoardStyle) }
            )

            SettingSelector(
                title = stringResource(R.string.difficulty),
                dataList = GameMode.entries,
                selected = selectedGameMode,
                onItemSelect = { onGameModeChange(it as GameMode) }
            )

            SettingSelector(
                title = stringResource(R.string.first_move),
                dataList = FirstMoveBehavior.entries,
                selected = firstMoveBehavior,
                onItemSelect = { onFirstMoveBehaviorChange(it as FirstMoveBehavior) }
            )
        }
    }
}

@Composable
private fun MainActionButtons(
    onStartGame: () -> Unit,
    onPvpMode: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onStartGame,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(MyIcons.PlayArrow, contentDescription = null)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.play_vs_ai), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = onPvpMode,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Text(stringResource(R.string.play_vs_player), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BottomNavigation(
    onAbout: () -> Unit,
    onSettings: () -> Unit,
    onViewStats: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigationItem(icon = MyIcons.History, label = stringResource(R.string.history), onClick = onViewStats)
        NavigationItem(icon = MyIcons.Settings, label = stringResource(R.string.settings), onClick = onSettings)
        NavigationItem(icon = MyIcons.Info, label = stringResource(R.string.about), onClick = onAbout)
    }
}

@Composable
private fun NavigationItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .height(56.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.height(24.dp)
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(name = "Portrait", showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
private fun MenuPreviewPortrait() {
    MenuScreenContent(
        boardSize = BoardSize(),
        boardStyle = BoardStyle.LAYERED_3D,
        gameMode = GameMode.HARD,
        firstMoveBehavior = FirstMoveBehavior.PLAYER_X,
        aiStrength = 75,
        onStartGame = {},
        onViewStats = {},
        onExit = {},
        onAbout = {},
        onSettings = {},
        onPvpMode = {},
        onBoardSizeChange = {},
        onBoardStyleChange = {},
        onGameModeChange = {},
        onFirstMoveBehaviorChange = {}
    )
}

@Preview(name = "Landscape", showBackground = true, device = "spec:width=891dp,height=411dp,orientation=landscape")
@Composable
private fun MenuPreviewLandscape() {
    MenuScreenContent(
        boardSize = BoardSize(),
        boardStyle = BoardStyle.LAYERED_3D,
        gameMode = GameMode.HARD,
        firstMoveBehavior = FirstMoveBehavior.PLAYER_X,
        aiStrength = 75,
        onStartGame = {},
        onViewStats = {},
        onExit = {},
        onAbout = {},
        onSettings = {},
        onPvpMode = {},
        onBoardSizeChange = {},
        onBoardStyleChange = {},
        onGameModeChange = {},
        onFirstMoveBehaviorChange = {}
    )
}
