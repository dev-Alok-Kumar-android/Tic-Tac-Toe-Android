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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Renders game status announcements including active turns, win/draw banners,
 * opponent thinking progress loaders, and a restart game action button.
 *
 * @param currentPlayer Character token indicating whose turn it currently is ('X' or 'O').
 * @param winner Flat indicator representing final match results: 'X', 'O', 'D' (Draw), or null if match is active.
 * @param isOpponentThinking Flag indicating if the background thread is calculating a move.
 * @param isPlayerOAI Flag indicating if Player O is an AI opponent.
 * @param modifier Modifier applied to the parent column container.
 * @param onRestart Callback event triggered when tapping the restart button.
 */
@Composable
fun GameInfoSection(
    currentPlayer: Char,
    winner: Char?,
    isOpponentThinking: Boolean,
    isPlayerOAI: Boolean,
    modifier: Modifier = Modifier,
    onRestart: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimatedVisibility(visible = winner == null) {
            val turnText = if (isPlayerOAI && currentPlayer == 'O') "Opponent Thinking..." else "Turn: $currentPlayer"
            Text(
                text = turnText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        AnimatedVisibility(visible = winner != null) {
            Text(
                text = if (winner == 'D') "It's a Draw!" else "Winner: ${winner?: "🧐"} 🎉",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Black
            )
        }

        Button(
            onClick = onRestart,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Restart Match", style = MaterialTheme.typography.bodyLarge)
        }

        AnimatedVisibility(isOpponentThinking) {
            Text(
                "Analyzing...",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(12.dp),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
