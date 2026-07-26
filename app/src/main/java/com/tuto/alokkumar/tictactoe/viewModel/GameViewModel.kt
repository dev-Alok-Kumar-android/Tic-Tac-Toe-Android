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
import com.tuto.alokkumar.tictactoe.data.AiDifficulty
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
    
    private val _gameMode = MutableStateFlow(if (route?.mode == null) GameMode.PVP else GameMode.VS_AI)
    val gameMode = _gameMode.asStateFlow()

    private val _aiDifficulty = MutableStateFlow(route?.mode ?: AiDifficulty.HARD)
    val aiDifficulty = _aiDifficulty.asStateFlow()

    private val boardSize: BoardSize = route?.boardSize ?: BoardSize()
    private val logic = GameLogic(_aiDifficulty.value, boardSize)

    // Player Customization State (Session specific)
    private val _p1Symbol = MutableStateFlow("X")
    val p1Symbol = _p1Symbol.asStateFlow()

    private val _p2Symbol = MutableStateFlow("O")
    val p2Symbol = _p2Symbol.asStateFlow()

    private val _p1Color = MutableStateFlow(0xFFE91E63)
    val p1Color = _p1Color.asStateFlow()

    private val _p2Color = MutableStateFlow(0xFF2196F3)
    val p2Color = _p2Color.asStateFlow()

    private val _p1Name = MutableStateFlow("Player 1")
    val p1Name = _p1Name.asStateFlow()

    private val _p2Name = MutableStateFlow("Player 2")
    val p2Name = _p2Name.asStateFlow()

    private val _humanSymbol = MutableStateFlow("X")
    val humanSymbol = _humanSymbol.asStateFlow()

    val isVsAI: Boolean get() = _gameMode.value == GameMode.VS_AI
    val isPlayerOAI: Boolean get() = isVsAI && _humanSymbol.value == _p1Symbol.value

    private val _state = MutableStateFlow(GameState(
        board = List(boardSize.x * boardSize.y * boardSize.z) { null },
        boardSize = boardSize
    ))
    val state = _state.asStateFlow()

    private val isRestoring = route == null
    private var isMatchSaved = false

    private val _isOpponentThinking = MutableStateFlow(false)
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

    private val hapticEnabled = preferences.hapticEnabledFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    init {
        viewModelScope.launch {
            val prefs = preferences.userPreferencesFlow.first()
            
            // Only apply global defaults if we are NOT restoring from history
            if (!isRestoring) {
                _p1Symbol.value = prefs.p1Symbol
                _p2Symbol.value = prefs.p2Symbol
                _p1Name.value = prefs.p1Name
                _p2Name.value = prefs.p2Name
                _p1Color.value = prefs.p1Color
                _p2Color.value = prefs.p2Color

                // Set human symbol based on first move preference
                _humanSymbol.value = when (prefs.firstMoveBehavior) {
                    FirstMoveBehavior.PLAYER_X -> prefs.p1Symbol
                    FirstMoveBehavior.PLAYER_O -> prefs.p2Symbol
                    else -> prefs.p1Symbol
                }

                val startChar = when (prefs.firstMoveBehavior) {
                    FirstMoveBehavior.PLAYER_X -> 'X'
                    FirstMoveBehavior.PLAYER_O -> 'O'
                    FirstMoveBehavior.RANDOM -> if (Math.random() < 0.5) 'X' else 'O'
                }
                
                logic.resetGame(startChar)
                updateState()
                
                // Trigger AI if it starts
                val isHumanP1 = _humanSymbol.value == _p1Symbol.value
                val aiChar = if (isHumanP1) 'O' else 'X'
                
                if (isVsAI && startChar == aiChar) {
                    _isOpponentThinking.value = true
                    delay(800.milliseconds)
                    aiMove()
                    _isOpponentThinking.value = false
                }
            }
        }
    }

    fun setLayer(layer: Int) {
        if (layer in 0 until boardSize.z) {
            _activeLayer.value = layer
        }
    }

    private fun triggerHaptic(type: String) {
        if (!hapticEnabled.value) return
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
        
        val isHumanP1 = _humanSymbol.value == _p1Symbol.value
        val humanChar = if (isHumanP1) 'X' else 'O'
        
        if (isVsAI && logic.currentPlayer != humanChar) return

        val current = _state.value
        if (current.winner != null || current.board[index] != null) return

        if (logic.makeMove(index)) {
            soundManager.playSound("move")
            triggerHaptic("move")
            checkAndHandleResult()
            saveHistory()
            
            if (logic.winner == null && isVsAI && logic.currentPlayer != humanChar) {
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
            saveHistory()
        }
    }

    private fun checkAndHandleResult() {
        val winnerChar = logic.winner
        val s = _state.value

        if (winnerChar != null) {
            when (winnerChar) {
                'X' -> { soundManager.playSound("win"); triggerHaptic("win") }
                'O' -> { soundManager.playSound("lose"); triggerHaptic("move") }
                'D' -> { soundManager.playSound("draw"); triggerHaptic("move") }
            }
        }

        _state.value = s.copy(
            board = mapBoardToSymbols(logic.getBoard()),
            currentPlayer = mapCharToSymbol(logic.currentPlayer),
            winner = winnerChar?.let { mapCharToSymbol(it) },
            winLine = logic.winLine,
            lastMove = logic.lastMove,
            xWins = s.xWins + if (winnerChar == 'X') 1 else 0,
            oWins = s.oWins + if (winnerChar == 'O') 1 else 0,
            draws = s.draws + if (winnerChar == 'D') 1 else 0
        )
    }

    private fun mapCharToSymbol(c: Char): String = if (c == 'X') _p1Symbol.value else if (c == 'O') _p2Symbol.value else c.toString()
    private fun mapBoardToSymbols(board: List<Char?>): List<String?> = board.map { it?.let { mapCharToSymbol(it) } }

    fun restartGame() {
        isMatchSaved = false
        viewModelScope.launch {
            val nextChar = calculateNextStartingPlayerChar()
            logic.resetGame(nextChar)
            
            _state.value = _state.value.copy(
                board = List(boardSize.x * boardSize.y * boardSize.z) { null },
                currentPlayer = mapCharToSymbol(nextChar),
                winner = null,
                winLine = null,
                lastMove = null
            )
            _activeLayer.value = 0
            
            val isHumanP1 = _humanSymbol.value == _p1Symbol.value
            val aiChar = if (isHumanP1) 'O' else 'X'

            if (isVsAI && nextChar == aiChar) {
                _isOpponentThinking.value = true
                delay(600.milliseconds)
                aiMove()
                _isOpponentThinking.value = false
            }
        }
    }

    private suspend fun calculateNextStartingPlayerChar(): Char {
        val currentState = _state.value
        val nextMovePref = preferences.nextMoveBehaviorFlow.first()
        val firstMovePref = preferences.firstMoveBehaviorFlow.first()

        val startChar = when (firstMovePref) {
            FirstMoveBehavior.PLAYER_X -> 'X'
            FirstMoveBehavior.PLAYER_O -> 'O'
            FirstMoveBehavior.RANDOM -> if (Math.random() < 0.5) 'X' else 'O'
        }

        if (currentState.winner == null) return startChar

        return when (nextMovePref) {
            NextMoveBehavior.FIXED -> startChar
            NextMoveBehavior.ALTERNATING -> if (logic.currentPlayer == 'X') 'O' else 'X'
            NextMoveBehavior.WINNER_STARTS -> {
                val w = logic.winner
                if (w == 'X' || w == 'O') w else (if (Math.random() < 0.5) 'X' else 'O')
            }
            NextMoveBehavior.LOSER_STARTS -> {
                val w = logic.winner
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
                    matchId = _state.value.matchId,
                    dateMillis = Date().time,
                    difficulty = _aiDifficulty.value,
                    gameMode = _gameMode.value,
                    state = _state.value,
                    humanSymbol = _humanSymbol.value,
                    p1Symbol = _p1Symbol.value,
                    p2Symbol = _p2Symbol.value,
                    p1Name = _p1Name.value,
                    p2Name = _p2Name.value,
                    p1Color = _p1Color.value,
                    p2Color = _p2Color.value
                )
            )
        }
    }

    private fun updateState() {
        val s = _state.value
        _state.value = s.copy(
            board = mapBoardToSymbols(logic.getBoard()),
            currentPlayer = mapCharToSymbol(logic.currentPlayer),
            winner = logic.winner?.let { mapCharToSymbol(it) },
            winLine = logic.winLine,
            lastMove = logic.lastMove
        )
    }

    fun loadFromHistory(history: GameHistory) {
        _p1Symbol.value = history.p1Symbol
        _p2Symbol.value = history.p2Symbol
        _p1Name.value = history.p1Name
        _p2Name.value = history.p2Name
        _p1Color.value = history.p1Color
        _p2Color.value = history.p2Color
        _humanSymbol.value = history.humanSymbol

        _state.value = history.state.copy(matchId = history.matchId)
        _gameMode.value = history.gameMode
        _aiDifficulty.value = history.difficulty
        logic.aiDifficulty = history.difficulty
        
        // Map symbols back to chars for logic
        val logicBoard = history.state.board.map { 
            if (it == history.p1Symbol) 'X' else if (it == history.p2Symbol) 'O' else null 
        }
        val logicCurrent = if (history.state.currentPlayer == history.p1Symbol) 'X' else 'O'
        
        logic.setBoard(logicBoard, logicCurrent, history.state.boardSize)
        _activeLayer.value = 0
        
        if (isVsAI && logic.currentPlayer != (if (history.humanSymbol == history.p1Symbol) 'X' else 'O') && logic.winner == null) {
            viewModelScope.launch {
                _isOpponentThinking.value = true
                delay(600.milliseconds)
                aiMove()
                _isOpponentThinking.value = false
            }
        }
    }

    override fun onCleared() {
        if (_state.value.winner == null && _state.value.board.any { it != null }) {
             saveHistory()
        }
    }
}
