package com.tuto.alokkumar.tictactoe.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.GameState
import com.tuto.alokkumar.tictactoe.data.PreferencesManager
import com.tuto.alokkumar.tictactoe.data.Sound
import com.tuto.alokkumar.tictactoe.domain.GameLogic
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class GameViewModel(
    private val gameMode: GameMode = GameMode.PVP,
    loadHistory: GameHistory? = null
) : ViewModel() {

    private val logic = GameLogic(gameMode)

    private val _state = MutableStateFlow(GameState())
    val state = _state.asStateFlow()

    private var isPaused = false

    fun pauseGame() {
        isPaused = true
    }

    fun resumeGame() {
        isPaused = false
    }

    init {
        if (loadHistory != null) {
            _state.value = loadHistory.state
            logic.gameMode = loadHistory.mode
            logic.setBoard(loadHistory.state.board, loadHistory.state.currentPlayer)
        } else {
            updateState()
        }
    }

    fun onCellClicked(index: Int) {
        val current = _state.value
        if (current.winner != null || current.board[index] != null) return

        if (logic.makeMove(index)) Sound.play("move")
        else return

        checkAndHandleResult()

        // AI plays after human (for AI modes)
        if (logic.winner == null && gameMode != GameMode.PVP && logic.currentPlayer == 'O') {
            viewModelScope.launch {
                delay(400L)
                aiMove()
            }
        }
    }

    private fun aiMove() {
        val aiSymbol = logic.currentPlayer
        val move = logic.getBestMove(aiSymbol)
        if (move != null) {
            logic.makeMove(move)
            Sound.play("move")
            checkAndHandleResult()
        }
    }

    private fun checkAndHandleResult() {
        val winner = logic.winner
        val s = _state.value

        if (winner!=null){
            when(winner){
                'X' -> Sound.play("win")
                'O' -> Sound.play("lose")
                'D' -> Sound.play("draw")
            }
        }
        _state.value = s.copy(
            board = logic.getBoard(),
            currentPlayer = logic.currentPlayer,
            winner = winner,
            xWins = s.xWins + if (winner == 'X') 1 else 0,
            oWins = s.oWins + if (winner == 'O') 1 else 0,
            draws = s.draws + if (winner == 'D') 1 else 0
        )
    }

    fun restartGame() {
        logic.resetGame()
        // Keep scores — only reset board and winner
        _state.value = _state.value.copy(
            board = List(9) { null },
            currentPlayer = 'X',
            winner = null
        )
        updateState()
    }

    fun saveHistory(prefs: PreferencesManager){
        viewModelScope.launch {
            prefs.addGameHistory(
                GameHistory(
                    dateMillis = Date().time,
                    mode = gameMode,
                    state = _state.value
                )
            )
        }
    }

    private fun updateState() {
        val s = _state.value
        _state.value = s.copy(
            board = logic.getBoard(),
            currentPlayer = logic.currentPlayer,
            winner = logic.winner
        )
    }
}



class GameViewModelFactory(
    private val mode: GameMode,
    private val loadHistory: GameHistory? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(mode, loadHistory) as T  // warning here: Unchecked cast of 'GameViewModel' to 'T (of fun <T : ViewModel> create)'.
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}