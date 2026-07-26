package com.tuto.alokkumar.tictactoe.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuto.alokkumar.tictactoe.R
import com.tuto.alokkumar.tictactoe.data.AiDifficulty
import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.GameStats
import com.tuto.alokkumar.tictactoe.ui.components.MyIcons
import com.tuto.alokkumar.tictactoe.viewModel.DifficultyFilter
import com.tuto.alokkumar.tictactoe.viewModel.HistoryFilter
import com.tuto.alokkumar.tictactoe.viewModel.HistoryViewModel
import com.tuto.alokkumar.tictactoe.viewModel.StatusFilter
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Screen rendering saved Tic Tac Toe match histories with advanced filtering and status badges.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onItemClick: (GameHistory) -> Unit,
) {
    val histories by viewModel.history.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val selectedItems by viewModel.selectedItems.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val statusFilter by viewModel.statusFilter.collectAsStateWithLifecycle()
    val difficultyFilter by viewModel.difficultyFilter.collectAsStateWithLifecycle()
    val isDatabaseEmpty by viewModel.isDatabaseEmpty.collectAsStateWithLifecycle()
    
    val isSelectionMode = selectedItems.isNotEmpty()
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    BackHandler(isSelectionMode) {
        viewModel.clearSelection()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    if (isSelectionMode) {
                        Text("${selectedItems.size} Selected")
                    } else {
                        Text(stringResource(R.string.history_title))
                    }
                },
                navigationIcon = {
                    if (isSelectionMode) {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(MyIcons.Close, contentDescription = "Clear Selection")
                        }
                    }
                },
                actions = {
                    if (isSelectionMode) {
                        IconButton(onClick = { viewModel.selectAll() }) {
                            Icon(MyIcons.Done, contentDescription = "Select All")
                        }
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(MyIcons.Delete, contentDescription = "Delete Selected", tint = MaterialTheme.colorScheme.error)
                        }
                    } else {
                        IconButton(onClick = { showFilterSheet = true }) {
                            val isFilterActive = statusFilter != StatusFilter.ALL || difficultyFilter != DifficultyFilter.ALL
                            BadgedBox(
                                badge = {
                                    if (isFilterActive) {
                                        Badge(containerColor = MaterialTheme.colorScheme.tertiary)
                                    }
                                }
                            ) {
                                Icon(MyIcons.Filter, contentDescription = "Filter")
                            }
                        }
                    }
                }
            )
        }) { inner ->

        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text(stringResource(R.string.clear_history_title)) },
                text = { Text(stringResource(R.string.delete_confirm_message, selectedItems.size)) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteSelected()
                        showDeleteConfirm = false
                    }) {
                        Text(stringResource(R.string.yes), color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text(stringResource(R.string.no))
                    }
                }
            )
        }

        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 48.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        stringResource(R.string.advanced_filters),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    AdvancedFiltersContent(
                        currentStatus = statusFilter,
                        onStatusSelected = { viewModel.setStatusFilter(it) },
                        currentDifficulty = difficultyFilter,
                        onDifficultySelected = { viewModel.setDifficultyFilter(it) },
                        showDifficulty = filter == HistoryFilter.VS_AI || filter == HistoryFilter.ALL
                    )

                    Button(
                        onClick = { showFilterSheet = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(stringResource(R.string.apply))
                    }
                }
            }
        }

        if (isDatabaseEmpty) {
            Box(
                Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.no_history))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(Modifier.height(16.dp))
                    StatsDashboard(stats)
                }

                item {
                    HistoryFilterBar(
                        selectedFilter = filter,
                        onFilterSelected = { viewModel.setFilter(it) }
                    )
                }

                if (histories.isEmpty() && filter != HistoryFilter.ALL) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(stringResource(R.string.no_matches_found), style = MaterialTheme.typography.bodyMedium)
                            TextButton(onClick = { viewModel.resetAllFilters() }) {
                                Text(stringResource(R.string.reset_filters))
                            }
                        }
                    }
                }

                items(histories, key = { it.matchId }) { item ->
                    val isSelected = selectedItems.contains(item)
                    
                    HistoryItem(
                        item,
                        isSelected = isSelected,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                            .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(12.dp))
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .combinedClickable(
                                onClick = { 
                                    if (isSelectionMode) viewModel.toggleSelection(item) else onItemClick(item) 
                                },
                                onLongClick = { viewModel.toggleSelection(item) })
                    )
                }
                
                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun StatsDashboard(stats: GameStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(label = stringResource(R.string.total_games), value = stats.totalGames.toString())
            StatItem(label = stringResource(R.string.win_rate), value = "${stats.winRate}%")
            StatItem(label = stringResource(R.string.draws), value = stats.draws.toString())
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
fun HistoryFilterBar(
    selectedFilter: HistoryFilter,
    onFilterSelected: (HistoryFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedFilter == HistoryFilter.ALL,
            onClick = { onFilterSelected(HistoryFilter.ALL) },
            label = { Text(stringResource(R.string.filter_all)) }
        )
        FilterChip(
            selected = selectedFilter == HistoryFilter.PVP,
            onClick = { onFilterSelected(HistoryFilter.PVP) },
            label = { Text(stringResource(R.string.filter_pvp)) }
        )
        FilterChip(
            selected = selectedFilter == HistoryFilter.VS_AI,
            onClick = { onFilterSelected(HistoryFilter.VS_AI) },
            label = { Text(stringResource(R.string.filter_ai)) }
        )
    }
}

