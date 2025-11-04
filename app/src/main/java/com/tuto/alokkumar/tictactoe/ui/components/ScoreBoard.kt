package com.tuto.alokkumar.tictactoe.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScoreBoard(
    xWins: Int,
    oWins: Int,
    draws: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(12.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.large
            )
            .padding(vertical = 10.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScoreItem(title = "X Wins", score = xWins, color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.width(30.dp))
        ScoreItem(title = "Draws", score = draws, color = MaterialTheme.colorScheme.outline)
        Spacer(modifier = Modifier.width(30.dp))
        ScoreItem(title = "O Wins", score = oWins, color = MaterialTheme.colorScheme.tertiary)
    }
}

@Composable
private fun ScoreItem(title: String, score: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = score.toString(),
            color = color,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
