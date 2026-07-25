package com.tuto.alokkumar.tictactoe.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tuto.alokkumar.tictactoe.R

/**
 * Card based range controller that allows incrementing or decrementing integers.
 *
 * Exposes a plus/minus horizontal button layout, validating boundaries reactively.
 *
 * @param label Text indicating what preference is being configured.
 * @param value Currently selected number.
 * @param onValueChange Callback triggered when the value increases or decreases.
 * @param range IntRange constraints defining lower and upper boundaries.
 * @param isError If true, highlights the card with an error color.
 * @param modifier Modifier applied to the parent outlined card.
 */
@Composable
fun NumberPicker(
    label: String,
    value: Int,
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit,
    range: IntRange = 1..10,
    isError: Boolean = false,
) {
    val borderColor by animateColorAsState(
        targetValue = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
        label = "borderColor"
    )

    val containerColor by animateColorAsState(
        targetValue = if (isError) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
        label = "containerColor"
    )

    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.outlinedCardColors(containerColor = containerColor),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(borderColor))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { if (value > range.first) onValueChange(value - 1) },
                    enabled = value > range.first
                ) {
                    Icon(MyIcons.Remove, contentDescription = stringResource(R.string.decrease))
                }
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = if (isError) androidx.compose.ui.text.font.FontWeight.ExtraBold else null,
                    color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                IconButton(
                    onClick = { if (value < range.last) onValueChange(value + 1) },
                    enabled = value < range.last
                ) {
                    Icon(MyIcons.Add, contentDescription = stringResource(R.string.increase))
                }
            }
        }
    }
}