@Composable
fun AdvancedFiltersContent(
    currentStatus: StatusFilter,
    onStatusSelected: (StatusFilter) -> Unit,
    currentDifficulty: DifficultyFilter,
    onDifficultySelected: (DifficultyFilter) -> Unit,
    showDifficulty: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        // Status Group
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.filter_status), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusFilter.entries.forEach { status ->
                    FilterChip(
                        selected = currentStatus == status,
                        onClick = { onStatusSelected(status) },
                        label = { 
                            Text(when(status) {
                                StatusFilter.ALL -> stringResource(R.string.filter_all)
                                StatusFilter.ONGOING -> stringResource(R.string.status_ongoing)
                                StatusFilter.FINISHED -> stringResource(R.string.status_finished)
                            })
                        }
                    )
                }
            }
        }

        // Difficulty Group
        if (showDifficulty) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.filter_difficulty), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DifficultyFilter.entries.forEach { diff ->
                        FilterChip(
                            selected = currentDifficulty == diff,
                            onClick = { onDifficultySelected(diff) },
                            label = { 
                                Text(when(diff) {
                                    DifficultyFilter.ALL -> stringResource(R.string.filter_all)
                                    DifficultyFilter.EASY -> stringResource(R.string.mode_easy)
                                    DifficultyFilter.MEDIUM -> stringResource(R.string.mode_medium)
                                    DifficultyFilter.HARD -> stringResource(R.string.mode_hard)
                                    DifficultyFilter.IMPOSSIBLE -> stringResource(R.string.mode_impossible)
                                })
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * List row item displaying singular history metrics and local dates.
 */
@Composable
fun HistoryItem(item: GameHistory, isSelected: Boolean, modifier: Modifier = Modifier) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val isOngoing = item.state.winner == null

    Column(modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically, 
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isSelected) MyIcons.Done else (if (item.gameMode == GameMode.PVP) MyIcons.Person else MyIcons.Face), 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.primary, 
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                
                val modeText = if (item.gameMode == GameMode.PVP) {
                    stringResource(R.string.mode_pvp)
                } else {
                    val modeRes = when(item.difficulty) {
                        AiDifficulty.EASY -> R.string.mode_easy
                        AiDifficulty.MEDIUM -> R.string.mode_medium
                        AiDifficulty.HARD -> R.string.mode_hard
                        AiDifficulty.IMPOSSIBLE -> R.string.mode_impossible
                    }
                    stringResource(modeRes)
                }
                
                Text(
                    text = modeText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isOngoing) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = stringResource(R.string.status_ongoing).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
        
        Spacer(Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "${item.p1Name} (${item.p1Symbol}): ${item.state.xWins}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(item.p1Color)
                )
                Text(
                    text = "${item.p2Name} (${item.p2Symbol}): ${item.state.oWins}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(item.p2Color)
                )
            }
            
            Text(
                text = stringResource(R.string.grid_size, "${item.state.boardSize.x}x${item.state.boardSize.y}"),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (isOngoing) {
             Text(
                text = stringResource(R.string.match_in_progress),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(Modifier.height(4.dp))
        Text(
            text = dateFormat.format(item.dateMillis),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}
