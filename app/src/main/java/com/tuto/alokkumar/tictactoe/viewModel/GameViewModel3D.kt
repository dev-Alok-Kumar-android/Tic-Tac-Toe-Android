package com.tuto.alokkumar.tictactoe.viewModel

import androidx.lifecycle.ViewModel
import com.tuto.alokkumar.tictactoe.core.sound.Sound
import com.tuto.alokkumar.tictactoe.data.GameState3D
import com.tuto.alokkumar.tictactoe.domain.GameLogic3D
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel3D : ViewModel() {

    private val logic = GameLogic3D()

    private val _state = MutableStateFlow(GameState3D())
    val state = _state.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused = _isPaused.asStateFlow()


    fun nextLayer() {
        val s = _state.value
        _state.value = s.copy(
            activeLayer = (s.activeLayer + 1) % 3
        )
    }

    fun prevLayer() {
        val s = _state.value
        _state.value = s.copy(
            activeLayer = (s.activeLayer - 1 + 3) % 3
        )
    }

    fun pauseGame() {
        _isPaused.value = true
    }

    fun resumeGame() {
        _isPaused.value = false
    }

    fun onCellClicked(layer: Int, index2D: Int) {
        if (_isPaused.value) return
        val s = _state.value
        if (s.winner != null) return

        val globalIndex = layer * 9 + index2D

        if (!logic.makeMove(globalIndex)) return
        Sound.play("move")

        val winner = logic.winner

        if (winner != null) {
            when (winner) {
                'X' -> Sound.play("win")
                'O' -> Sound.play("lose")
                'D' -> Sound.play("draw")
            }
        }

        _state.value = s.copy(
            board = logic.getBoard(),
            currentPlayer = logic.currentPlayer,
            winner = winner,
            winLine = logic.winningLine,
            xWins = s.xWins + if (winner == 'X') 1 else 0,
            oWins = s.oWins + if (winner == 'O') 1 else 0,
            draws = s.draws + if (winner == 'D') 1 else 0
        )
    }

    fun restartGame() {
        logic.reset()
        val s = _state.value
        _state.value = s.copy(
            board = logic.getBoard(),
            currentPlayer = logic.currentPlayer,
            winner = null,
            winLine = null,
            activeLayer = 0 // 👈 reset layer too
        )
    }
}