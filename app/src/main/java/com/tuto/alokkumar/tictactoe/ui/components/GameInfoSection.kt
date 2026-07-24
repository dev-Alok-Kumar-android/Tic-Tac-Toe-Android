package com.tuto.alokkumar.tictactoe.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Renders game status announcements including active turns, win/draw banners,
 * AI thinking progress loaders, and a restart game action button.
 *
 * @param currentPlayer Character token indicating whose turn it currently is ('X' or 'O').
 * @param winner Flat indicator representing final match results: 'X', 'O', 'D' (Draw), or null if match is active.
 * @param isAiThinking Flag indicating if the background thread is calculating an AI move.
 * @param modifier Modifier applied to the parent column container.
 * @param onRestart Callback event triggered when tapping the restart button.
 */
@Composable
fun GameInfoSection(
    currentPlayer: Char,
    winner: Char?,
    isAiThinking: Boolean,
    modifier: Modifier = Modifier,
    onRestart: () -> Unit
) {
    Column(
        modifier = modifier,
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


        AnimatedVisibility(isAiThinking) {
            Text(
                "Thinking...",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(12.dp)
            )
        }

    }
}