package com.tuto.alokkumar.tictactoe.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuScreen(
    onStartGame: () -> Unit,
    onStartGame3D: () -> Unit,
    onViewStats: () -> Unit,
    onExit: () -> Unit,
    onAbout: () -> Unit = {},
    onSettings: () -> Unit,
    onPvpMode: () -> Unit = {}
) {
    var showExitDialog by remember { mutableStateOf(false) }
    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    BackHandler {
        onExit
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
            text = "Tic Tac Toe Game",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 60.dp)
        )

        // Main action buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onStartGame,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(80.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("START", color = MaterialTheme.colorScheme.primary, fontSize = 28.sp)
            }

            Row {
                TextButton(onClick = onPvpMode) {
                    Text("PVP", color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                }

                TextButton(onClick = onStartGame3D) {
                    Text("3D", color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                }
            }
        }

        // Bottom navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextButton(onClick = onAbout) {
                Text(
                    "ABOUT",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            TextButton(onClick = onSettings) {
                Text(
                    "SETTINGS",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            TextButton(onClick = onViewStats) {
                Text(
                    "HISTORY",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MenuPrev() {
    MenuScreen(
        onStartGame = {},
        onStartGame3D = {},
        onViewStats = {},
        onExit = {},
        onAbout = {},
        onSettings = {},
        onPvpMode = {}
    )
    
}