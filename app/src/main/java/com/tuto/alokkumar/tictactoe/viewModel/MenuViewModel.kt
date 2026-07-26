package com.tuto.alokkumar.tictactoe.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuto.alokkumar.tictactoe.core.pref.PreferencesManager
import com.tuto.alokkumar.tictactoe.data.AiDifficulty
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.FirstMoveBehavior
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

    /** Current AI difficulty. */
    val aiDifficulty = preferences.aiDifficultyFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), AiDifficulty.HARD
    )

    /** Current game mode (PvP vs vs AI). */
    val gameMode = preferences.gameModeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), GameMode.VS_AI
    )

    /** Current first move behavior. */
    val firstMoveBehavior = preferences.firstMoveBehaviorFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), FirstMoveBehavior.PLAYER_X
    )

    /** Current AI strength level. */
    val aiStrength = preferences.aiStrengthFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 75
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

    /** Updates the selected AI difficulty. */
    fun setAiDifficulty(difficulty: AiDifficulty) {
        viewModelScope.launch {
            preferences.setAiDifficulty(difficulty)
        }
    }

    /** Updates the selected game mode. */
    fun setGameMode(mode: GameMode) {
        viewModelScope.launch {
            preferences.setGameMode(mode)
        }
    }

    /** Updates first move behavior. */
    fun setFirstMoveBehavior(behavior: FirstMoveBehavior) {
        viewModelScope.launch {
            preferences.setFirstMoveBehavior(behavior)
        }
    }
}
