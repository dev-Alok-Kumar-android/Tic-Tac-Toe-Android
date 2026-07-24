package com.tuto.alokkumar.tictactoe.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuto.alokkumar.tictactoe.core.pref.Preferences
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.BoardStyle
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Main Menu, providing quick access to game configuration.
 */
class MenuViewModel : ViewModel() {

    /** Current board size configuration. */
    val boardSize = Preferences.boardSizeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), BoardSize()
    )

    /** Current board rendering style. */
    val boardStyle = Preferences.boardStyleFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), BoardStyle.LAYERED_3D
    )

    fun setBoardSize(size: BoardSize) {
        viewModelScope.launch {
            val maxDim = maxOf(size.x, size.y, size.z)
            // Fix: ensure min of coerceIn is not greater than max (maxDim)
            val minWinCondition = minOf(3, maxDim)
            val clampedWinCondition = size.winCondition.coerceIn(minWinCondition, maxDim)
            val finalSize = size.copy(winCondition = clampedWinCondition)
            
            Preferences.setBoardSize(finalSize)
        }
    }

    /** Toggles or sets the board style. */
    fun setBoardStyle(style: BoardStyle) {
        viewModelScope.launch {
            Preferences.setBoardStyle(style)
        }
    }
}
