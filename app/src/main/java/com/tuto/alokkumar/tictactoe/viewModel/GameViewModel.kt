package com.tuto.alokkumar.tictactoe.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tuto.alokkumar.tictactoe.core.pref.Preferences
import com.tuto.alokkumar.tictactoe.core.sound.Sound
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.BoardStyle
import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.GameState
import com.tuto.alokkumar.tictactoe.domain.GameLogic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * State holding ViewModel for the active gameplay session.
 *
 * Orchestrates communication between [GameLogic] and UI, handles AI move delays,
 * triggers sound effects, and manages persistence of completed matches into history.
 *
 * @param gameMode Selected difficulty/mode for this session.
 * @param boardSize Requested dimensions for the grid.
 * @param loadHistory Optional previous state used to resume a specific match.
 */
class GameViewModel(
    private val gameMode: GameMode = GameMode.PVP,
    private val boardSize: BoardSize = BoardSize(),
    loadHistory: GameHistory? = null
) : ViewModel() {

    private val logic = GameLogic(gameMode, boardSize)

    private val _state = MutableStateFlow(GameState(
        board = List(boardSize.x * boardSize.y * boardSize.z) { null },
        boardSize = boardSize
    ))
    /** Reactive flow emitting the latest snapshots of the board, winners, and scores. */
    val state = _state.asStateFlow()
    
    private val _isAiThinking = MutableStateFlow(false)
    /** Emits true when the AI is currently calculating its next move. */
    val isAiThinking = _isAiThinking.asStateFlow()
    
    private val _isPaused = MutableStateFlow(false)
    /** Emits true when the game is manually paused by the user. */
    val isPaused = _isPaused.asStateFlow()

    private val _activeLayer = MutableStateFlow(0)
    /** Current active 2D layer index being rendered in the UI. */
    val activeLayer = _activeLayer.asStateFlow()

    /** Flow emitting whether background animations are enabled globally. */
    val bgAnimationEnabled = Preferences.bgAnimationEnabledFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    /** Flow emitting the current board render style preference. */
    val boardStyle = Preferences.boardStyleFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), BoardStyle.CLASSIC
    )

    init {
        if (loadHistory != null) {
            _state.value = loadHistory.state
            logic.gameMode = loadHistory.mode
            logic.setBoard(loadHistory.state.board, loadHistory.state.currentPlayer)
        } else {
            updateState()
        }
    }

    /** Updates the visible 2D layer index for 3D playboards. */
    fun setLayer(layer: Int) {
        if (layer in 0 until boardSize.z) {
            _activeLayer.value = layer
        }
    }

    /**
     * Handles user interaction with a grid cell.
     * Validates if the move is legal and triggers subsequent AI routines if applicable.
     *
     * @param index Flattened index of the clicked cell.
     */
    fun onCellClicked(index: Int) {
        if (_isAiThinking.value || _isPaused.value) return
        val current = _state.value
        if (current.winner != null || current.board[index] != null) return

        if (logic.makeMove(index)) {
            Sound.play("move")
            checkAndHandleResult()
            
            if (logic.winner == null && gameMode != GameMode.PVP && logic.currentPlayer == 'O') {
                viewModelScope.launch {
                    _isAiThinking.value = true
                    delay(500L)
                    aiMove()
                    _isAiThinking.value = false
                }
            }
        }
    }

    private fun aiMove() {
        viewModelScope.launch {
            val move = withContext(Dispatchers.Default) {
                logic.getBestMove(logic.currentPlayer)
            }
            if (move != null) {
                logic.makeMove(move)
                Sound.play("move")
                checkAndHandleResult()
            }
        }
    }

    private fun checkAndHandleResult() {
        val winner = logic.winner
        val s = _state.value

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
            winLine = logic.winLine,
            lastMove = logic.lastMove,
            xWins = s.xWins + if (winner == 'X') 1 else 0,
            oWins = s.oWins + if (winner == 'O') 1 else 0,
            draws = s.draws + if (winner == 'D') 1 else 0
        )
    }

    /** Resets the logic engine and state flows to initial values. */
    fun restartGame() {
        logic.resetGame()
        _state.value = _state.value.copy(
            board = List(boardSize.x * boardSize.y * boardSize.z) { null },
            currentPlayer = 'X',
            winner = null,
            winLine = null
        )
        _activeLayer.value = 0
    }

    /** Blocks cell interactions. */
    fun pauseGame() { _isPaused.value = true }
    /** Resumes cell interactions. */
    fun resumeGame() { _isPaused.value = false }

    /** Persists current match snapshot into the persistent history DataStore. */
    fun saveHistory() {
        viewModelScope.launch {
            Preferences.addGameHistory(
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
            winner = logic.winner,
            winLine = logic.winLine,
            lastMove = logic.lastMove
        )
    }
}

/**
 * Factory class used to inject runtime session parameters into [GameViewModel].
 */
class GameViewModelFactory(
    private val mode: GameMode,
    private val boardSize: BoardSize,
    private val loadHistory: GameHistory? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(mode, boardSize, loadHistory) as T
    }
}
