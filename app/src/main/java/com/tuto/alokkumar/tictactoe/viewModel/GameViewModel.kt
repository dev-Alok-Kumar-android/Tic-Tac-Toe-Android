package com.tuto.alokkumar.tictactoe.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.tuto.alokkumar.tictactoe.Route
import com.tuto.alokkumar.tictactoe.core.navigation.BoardSizeNavType
import com.tuto.alokkumar.tictactoe.data.AiDifficulty
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.BoardStyle
import com.tuto.alokkumar.tictactoe.data.FirstMoveBehavior
import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.UserPreferences
import com.tuto.alokkumar.tictactoe.domain.HardwareService
import com.tuto.alokkumar.tictactoe.domain.mapper.MatchMapper
import com.tuto.alokkumar.tictactoe.domain.model.GameFeedback
import com.tuto.alokkumar.tictactoe.domain.model.GameState
import com.tuto.alokkumar.tictactoe.domain.model.MatchResult
import com.tuto.alokkumar.tictactoe.domain.model.Player
import com.tuto.alokkumar.tictactoe.domain.model.PlayerId
import com.tuto.alokkumar.tictactoe.domain.usecase.ComputeWinningLinesUseCase
import com.tuto.alokkumar.tictactoe.domain.usecase.GetAiMoveUseCase
import com.tuto.alokkumar.tictactoe.domain.usecase.ManageHistoryUseCase
import com.tuto.alokkumar.tictactoe.domain.usecase.ManagePreferencesUseCase
import com.tuto.alokkumar.tictactoe.domain.usecase.ProcessMoveUseCase
import com.tuto.alokkumar.tictactoe.ui.model.GameStateUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import kotlin.reflect.typeOf
import kotlin.time.Duration.Companion.milliseconds

/**
 * Professional Orchestrator for Tic Tac Toe matches.
 * Reactive architecture that handles process death, heavy calculations, and ID-based logic.
 */
