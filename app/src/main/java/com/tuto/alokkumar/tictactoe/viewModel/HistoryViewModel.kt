package com.tuto.alokkumar.tictactoe.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuto.alokkumar.tictactoe.core.pref.PreferencesManager
import com.tuto.alokkumar.tictactoe.data.GameHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val preferences: PreferencesManager
) : ViewModel() {

    val history = preferences.getGameHistoryFlow().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun clearHistory() {
        viewModelScope.launch {
            preferences.clearAllGameHistory()
        }
    }

    fun removeHistory(history: GameHistory) {
        viewModelScope.launch {
            preferences.removeGameHistory(history)
        }
    }
}
