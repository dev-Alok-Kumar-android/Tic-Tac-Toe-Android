package com.tuto.alokkumar.tictactoe.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuto.alokkumar.tictactoe.data.AiDifficulty
import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.GameStats
import com.tuto.alokkumar.tictactoe.domain.usecase.ManageHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Filter categories for history matches.
 */
enum class HistoryFilter { ALL, PVP, VS_AI }
enum class StatusFilter { ALL, ONGOING, FINISHED }
enum class DifficultyFilter { ALL, EASY, MEDIUM, HARD, IMPOSSIBLE }

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val manageHistoryUseCase: ManageHistoryUseCase,
) : ViewModel() {

    private val _selectedItems = MutableStateFlow<Set<GameHistory>>(emptySet())
    val selectedItems = _selectedItems.asStateFlow()

    private val _filter = MutableStateFlow(HistoryFilter.ALL)
    val filter = _filter.asStateFlow()

    private val _statusFilter = MutableStateFlow(StatusFilter.ALL)
    val statusFilter = _statusFilter.asStateFlow()

    private val _difficultyFilter = MutableStateFlow(DifficultyFilter.ALL)
    val difficultyFilter = _difficultyFilter.asStateFlow()

    private val _history = manageHistoryUseCase.gameHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val isDatabaseEmpty = _history.map { it.isEmpty() }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val history = combine(_history, _filter, _statusFilter, _difficultyFilter) { list, f, s, d ->
        list.filter { item ->
            val matchTypeMatch = when (f) {
                HistoryFilter.ALL -> true
                HistoryFilter.PVP -> item.gameMode == GameMode.PVP
                HistoryFilter.VS_AI -> item.gameMode == GameMode.VS_AI
            }
            
            val statusMatch = when (s) {
                StatusFilter.ALL -> true
                StatusFilter.ONGOING -> item.state.winnerId == null && !item.state.isDraw
                StatusFilter.FINISHED -> item.state.winnerId != null || item.state.isDraw
            }
            
            val difficultyMatch = if (f == HistoryFilter.PVP) true else {
                when (d) {
                    DifficultyFilter.ALL -> true
                    DifficultyFilter.EASY -> item.difficulty == AiDifficulty.EASY
                    DifficultyFilter.MEDIUM -> item.difficulty == AiDifficulty.MEDIUM
                    DifficultyFilter.HARD -> item.difficulty == AiDifficulty.HARD
                    DifficultyFilter.IMPOSSIBLE -> item.difficulty == AiDifficulty.IMPOSSIBLE
                }
            }
            
            matchTypeMatch && statusMatch && difficultyMatch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stats = history.map { list ->
        manageHistoryUseCase.getStats(list)
    }.flowOn(kotlinx.coroutines.Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GameStats())

    fun setFilter(newFilter: HistoryFilter) {
        _filter.value = newFilter
        clearSelection()
    }

    fun setStatusFilter(newFilter: StatusFilter) {
        _statusFilter.value = newFilter
        clearSelection()
    }

    fun setDifficultyFilter(newFilter: DifficultyFilter) {
        _difficultyFilter.value = newFilter
        clearSelection()
    }

    fun resetAllFilters() {
        _filter.value = HistoryFilter.ALL
        _statusFilter.value = StatusFilter.ALL
        _difficultyFilter.value = DifficultyFilter.ALL
        clearSelection()
    }

    @Suppress("unused")
    fun clearHistory() {
        viewModelScope.launch {
            manageHistoryUseCase.clearHistory()
        }
    }

    @Suppress("unused")
    fun removeHistory(history: GameHistory) {
        viewModelScope.launch {
            manageHistoryUseCase.deleteGames(listOf(history.matchId))
        }
    }

    fun toggleSelection(item: GameHistory) {
        val current = _selectedItems.value
        _selectedItems.value = if (current.contains(item)) current - item else current + item
    }

    fun clearSelection() {
        _selectedItems.value = emptySet()
    }

    fun selectAll() {
        _selectedItems.value = history.value.toSet()
    }

    fun deleteSelected() {
        viewModelScope.launch {
            val ids = _selectedItems.value.map { it.matchId }
            manageHistoryUseCase.deleteGames(ids)
            clearSelection()
        }
    }
}
