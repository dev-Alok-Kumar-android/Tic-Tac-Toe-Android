package com.tuto.alokkumar.tictactoe.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuto.alokkumar.tictactoe.R

/**
 * Renders game status announcements including active turns, win/draw banners.
 */
@Composable
fun GameInfoSection(
    currentPlayerSymbol: String,
    winnerSymbol: String?,
    isAiTurn: Boolean,
    p1Name: String,
    p2Name: String,
    p1Symbol: String,
    p2Symbol: String,
    modifier: Modifier = Modifier,
    onRestart: () -> Unit
) {
    val currentPlayerName = remember(currentPlayerSymbol, p1Symbol, p2Symbol, p1Name, p2Name) {
        if (currentPlayerSymbol == p1Symbol) p1Name else p2Name
    }
    
    val winnerName = remember(winnerSymbol, p1Symbol, p2Symbol, p1Name, p2Name) {
        if (winnerSymbol == p1Symbol) p1Name else if (winnerSymbol == p2Symbol) p2Name else winnerSymbol
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimatedVisibility(visible = winnerSymbol == null) {
            val turnText = if (isAiTurn) {
                stringResource(R.string.opponent_thinking)
            } else {
                stringResource(R.string.turn_label, "$currentPlayerName ($currentPlayerSymbol)")
            }
            Text(
                text = turnText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        AnimatedVisibility(visible = winnerSymbol != null) {
            val winText = if (winnerSymbol == "D") {
                stringResource(R.string.draw_message)
            } else {
                stringResource(R.string.winner_label, "$winnerName ($winnerSymbol)")
            }
            Text(
                text = winText,
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
            Text(stringResource(R.string.restart_match), style = MaterialTheme.typography.bodyLarge)
        }
    }
}
