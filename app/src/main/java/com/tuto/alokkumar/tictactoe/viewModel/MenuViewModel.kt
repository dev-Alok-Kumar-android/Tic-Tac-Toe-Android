package com.tuto.alokkumar.tictactoe.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuto.alokkumar.tictactoe.data.AiDifficulty
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.FirstMoveBehavior
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.domain.usecase.ManagePreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Main Menu, providing quick access to game configuration.
 */
@HiltViewModel
class MenuViewModel @Inject constructor(
    private val managePreferencesUseCase: ManagePreferencesUseCase
) : ViewModel() {

    val boardSize = managePreferencesUseCase.userPreferences.map { it.boardSize }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), BoardSize()
    )

    val aiDifficulty = managePreferencesUseCase.userPreferences.map { it.aiDifficulty }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), AiDifficulty.HARD
    )

    @Suppress("unused")
    val gameMode = managePreferencesUseCase.userPreferences.map { it.gameMode }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), GameMode.VS_AI
    )

    val firstMoveBehavior = managePreferencesUseCase.userPreferences.map { it.firstMoveBehavior }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), FirstMoveBehavior.PLAYER_X
    )

    val aiStrength = managePreferencesUseCase.userPreferences.map { it.aiStrength }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 75
    )

    val p1Name = managePreferencesUseCase.userPreferences.map { it.p1Name }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "Player 1"
    )

    val p2Name = managePreferencesUseCase.userPreferences.map { it.p2Name }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "Player 2"
    )

    fun setBoardSize(size: BoardSize) {
        viewModelScope.launch {
            val maxDim = maxOf(size.x, size.y, size.z)
            val minWinCondition = minOf(3, maxDim)
            val clampedWinCondition = size.winCondition.coerceIn(minWinCondition, maxDim)
            managePreferencesUseCase.updatePreferences { it.copy(boardSize = size.copy(winCondition = clampedWinCondition)) }
        }
    }

    fun setAiDifficulty(difficulty: AiDifficulty) {
        viewModelScope.launch {
            managePreferencesUseCase.updatePreferences { it.copy(aiDifficulty = difficulty) }
        }
    }

    @Suppress("unused")
    fun setGameMode(mode: GameMode) {
        viewModelScope.launch {
            managePreferencesUseCase.updatePreferences { it.copy(gameMode = mode) }
        }
    }

    fun setFirstMoveBehavior(behavior: FirstMoveBehavior) {
        viewModelScope.launch {
            managePreferencesUseCase.updatePreferences { it.copy(firstMoveBehavior = behavior) }
        }
    }
}
