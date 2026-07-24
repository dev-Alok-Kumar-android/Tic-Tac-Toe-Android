package com.tuto.alokkumar.tictactoe.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Card based range controller that allows incrementing or decrementing integers.
 *
 * Exposes a plus/minus horizontal button layout, validating boundaries reactively.
 *
 * @param label Text indicating what preference is being configured.
 * @param value Currently selected number.
 * @param onValueChange Callback triggered when the value increases or decreases.
 * @param range IntRange constraints defining lower and upper boundaries.
 * @param modifier Modifier applied to the parent outlined card.
 */
@Composable
fun NumberPicker(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange = 1..10,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelMedium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { if (value > range.first) onValueChange(value - 1) },
                    enabled = value > range.first
                ) {
                    Icon(Icons.Default.Clear, contentDescription = "Decrease")
                }
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                IconButton(
                    onClick = { if (value < range.last) onValueChange(value + 1) },
                    enabled = value < range.last
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase")
                }
            }
        }
    }
}
