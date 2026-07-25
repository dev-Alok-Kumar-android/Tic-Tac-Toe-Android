package com.tuto.alokkumar.tictactoe.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuto.alokkumar.tictactoe.core.pref.PreferencesManager
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.BoardStyle
import com.tuto.alokkumar.tictactoe.data.GameMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Main Menu, providing quick access to game configuration.
 */
@HiltViewModel
class MenuViewModel @Inject constructor(
    private val preferences: PreferencesManager
) : ViewModel() {

    /** Current board size configuration. */
    val boardSize = preferences.boardSizeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), BoardSize()
    )

    /** Current board rendering style. */
    val boardStyle = preferences.boardStyleFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), BoardStyle.LAYERED_3D
    )

    /** Current game difficulty mode. */
    val gameMode = preferences.gameModeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), GameMode.HARD
    )

    fun setBoardSize(size: BoardSize) {
        viewModelScope.launch {
            val maxDim = maxOf(size.x, size.y, size.z)
            // Fix: ensure min of coerceIn is not greater than max (maxDim)
            val minWinCondition = minOf(3, maxDim)
            val clampedWinCondition = size.winCondition.coerceIn(minWinCondition, maxDim)
            val finalSize = size.copy(winCondition = clampedWinCondition)
            
            preferences.setBoardSize(finalSize)
        }
    }

    /** Toggles or sets the board style. */
    fun setBoardStyle(style: BoardStyle) {
        viewModelScope.launch {
            preferences.setBoardStyle(style)
        }
    }

    /** Updates the selected game difficulty. */
    fun setGameMode(mode: GameMode) {
        viewModelScope.launch {
            preferences.setGameMode(mode)
        }
    }
}
