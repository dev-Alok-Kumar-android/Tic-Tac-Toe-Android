package com.tuto.alokkumar.tictactoe.viewModel

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.tuto.alokkumar.tictactoe.Route
import com.tuto.alokkumar.tictactoe.core.navigation.BoardSizeNavType
import com.tuto.alokkumar.tictactoe.core.pref.PreferencesManager
import com.tuto.alokkumar.tictactoe.core.sound.SoundManager
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.BoardStyle
import com.tuto.alokkumar.tictactoe.data.FirstMoveBehavior
import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.GameState
import com.tuto.alokkumar.tictactoe.data.NextMoveBehavior
import com.tuto.alokkumar.tictactoe.domain.GameLogic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import kotlin.reflect.typeOf
import kotlin.time.Duration.Companion.milliseconds

/**
 * State holding ViewModel for the active gameplay session.
 */
@HiltViewModel
class GameViewModel @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: Context,
    private val preferences: PreferencesManager,
    private val soundManager: SoundManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = try { 
        savedStateHandle.toRoute<Route.Game>(
            typeMap = mapOf(typeOf<BoardSize>() to BoardSizeNavType)
        ) 
    } catch (_: Exception) { null }
    
    private val gameMode: GameMode = route?.mode ?: GameMode.PVP
    private val boardSize: BoardSize = route?.boardSize ?: BoardSize()

    private val logic = GameLogic(gameMode, boardSize)

    /** The symbol used by the local human player. */
    var humanSymbol: Char = 'X'
        private set

    /** Returns true if the current match has an AI opponent. */
    val isVsAI: Boolean = gameMode != GameMode.PVP

    private var currentStartPlayer: Char = 'X'

    private val _state = MutableStateFlow(GameState(
        board = List(boardSize.x * boardSize.y * boardSize.z) { null },
        boardSize = boardSize
    ))
    val state = _state.asStateFlow()

    /** Returns true if Player O is an AI opponent. Legacy flag, consider using [isVsAI] with turn check. */
    val isPlayerOAI: Boolean = isVsAI && humanSymbol == 'X'
    
    private val _isOpponentThinking = MutableStateFlow(false)
    /** Emits true when the opponent (AI or remote) is currently calculating its next move. */
    val isOpponentThinking = _isOpponentThinking.asStateFlow()
    
    private val _isPaused = MutableStateFlow(false)
    val isPaused = _isPaused.asStateFlow()

    private val _activeLayer = MutableStateFlow(0)
    val activeLayer = _activeLayer.asStateFlow()

    val bgAnimationEnabled = preferences.bgAnimationEnabledFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    val boardStyle = preferences.boardStyleFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), BoardStyle.LAYERED_3D
    )

    init {
        viewModelScope.launch {
            val firstMovePref = preferences.firstMoveBehaviorFlow.first()
            
            // Fix: Human is X if X starts, or if Random chose X, etc.
            // But we actually want to know what symbol the user PREFERS to play as.
            // For now, let's assume if it's VS AI, and first move is Player O, 
            // the human wants to play as O and go first.
            humanSymbol = when (firstMovePref) {
                FirstMoveBehavior.PLAYER_X -> 'X'
                FirstMoveBehavior.PLAYER_O -> 'O'
                else -> 'X' // Random/Default human is X
            }

            currentStartPlayer = when (firstMovePref) {
                FirstMoveBehavior.PLAYER_X -> 'X'
                FirstMoveBehavior.PLAYER_O -> 'O'
                FirstMoveBehavior.RANDOM -> if (Math.random() < 0.5) 'X' else 'O'
            }
            logic.resetGame(currentStartPlayer)
            updateState()
            
            // If AI starts (currentStartPlayer is NOT humanSymbol), trigger it
            if (isVsAI && currentStartPlayer != humanSymbol) {
                _isOpponentThinking.value = true
                delay(800.milliseconds)
                aiMove()
                _isOpponentThinking.value = false
            }
        }
    }

    fun setLayer(layer: Int) {
        if (layer in 0 until boardSize.z) {
            _activeLayer.value = layer
        }
    }

    private fun triggerHaptic(type: String) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(VibratorManager::class.java)
            vibratorManager?.defaultVibrator ?: return
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        }

        if (vibrator.hasVibrator()) {
            when (type) {
                "move" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(30)
                    }
                }
                "win" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 200), -1))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(longArrayOf(0, 100, 50, 200), -1)
                    }
                }
            }
        }
    }

    fun onCellClicked(index: Int) {
        if (_isOpponentThinking.value || _isPaused.value) return
        
        // Logical Turn Guard: Only allow interaction if it's the intended player's turn.
        if (isVsAI && logic.currentPlayer != humanSymbol) return

        val current = _state.value
        if (current.winner != null || current.board[index] != null) return

        if (logic.makeMove(index)) {
            soundManager.playSound("move")
            triggerHaptic("move")
            checkAndHandleResult()
            
            // Trigger AI if match is ongoing and it's AI's turn (currentPlayer != humanSymbol)
            if (logic.winner == null && isVsAI && logic.currentPlayer != humanSymbol) {
                viewModelScope.launch {
                    _isOpponentThinking.value = true
                    delay(500.milliseconds)
                    aiMove()
                    _isOpponentThinking.value = false
                }
            }
        }
    }

    private suspend fun aiMove() {
        val prefs = preferences.userPreferencesFlow.first()
        val move = withContext(Dispatchers.Default) {
            logic.getBestMove(
                aiSymbol = logic.currentPlayer,
                strength = prefs.aiStrength,
                isManualDepth = prefs.isAdvancedAiEnabled,
                manualDepth = prefs.manualMaxDepth
            )
        }
        if (move != null) {
            logic.makeMove(move)
            soundManager.playSound("move")
            triggerHaptic("move")
            checkAndHandleResult()
        }
    }

    private fun checkAndHandleResult() {
        val winner = logic.winner
        val s = _state.value

        if (winner != null) {
            when (winner) {
                'X' -> { soundManager.playSound("win"); triggerHaptic("win") }
                'O' -> { soundManager.playSound("lose"); triggerHaptic("move") }
                'D' -> { soundManager.playSound("draw"); triggerHaptic("move") }
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

    fun restartGame() {
        viewModelScope.launch {
            val nextPlayer = calculateNextStartingPlayer()
            currentStartPlayer = nextPlayer
            
            logic.resetGame(nextPlayer)
            _state.value = _state.value.copy(
                board = List(boardSize.x * boardSize.y * boardSize.z) { null },
                currentPlayer = nextPlayer,
                winner = null,
                winLine = null,
                lastMove = null
            )
            _activeLayer.value = 0
            
            // If AI starts the new round (nextPlayer is NOT humanSymbol)
            if (isVsAI && nextPlayer != humanSymbol) {
                _isOpponentThinking.value = true
                delay(600.milliseconds)
                aiMove()
                _isOpponentThinking.value = false
            }
        }
    }

    private suspend fun calculateNextStartingPlayer(): Char {
        val currentState = _state.value
        val nextMovePref = preferences.nextMoveBehaviorFlow.first()
        val firstMovePref = preferences.firstMoveBehaviorFlow.first()

        if (currentState.winner == null) {
            return when (firstMovePref) {
                FirstMoveBehavior.PLAYER_X -> 'X'
                FirstMoveBehavior.PLAYER_O -> 'O'
                FirstMoveBehavior.RANDOM -> if (Math.random() < 0.5) 'X' else 'O'
            }
        }

        return when (nextMovePref) {
            NextMoveBehavior.FIXED -> when (firstMovePref) {
                FirstMoveBehavior.PLAYER_X -> 'X'
                FirstMoveBehavior.PLAYER_O -> 'O'
                FirstMoveBehavior.RANDOM -> if (Math.random() < 0.5) 'X' else 'O'
            }
            NextMoveBehavior.ALTERNATING -> if (currentStartPlayer == 'X') 'O' else 'X'
            NextMoveBehavior.WINNER_STARTS -> {
                val w = currentState.winner
                if (w == 'X' || w == 'O') w else (if (Math.random() < 0.5) 'X' else 'O')
            }
            NextMoveBehavior.LOSER_STARTS -> {
                val w = currentState.winner
                if (w == 'X') 'O' else if (w == 'O') 'X' else (if (Math.random() < 0.5) 'X' else 'O')
            }
            NextMoveBehavior.RANDOM -> if (Math.random() < 0.5) 'X' else 'O'
        }
    }

    fun pauseGame() { _isPaused.value = true }
    fun resumeGame() { _isPaused.value = false }

    fun saveHistory() {
        viewModelScope.launch {
            preferences.addGameHistory(
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

    fun loadFromHistory(history: GameHistory) {
        _state.value = history.state
        logic.gameMode = history.mode
        logic.setBoard(history.state.board, history.state.currentPlayer)
        _activeLayer.value = 0
    }
}
