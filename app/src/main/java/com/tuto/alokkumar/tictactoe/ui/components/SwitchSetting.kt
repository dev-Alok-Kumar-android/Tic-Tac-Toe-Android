package com.tuto.alokkumar.tictactoe.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/**
 * Standard visual row layout combining a text label description and a Material binary switch.
 *
 * @param modifier Modifier applied to the horizontal row.
 * @param checked Checked active state of the switch.
 * @param onCheckedChange Callback event triggered when switch active state toggles.
 * @param text Descriptive name label printed in the row.
 */
@Composable
fun SwitchSetting(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Preview(showBackground = true)
@Composable
private fun SSPrev() {
    SwitchSetting(Modifier, true, {}, "Dark Theme")
}