package com.tuto.alokkumar.tictactoe.ui.screens

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.viewModel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Screen rendering saved Tic Tac Toe match histories.
 *
 * Displays match statistics, timestamps, and game difficulty modes. Long-pressing items opens
 * deletion confirmation alerts.
 *
 * @param viewModel Attached history state view model.
 * @param onItemClick Callback action resuming/viewing selected history snapshot.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onItemClick: (GameHistory) -> Unit,
) {
    val histories by viewModel.history.collectAsStateWithLifecycle()
    var showDialogItem by remember { mutableStateOf<GameHistory?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Game History") }, actions = {
                if (histories.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearHistory() }) {
                        Text("Clear All", color = MaterialTheme.colorScheme.error)
                    }
                }
            })
        }) { inner ->

        if (showDialogItem != null) {
            AlertDialog(
                onDismissRequest = { showDialogItem = null },
                title = { Text("Clear History") },
                text = { Text("Are you sure you want to clear this entry?") },
                confirmButton = {
                    TextButton(onClick = {
                        val item = showDialogItem ?: return@TextButton
                        viewModel.removeHistory(item)
                        showDialogItem = null
                    }) {
                        Text("Yes", color = MaterialTheme.colorScheme.error)
                    }
                    TextButton(onClick = { showDialogItem = null }) {
                        Text("No", color = MaterialTheme.colorScheme.onSurface)
                    }
                })
        }

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
                    HistoryItem(
                        item,
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = { onItemClick(item) },
                                onLongClick = { showDialogItem = item })
                    )
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                }
            }
        }
    }
}

/**
 * List row item displaying singular history metrics and local dates.
 */
@Composable
fun HistoryItem(item: GameHistory, modifier: Modifier = Modifier) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    Column(modifier.padding(8.dp)) {
        Text("Mode: ${item.mode}", style = MaterialTheme.typography.bodyLarge)
        Text(
            "Result:\n\t X Wins: ${item.state.xWins}\n\t O Wins: ${item.state.oWins}\n\t Draws: ${item.state.draws}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            "Date: ${dateFormat.format(item.dateMillis)}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
