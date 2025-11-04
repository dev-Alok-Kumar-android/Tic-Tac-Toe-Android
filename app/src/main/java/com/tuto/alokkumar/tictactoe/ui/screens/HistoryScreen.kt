package com.tuto.alokkumar.tictactoe.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tuto.alokkumar.tictactoe.data.GameHistory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    histories: List<GameHistory>,
    onClear: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Game History") },
                actions = {
                    if (histories.isNotEmpty()) {
                        TextButton(onClick = onClear) {
                            Text("Clear", color = MaterialTheme.colorScheme.error)
                        }
                    }
                })
        }
    ) { inner ->
        if (histories.isEmpty()) {
            Box(
                Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("No history yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(histories) { item ->
                    HistoryItem(item)
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                }
            }
        }
    }
}

@Composable
fun HistoryItem(item: GameHistory) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    Column(Modifier.padding(8.dp)) {
        Text("Mode: ${item.mode}", style = MaterialTheme.typography.bodyLarge)
        Text("Result:\n\t X Wins: ${item.state.xWins}\n\t O Wins: ${item.state.oWins}\n\t Draws: ${item.state.draws}", style = MaterialTheme.typography.bodyLarge)
        Text("Date: ${dateFormat.format(item.dateMillis)}", style = MaterialTheme.typography.bodySmall)
    }
}
