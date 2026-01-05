package com.tuto.alokkumar.tictactoe.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingSelector(
    modifier: Modifier = Modifier,
    title: String? = null,
    dataList: List<Any>,
    selected: Any,
    onItemSelect: (selected: Any) -> Unit,
) {
    val expanded = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded.value = !expanded.value }
                .padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(title ?: "", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(selected.toString(), style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    if (expanded.value) " ▲" else " ▼", style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        HorizontalDivider(thickness = if (title != null) 1.dp else 2.dp)

        AnimatedVisibility(expanded.value) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                dataList.forEach { data ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 12.dp)
                            .clickable {
                                onItemSelect(data)
                            }) {
                        RadioButton(selected = data == selected, onClick = {
                            onItemSelect(data)
                        })
                        Text(data.toString(), style = MaterialTheme.typography.bodyLarge)
                    }
                }
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun SsdPreview() {
    val selected = remember { mutableStateOf("") }
    SettingSelector(
        title = "Game Mode", dataList = List(5) { "Item ${it + 1}" }, selected = selected.value
    ) {
        selected.value = it as String
    }
}