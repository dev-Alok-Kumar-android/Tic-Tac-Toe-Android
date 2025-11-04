package com.tuto.alokkumar.tictactoe.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameBoard(board: List<Char?>, onCellClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        for (i in 0..2) {
            Row(horizontalArrangement = Arrangement.Center) {
                for (j in 0..2) {
                    val index = i * 3 + j
                    val cellValue = board[index]
                    val cellColor by animateColorAsState(
                        targetValue = when (cellValue) {
                            'X' -> MaterialTheme.colorScheme.secondary
                            'O' -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(6.dp)
                            .background(cellColor, RoundedCornerShape(16.dp))
                            .clickable(enabled = cellValue == null) { onCellClick(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cellValue?.toString() ?: "",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}