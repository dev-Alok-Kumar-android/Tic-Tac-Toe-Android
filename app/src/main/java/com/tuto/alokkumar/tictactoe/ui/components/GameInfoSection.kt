package com.tuto.alokkumar.tictactoe.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun GameInfoSection(
    currentPlayer: Char,
    winner: Char?,
    modifier: Modifier = Modifier,
    onRestart: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimatedVisibility(visible = winner == null) {
            Text(
                text = "Turn: $currentPlayer",
                style = MaterialTheme.typography.titleLarge
            )
        }
        AnimatedVisibility(visible = winner != null) {
            Text(
                text = if (winner == 'D') "It's a Draw!" else "Winner: ${winner?: "🧐"} 🎉",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Button(
            onClick = onRestart,
            shape = MaterialTheme.shapes.large
        ) {
            Text("Restart", style = MaterialTheme.typography.bodyLarge)
        }
    }
}