package com.tuto.alokkumar.tictactoe.ui.screens

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.BoardStyle
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
    onStartGame: () -> Unit,
    onViewStats: () -> Unit,
    onExit: () -> Unit,
    onAbout: () -> Unit = {},
    onSettings: () -> Unit,
    onPvpMode: () -> Unit = {},
    viewModel: MenuViewModel = viewModel()
) {
    val boardSize by viewModel.boardSize.collectAsState()
    val boardStyle by viewModel.boardStyle.collectAsState()

    MenuScreenContent(
        boardSize = boardSize,
        boardStyle = boardStyle,
        onStartGame = onStartGame,
        onViewStats = onViewStats,
        onExit = onExit,
        onAbout = onAbout,
        onSettings = onSettings,
        onPvpMode = onPvpMode,
        onBoardSizeChange = { size -> viewModel.setBoardSize(size) },
        onBoardStyleChange = { viewModel.setBoardStyle(it) }
    )
}

@Composable
fun MenuScreenContent(
    boardSize: BoardSize,
    boardStyle: BoardStyle,
    onStartGame: () -> Unit,
    onViewStats: () -> Unit,
    onExit: () -> Unit,
    onAbout: () -> Unit,
    onSettings: () -> Unit,
    onPvpMode: () -> Unit,
    onBoardSizeChange: (BoardSize) -> Unit,
    onBoardStyleChange: (BoardStyle) -> Unit
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
            title = { Text("Exit Game?") },
            text = { Text("Are you sure you want to quit Tic Tac Toe?") },
            confirmButton = {
                TextButton(onClick = { onExit() }) {
                    Text("Exit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.Transparent // Allow background to show
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column: Branding + Setup
                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tic Tac Toe",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        QuickSetupCard(
                            boardSize = boardSize,
                            boardStyle = boardStyle,
                            onBoardSizeChange = onBoardSizeChange,
                            onBoardStyleChange = onBoardStyleChange
                        )
                    }

                    // Right Column: Main Actions + Bottom Nav
                    Column(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        MainActionButtons(
                            onStartGame = onStartGame,
                            onPvpMode = onPvpMode
                        )

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
                        .background(MaterialTheme.colorScheme.background)
                        .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 36.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    Text(
                        text = "Tic Tac Toe",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    // Quick Setup Card
                    QuickSetupCard(
                        boardSize = boardSize,
                        boardStyle = boardStyle,
                        onBoardSizeChange = onBoardSizeChange,
                        onBoardStyleChange = onBoardStyleChange
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
    onBoardSizeChange: (BoardSize) -> Unit,
    onBoardStyleChange: (BoardStyle) -> Unit
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
                "Quick Setup",
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
                        label = "Rows",
                        value = boardSize.y,
                        onValueChange = { onBoardSizeChange(boardSize.copy(y = it)) },
                        range = 3..10,
                        modifier = Modifier.weight(1f)
                    )
                    NumberPicker(
                        label = "Cols",
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
                        label = "Layers",
                        value = boardSize.z,
                        onValueChange = { onBoardSizeChange(boardSize.copy(z = it)) },
                        range = 1..10,
                        modifier = Modifier.weight(1f)
                    )
                    NumberPicker(
                        label = "To Win",
                        value = boardSize.winCondition,
                        onValueChange = { onBoardSizeChange(boardSize.copy(winCondition = it)) },
                        range = minOf(3, maxOf(boardSize.x, boardSize.y, boardSize.z))..maxOf(boardSize.x, boardSize.y, boardSize.z),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            SettingSelector(
                title = "Style",
                dataList = BoardStyle.entries,
                selected = boardStyle,
                onItemSelect = { onBoardStyleChange(it as BoardStyle) }
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onStartGame,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("START", color = MaterialTheme.colorScheme.primary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onPvpMode) {
                Text("PVP", color = MaterialTheme.colorScheme.primary, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TextButton(onClick = onAbout) {
            Text(
                "ABOUT",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }
        TextButton(onClick = onSettings) {
            Text(
                "SETTINGS",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }
        TextButton(onClick = onViewStats) {
            Text(
                "HISTORY",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Preview(name = "Portrait", showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
private fun MenuPreviewPortrait() {
    MenuScreenContent(
        boardSize = BoardSize(),
        boardStyle = BoardStyle.LAYERED_3D,
        onStartGame = {},
        onViewStats = {},
        onExit = {},
        onAbout = {},
        onSettings = {},
        onPvpMode = {},
        onBoardSizeChange = {},
        onBoardStyleChange = {}
    )
}

@Preview(name = "Landscape", showBackground = true, device = "spec:width=891dp,height=411dp,orientation=landscape")
@Composable
private fun MenuPreviewLandscape() {
    MenuScreenContent(
        boardSize = BoardSize(),
        boardStyle = BoardStyle.LAYERED_3D,
        onStartGame = {},
        onViewStats = {},
        onExit = {},
        onAbout = {},
        onSettings = {},
        onPvpMode = {},
        onBoardSizeChange = {},
        onBoardStyleChange = {}
    )
}