@HiltViewModel
class GameViewModel @Inject constructor(
    private val processMoveUseCase: ProcessMoveUseCase,
    private val getAiMoveUseCase: GetAiMoveUseCase,
    private val computeWinningLinesUseCase: ComputeWinningLinesUseCase,
    private val manageHistoryUseCase: ManageHistoryUseCase,
    private val managePreferencesUseCase: ManagePreferencesUseCase,
    private val hardwareService: HardwareService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = try { 
        savedStateHandle.toRoute<Route.Game>(
            typeMap = mapOf(typeOf<BoardSize>() to BoardSizeNavType)
        ) 
    } catch (_: Exception) { null }
    
    // --- MATCH CONFIGURATION (Reactive) ---
    private val _gameMode = MutableStateFlow(if (route?.mode == null) GameMode.PVP else GameMode.VS_AI)
    val gameMode = _gameMode.asStateFlow()

    private val _aiDifficulty = MutableStateFlow(route?.mode ?: AiDifficulty.HARD)
    val aiDifficulty = _aiDifficulty.asStateFlow()

    private var winningLines = emptyList<List<Int>>() 

    // --- PLAYER ABSTRACTION ---
    private val _player1 = MutableStateFlow<Player?>(null)
    private val _player2 = MutableStateFlow<Player?>(null)

    val p1Symbol = _player1.map { it?.symbol ?: "X" }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "X")
    val p1Name = _player1.map { it?.name ?: "Player 1" }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Player 1")
    val p1Color = _player1.map { it?.color ?: 0xFFE91E63 }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0xFFE91E63)

    val p2Symbol = _player2.map { it?.symbol ?: "O" }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "O")
    val p2Name = _player2.map { it?.name ?: "Player 2" }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Player 2")
    val p2Color = _player2.map { it?.color ?: 0xFF2196F3 }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0xFF2196F3)

    private val _humanSymbol = MutableStateFlow("X")
    val humanSymbol = _humanSymbol.asStateFlow()

    val isVsAI: Boolean get() = _gameMode.value == GameMode.VS_AI

    // --- STATE MANAGEMENT ---
    private val _domainState = MutableStateFlow(GameState(boardSize = route?.boardSize ?: BoardSize()))

    val state: StateFlow<GameStateUi> = savedStateHandle.getStateFlow<GameStateUi?>(
        "game_ui_state", 
        null
    ).map { savedUi ->
        savedUi ?: MatchMapper.mapToUi(_domainState.value, p1Symbol.value, p2Symbol.value)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MatchMapper.mapToUi(_domainState.value, "X", "O"))

    private fun updateUiState(newDomainState: GameState) {
        _domainState.value = newDomainState
        savedStateHandle["game_ui_state"] = MatchMapper.mapToUi(
            newDomainState, p1Symbol.value, p2Symbol.value
        )
        // Auto-persist match ID to preferences whenever state changes, 
        // ensuring Settings always knows about the active match.
        viewModelScope.launch {
            if (newDomainState.result is MatchResult.Ongoing) {
                managePreferencesUseCase.updatePreferences { 
                    if (it.activeMatchId != newDomainState.matchId) it.copy(activeMatchId = newDomainState.matchId) else it
                }
            }
        }
    }

    // --- UI CONTROLS ---
    private val _isOpponentThinking = MutableStateFlow(false)
    val isOpponentThinking = _isOpponentThinking.asStateFlow()
    
    private var aiTurnJob: kotlinx.coroutines.Job? = null

    private val _isPaused = MutableStateFlow(false)
    val isPaused = _isPaused.asStateFlow()

    private val _activeLayer = MutableStateFlow(0)
    val activeLayer = _activeLayer.asStateFlow()

    val bgAnimationEnabled = managePreferencesUseCase.userPreferences.map { it.bgAnimationEnabled }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )
    val boardStyle = managePreferencesUseCase.userPreferences.map { it.boardStyle }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), BoardStyle.LAYERED_3D
    )
    private val hapticEnabled = managePreferencesUseCase.userPreferences.map { it.hapticEnabled }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    init {
        viewModelScope.launch {
            winningLines = computeWinningLinesUseCase(_domainState.value.boardSize)
            
            // 1. Initial Setup (One-time based on current prefs and saved state)
            val initialPrefs = managePreferencesUseCase.userPreferences.first()
            if (route?.mode == null) {
                _aiDifficulty.value = initialPrefs.aiDifficulty
                _gameMode.value = initialPrefs.gameMode
            }
            
            val savedUi = savedStateHandle.get<GameStateUi>("game_ui_state")
            if (savedUi == null) {
                val startId = determineStartingPlayerId(initialPrefs)
                val newMatchId = java.util.UUID.randomUUID().toString()
                updateUiState(_domainState.value.copy(
                    matchId = newMatchId,
                    currentPlayerId = startId
                ))
                managePreferencesUseCase.updatePreferences { it.copy(activeMatchId = newMatchId) }
            } else {
                restoreDomainState(savedUi)
                // Ensure the active match ID is synced if it was lost (e.g. process death)
                if (initialPrefs.activeMatchId != savedUi.matchId) {
                    managePreferencesUseCase.updatePreferences { it.copy(activeMatchId = savedUi.matchId) }
                }
            }

            // 2. Reactive Player Sync (Only visual properties should sync live)
            managePreferencesUseCase.userPreferences.collect { prefs ->
                initializePlayers(prefs)
            }
        }
        observeTurnLoop()
    }

    private fun restoreDomainState(savedUi: GameStateUi) {
        _domainState.value = GameState(
            matchId = savedUi.matchId,
            boardSize = savedUi.boardSize,
            board = savedUi.board.map { sym ->
                when(sym) {
                    p1Symbol.value -> PlayerId.P1
                    p2Symbol.value -> PlayerId.P2
                    else -> null
                }
            },
            currentPlayerId = if (savedUi.currentPlayerSymbol == p1Symbol.value) PlayerId.P1 else PlayerId.P2,
            result = when(savedUi.winnerSymbol) {
                p1Symbol.value -> MatchResult.Winner(PlayerId.P1)
                p2Symbol.value -> MatchResult.Winner(PlayerId.P2)
                "D" -> MatchResult.Draw
                else -> MatchResult.Ongoing
            },
            winLine = savedUi.winLine,
            lastMove = savedUi.lastMove,
            p1Wins = savedUi.p1Wins,
            p2Wins = savedUi.p2Wins,
            draws = savedUi.draws
        )
    }

    private fun initializePlayers(prefs: UserPreferences) {
        val existingAi = _player2.value as? Player.Ai
        val currentState = _domainState.value
        val isGameStarted = currentState.board.any { it != null } && currentState.result is MatchResult.Ongoing

        // 1. Update Player 1 (Local)
        _player1.value = if (isGameStarted) {
            (_player1.value as? Player.Local)?.copy(color = prefs.p1Color)
                ?: Player.Local(PlayerId.P1, prefs.p1Name, prefs.p1Symbol, prefs.p1Color)
        } else {
            Player.Local(PlayerId.P1, prefs.p1Name, prefs.p1Symbol, prefs.p1Color)
        }
        
        // 2. Update Player 2 (Local or AI)
        _player2.value = if (isVsAI) {
            if (isGameStarted && existingAi != null) {
                existingAi.copy(color = prefs.p2Color)
            } else {
                Player.Ai(
                    id = PlayerId.P2,
                    name = "AI",
                    symbol = prefs.p2Symbol,
                    color = prefs.p2Color,
                    difficulty = _aiDifficulty.value,
                    strength = prefs.aiStrength,
                    manualMaxDepth = prefs.manualMaxDepth
                )
            }
        } else {
            if (isGameStarted) {
                (_player2.value as? Player.Local)?.copy(color = prefs.p2Color)
                    ?: Player.Local(PlayerId.P2, prefs.p2Name, prefs.p2Symbol, prefs.p2Color)
            } else {
                Player.Local(PlayerId.P2, prefs.p2Name, prefs.p2Symbol, prefs.p2Color)
            }
        }

        // 3. Human symbol mapping (only update if not started)
        if (!isGameStarted) {
            _humanSymbol.value = if (prefs.firstMoveBehavior == FirstMoveBehavior.PLAYER_O) prefs.p2Symbol else prefs.p1Symbol
        }
    }

    private fun determineStartingPlayerId(prefs: UserPreferences): PlayerId {
        return when (prefs.firstMoveBehavior) {
            FirstMoveBehavior.PLAYER_X -> PlayerId.P1
            FirstMoveBehavior.PLAYER_O -> PlayerId.P2
            else -> if (Math.random() < 0.5) PlayerId.P1 else PlayerId.P2
        }
    }

    private fun observeTurnLoop() {
        viewModelScope.launch {
            _domainState.map { it.currentPlayerId }.distinctUntilChanged().collectLatest { playerId ->
                val currentPlayer = if (playerId == PlayerId.P1) _player1.value else _player2.value
                if (currentPlayer is Player.Ai && _domainState.value.result is MatchResult.Ongoing) {
                    triggerAiTurn()
                }
            }
        }
    }

    fun setLayer(layer: Int) {
        if (layer in 0 until _domainState.value.boardSize.z) {
            _activeLayer.value = layer
        }
    }

    fun onCellClicked(index: Int) {
        if (_isOpponentThinking.value || _isPaused.value || winningLines.isEmpty()) return
        
        val playerId = _domainState.value.currentPlayerId
        val currentPlayer = if (playerId == PlayerId.P1) _player1.value else _player2.value
        
        if (currentPlayer is Player.Local || currentPlayer == null) {
             performMove(index)
        }
    }

    private fun performMove(index: Int) {
        val current = _domainState.value
        val newState = processMoveUseCase(current, index, winningLines)
        if (newState != current) {
            applyNewState(newState)
        }
    }

    private fun triggerAiTurn() {
        aiTurnJob?.cancel()
        aiTurnJob = viewModelScope.launch {
            try {
                _isOpponentThinking.value = true
                delay(600.milliseconds)
                aiMove()
            } finally {
                _isOpponentThinking.value = false
            }
        }
    }

    private suspend fun aiMove() {
        val current = _domainState.value
        val aiPlayer = (if (current.currentPlayerId == PlayerId.P1) _player1.value else _player2.value) as? Player.Ai ?: return
        
        val move = getAiMoveUseCase(
            board = current.board,
            aiPlayerId = aiPlayer.id,
            difficulty = aiPlayer.difficulty,
            boardSize = current.boardSize,
            winLines = winningLines,
            strength = aiPlayer.strength,
            manualDepth = aiPlayer.manualMaxDepth
        )
        
        move?.let { performMove(it) }
    }

    private fun applyNewState(newState: GameState) {
        val oldState = _domainState.value
        updateUiState(newState)

        if (newState.lastMove != oldState.lastMove) {
            hardwareService.playFeedback(GameFeedback.MOVE, hapticEnabled.value)
        }

        if (newState.result !is MatchResult.Ongoing && newState != oldState) {
            handleWinEffects(newState)
            viewModelScope.launch {
                managePreferencesUseCase.updatePreferences { it.copy(activeMatchId = null) }
            }
        }
        saveHistory()
    }

    private fun handleWinEffects(state: GameState) {
        val currentResult = state.result
        if (currentResult is MatchResult.Draw) {
            hardwareService.playFeedback(GameFeedback.DRAW, hapticEnabled.value)
            return
        }
        
        val winnerId = (currentResult as? MatchResult.Winner)?.id ?: return
        val humanSym = _humanSymbol.value
        val winnerPlayer = if (winnerId == PlayerId.P1) _player1.value else _player2.value
        
        if (winnerPlayer?.symbol == humanSym) {
            hardwareService.playFeedback(GameFeedback.WIN, hapticEnabled.value)
        } else {
            hardwareService.playFeedback(if (isVsAI) GameFeedback.LOSE else GameFeedback.WIN, hapticEnabled.value)
        }
    }

    fun restartGame() {
        aiTurnJob?.cancel()
        _isOpponentThinking.value = false
        
        viewModelScope.launch {
            val prefs = managePreferencesUseCase.userPreferences.first()
            val newSize = prefs.boardSize
            
            // 1. Sync match-level configuration
            if (route?.mode == null) {
                _aiDifficulty.value = prefs.aiDifficulty
                _gameMode.value = prefs.gameMode
            }
            
            // 2. Recompute lines if board size changed
            if (newSize != _domainState.value.boardSize) {
                winningLines = computeWinningLinesUseCase(newSize)
            }
            
            // 3. Reset domain state with new size and starting player
            val nextId = determineStartingPlayerId(prefs)
            val newMatchId = java.util.UUID.randomUUID().toString()
            updateUiState(GameState(
                matchId = newMatchId,
                boardSize = newSize,
                board = List(newSize.x * newSize.y * newSize.z) { null },
                currentPlayerId = nextId,
                p1Wins = _domainState.value.p1Wins,
                p2Wins = _domainState.value.p2Wins,
                draws = _domainState.value.draws
            ))
            managePreferencesUseCase.updatePreferences { it.copy(activeMatchId = newMatchId) }
            
            // 4. Force player re-initialization (now that board is empty)
            initializePlayers(prefs)
            
            _activeLayer.value = 0
            _isPaused.value = false
        }
    }

    fun pauseGame() { _isPaused.value = true }
    fun resumeGame() { _isPaused.value = false }

    fun saveHistory() {
        viewModelScope.launch {
            val p1 = _player1.value ?: return@launch
            val p2 = _player2.value ?: return@launch
            
            val (strength, depth) = if (p2 is Player.Ai) {
                p2.strength to p2.manualMaxDepth
            } else {
                val prefs = managePreferencesUseCase.userPreferences.first()
                prefs.aiStrength to prefs.manualMaxDepth
            }

            manageHistoryUseCase.saveGame(
                GameHistory(
                    matchId = _domainState.value.matchId,
                    dateMillis = Date().time,
                    difficulty = _aiDifficulty.value,
                    gameMode = _gameMode.value,
                    state = MatchMapper.mapToEntity(_domainState.value),
                    humanSymbol = _humanSymbol.value,
                    p1Symbol = p1.symbol,
                    p2Symbol = p2.symbol,
                    p1Name = p1.name,
                    p2Name = p2.name,
                    p1Color = p1.color,
                    p2Color = p2.color,
                    aiStrength = strength,
                    manualMaxDepth = depth
                )
            )
        }
    }

    fun loadFromHistory(history: GameHistory) {
        aiTurnJob?.cancel()
        _isOpponentThinking.value = false

        _player1.value = Player.Local(PlayerId.P1, history.p1Name, history.p1Symbol, history.p1Color)
        _player2.value = if (history.gameMode == GameMode.VS_AI) {
            Player.Ai(
                id = PlayerId.P2,
                name = history.p2Name,
                symbol = history.p2Symbol,
                color = history.p2Color,
                difficulty = history.difficulty,
                strength = history.aiStrength,
                manualMaxDepth = history.manualMaxDepth
            )
        } else {
            Player.Local(PlayerId.P2, history.p2Name, history.p2Symbol, history.p2Color)
        }
        
        _humanSymbol.value = history.humanSymbol
        _gameMode.value = history.gameMode
        _aiDifficulty.value = history.difficulty

        val restoredDomain = MatchMapper.mapToDomain(history.state, history.matchId)
        updateUiState(restoredDomain)
        
        viewModelScope.launch {
            winningLines = computeWinningLinesUseCase(restoredDomain.boardSize)
        }
        _activeLayer.value = 0
    }

    override fun onCleared() {
        if (_domainState.value.result is MatchResult.Ongoing && _domainState.value.board.any { it != null }) {
             saveHistory()
        }
    }
}
