package com.tuto.alokkumar.tictactoe.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Scoreboard indicator showing current game session metrics.
 *
 * Exposes player X wins, draws, and player O wins side-by-side inside structured cards.
 *
 * @param xWins Number of wins achieved by Player 'X'.
 * @param oWins Number of wins achieved by Player 'O'.
 * @param draws Number of draws/ties.
 * @param modifier Modifier applied to the parent Card container.
 */
@Composable
fun ScoreBoard(
    xWins: Int,
    oWins: Int,
    draws: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(12.dp)
            .shadow(12.dp, MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScoreItem(
                title = "Player X",
                score = xWins,
                color = MaterialTheme.colorScheme.secondary,
                icon = Icons.Default.Person
            )
            
            VerticalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            
            ScoreItem(
                title = "Draws",
                score = draws,
                color = MaterialTheme.colorScheme.outline,
                icon = Icons.Default.Star
            )
            
            VerticalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            
            ScoreItem(
                title = "Player O",
                score = oWins,
                color = MaterialTheme.colorScheme.tertiary,
                icon = Icons.Default.Face
            )
        }
    }
}

/**
 * Individual score metric item representing a participant or ties.
 */
@Composable
private fun ScoreItem(title: String, score: Int, color: Color, icon: ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color.copy(alpha = 0.8f),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = score.toString(),
            color = color,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.displaySmall
        )
    }
}
