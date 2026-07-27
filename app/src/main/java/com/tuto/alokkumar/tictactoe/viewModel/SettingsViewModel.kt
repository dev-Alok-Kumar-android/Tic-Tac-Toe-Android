package com.tuto.alokkumar.tictactoe.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuto.alokkumar.tictactoe.data.AiDifficulty
import com.tuto.alokkumar.tictactoe.data.AppLanguage
import com.tuto.alokkumar.tictactoe.data.AppTheme
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.BoardStyle
import com.tuto.alokkumar.tictactoe.data.FirstMoveBehavior
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.NextMoveBehavior
import com.tuto.alokkumar.tictactoe.data.Orientation
import com.tuto.alokkumar.tictactoe.domain.HardwareService
import com.tuto.alokkumar.tictactoe.domain.usecase.CalculateMatchProbabilitiesUseCase
import com.tuto.alokkumar.tictactoe.domain.usecase.ManagePreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * Visual guidance for AI performance and outcome expectations.
 */
data class AiGuidance(
    val recommendedDepth: Int,
    val performanceOutlook: PerformanceLevel,
    val matchOutlook: String,
    val warning: String? = null,
)

enum class PerformanceLevel { FAST, BALANCED, SLOW, DANGEROUS }

/**
 * ViewModel managing global application preferences and configuration state.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val managePreferencesUseCase: ManagePreferencesUseCase,
    private val hardwareService: HardwareService,
    private val calculateMatchProbabilitiesUseCase: CalculateMatchProbabilitiesUseCase,
) : ViewModel() {

    private val _p1NameLocal = MutableStateFlow("Player 1")
    val p1Name = _p1NameLocal.asStateFlow()

    private val _p2NameLocal = MutableStateFlow("Player 2")
    val p2Name = _p2NameLocal.asStateFlow()

    private val _p1SymbolLocal = MutableStateFlow("X")
    val p1Symbol = _p1SymbolLocal.asStateFlow()

    private val _p2SymbolLocal = MutableStateFlow("O")
    val p2Symbol = _p2SymbolLocal.asStateFlow()

    private val _showStickyWarning = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)
    val showStickyWarning = _showStickyWarning.asSharedFlow()

    init {
        viewModelScope.launch {
            managePreferencesUseCase.userPreferences.collect { prefs ->
                if (_p1NameLocal.value != prefs.p1Name) _p1NameLocal.value = prefs.p1Name
                if (_p2NameLocal.value != prefs.p2Name) _p2NameLocal.value = prefs.p2Name
                if (_p1SymbolLocal.value != prefs.p1Symbol) _p1SymbolLocal.value = prefs.p1Symbol
                if (_p2SymbolLocal.value != prefs.p2Symbol) _p2SymbolLocal.value = prefs.p2Symbol
            }
        }
        
        setupDebouncedPersistence()
    }

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    private fun setupDebouncedPersistence() {
        viewModelScope.launch {
            _p1NameLocal.debounce(800.milliseconds).distinctUntilChanged().collectLatest { name ->
                val prefs = managePreferencesUseCase.userPreferences.first()
                if (prefs.p1Name != name) {
                    if (prefs.activeMatchId != null) _showStickyWarning.emit(Unit)
                    managePreferencesUseCase.updatePreferences { it.copy(p1Name = name) }
                }
            }
        }
        viewModelScope.launch {
            _p2NameLocal.debounce(800.milliseconds).distinctUntilChanged().collectLatest { name ->
                val prefs = managePreferencesUseCase.userPreferences.first()
                if (prefs.p2Name != name) {
                    if (prefs.activeMatchId != null) _showStickyWarning.emit(Unit)
                    managePreferencesUseCase.updatePreferences { it.copy(p2Name = name) }
                }
            }
        }
        viewModelScope.launch {
            _p1SymbolLocal.debounce(800.milliseconds).distinctUntilChanged().collectLatest { symbol ->
                if (symbol.isNotEmpty()) {
                    val prefs = managePreferencesUseCase.userPreferences.first()
                    if (prefs.p1Symbol != symbol) {
                        if (prefs.activeMatchId != null) _showStickyWarning.emit(Unit)
                        managePreferencesUseCase.updatePreferences { it.copy(p1Symbol = symbol) }
                    }
                }
            }
        }
        viewModelScope.launch {
            _p2SymbolLocal.debounce(800.milliseconds).distinctUntilChanged().collectLatest { symbol ->
                if (symbol.isNotEmpty()) {
                    val prefs = managePreferencesUseCase.userPreferences.first()
                    if (prefs.p2Symbol != symbol) {
                        if (prefs.activeMatchId != null) _showStickyWarning.emit(Unit)
                        managePreferencesUseCase.updatePreferences { it.copy(p2Symbol = symbol) }
                    }
                }
            }
        }
    }

    fun playBgm() = hardwareService.startBgm()
    fun pauseBgm() = hardwareService.stopBgm()

    val selectedAiDifficulty = managePreferencesUseCase.userPreferences.map { it.aiDifficulty }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), AiDifficulty.HARD
    )

    @Suppress("unused")
    val matchType = managePreferencesUseCase.userPreferences.map { it.gameMode }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), GameMode.VS_AI
    )

    val boardSize = managePreferencesUseCase.userPreferences.map { it.boardSize }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), BoardSize()
    )

    val theme = managePreferencesUseCase.userPreferences.map { it.theme }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), AppTheme.SYSTEM
    )

    val immersiveMode = managePreferencesUseCase.userPreferences.map { it.immersiveMode }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), initialValue = false
    )

    val bgmEnabled = managePreferencesUseCase.userPreferences.map { it.bgmEnabled }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val soundEnabled = managePreferencesUseCase.userPreferences.map { it.soundEnabled }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val dynamicColor = managePreferencesUseCase.userPreferences.map { it.dynamicColor }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val bgAnimationEnabled = managePreferencesUseCase.userPreferences.map { it.bgAnimationEnabled }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    val boardStyle = managePreferencesUseCase.userPreferences.map { it.boardStyle }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), BoardStyle.LAYERED_3D
    )

    val orientation = managePreferencesUseCase.userPreferences.map { it.orientation }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), Orientation.SYSTEM
    )

    val firstMoveBehavior = managePreferencesUseCase.userPreferences.map { it.firstMoveBehavior }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), FirstMoveBehavior.PLAYER_X
    )

    val nextMoveBehavior = managePreferencesUseCase.userPreferences.map { it.nextMoveBehavior }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), NextMoveBehavior.ALTERNATING
    )

    val aiStrength = managePreferencesUseCase.userPreferences.map { it.aiStrength }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 75
    )

    val manualMaxDepth = managePreferencesUseCase.userPreferences.map { it.manualMaxDepth }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 6
    )

    val hapticEnabled = managePreferencesUseCase.userPreferences.map { it.hapticEnabled }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val appLanguage = managePreferencesUseCase.userPreferences.map { it.appLanguage }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.ENGLISH
    )

    val p1Color = managePreferencesUseCase.userPreferences.map { it.p1Color }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0xFFE91E63
    )
    val p2Color = managePreferencesUseCase.userPreferences.map { it.p2Color }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0xFF2196F3
    )

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    val probabilities = combine(boardSize, firstMoveBehavior) { size, first ->
        size to first
    }.debounce(300.milliseconds)
    .map { (size, first) ->
        calculateMatchProbabilitiesUseCase(size, first)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val aiGuidance = combine(boardSize, selectedAiDifficulty, manualMaxDepth) { size, diff, depth ->
        val totalCells = size.x * size.y * size.z
        
        val recommended = when {
            totalCells <= 9 -> 9
            totalCells <= 16 -> 6
            totalCells <= 25 -> 4
            else -> 2
        }

        val perf = when {
            depth <= recommended -> PerformanceLevel.FAST
            depth <= (recommended + 2) -> PerformanceLevel.BALANCED
            depth <= (recommended + 4) -> PerformanceLevel.SLOW
            else -> PerformanceLevel.DANGEROUS
        }

        val outlook = when {
            diff == AiDifficulty.IMPOSSIBLE && depth >= 9 && totalCells <= 9 -> 
                "Invincible: AI will always Win or Draw."
            diff == AiDifficulty.EASY -> 
                "Casual: AI will make frequent mistakes."
            depth < recommended -> 
                "Human-like: AI may miss strategic moves."
            else -> "Professional: AI is highly strategic."
        }

        val warn = if (perf == PerformanceLevel.DANGEROUS || perf == PerformanceLevel.SLOW) {
            "Large board + high depth might cause calculation lag."
        } else null

        AiGuidance(recommended, perf, outlook, warn)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AiGuidance(9, PerformanceLevel.FAST, ""))

    fun setAiDifficulty(difficulty: AiDifficulty) {
        viewModelScope.launch { 
            val prefs = managePreferencesUseCase.userPreferences.first()
            if (prefs.activeMatchId != null) _showStickyWarning.emit(Unit)
            managePreferencesUseCase.updatePreferences { it.copy(aiDifficulty = difficulty) } 
        }
    }

    @Suppress("unused")
    fun setMatchType(mode: GameMode) {
        viewModelScope.launch { 
            val prefs = managePreferencesUseCase.userPreferences.first()
            if (prefs.activeMatchId != null) _showStickyWarning.emit(Unit)
            managePreferencesUseCase.updatePreferences { it.copy(gameMode = mode) } 
        }
    }

    fun setBoardSize(size: BoardSize) {
        viewModelScope.launch {
            val prefs = managePreferencesUseCase.userPreferences.first()
            if (prefs.activeMatchId != null) _showStickyWarning.emit(Unit)
            val maxDim = maxOf(size.x, size.y, size.z)
            val minWinCondition = minOf(3, maxDim)
            val clampedWinCondition = size.winCondition.coerceIn(minWinCondition, maxDim)
            managePreferencesUseCase.updatePreferences { it.copy(boardSize = size.copy(winCondition = clampedWinCondition)) }
        }
    }

    fun toggleImmersiveMode() = viewModelScope.launch { 
        val current = immersiveMode.value
        managePreferencesUseCase.updatePreferences { it.copy(immersiveMode = !current) } 
    }
    
    fun toggleDynamicColor() = viewModelScope.launch { 
        val current = dynamicColor.value
        managePreferencesUseCase.updatePreferences { it.copy(dynamicColor = !current) } 
    }
    
    fun toggleBgm() = viewModelScope.launch { 
        val current = bgmEnabled.value
        managePreferencesUseCase.updatePreferences { it.copy(bgmEnabled = !current) } 
    }
    
    fun toggleSound() = viewModelScope.launch { 
        val current = soundEnabled.value
        managePreferencesUseCase.updatePreferences { it.copy(soundEnabled = !current) } 
    }
    
    fun toggleBgAnimation() = viewModelScope.launch { 
        val current = bgAnimationEnabled.value
        managePreferencesUseCase.updatePreferences { it.copy(bgAnimationEnabled = !current) } 
    }
    
    fun toggleHaptic() = viewModelScope.launch { 
        val current = hapticEnabled.value
        managePreferencesUseCase.updatePreferences { it.copy(hapticEnabled = !current) } 
    }
    
    fun setBoardStyle(style: BoardStyle) = viewModelScope.launch { managePreferencesUseCase.updatePreferences { it.copy(boardStyle = style) } }
    fun setOrientation(orientation: Orientation) = viewModelScope.launch { managePreferencesUseCase.updatePreferences { it.copy(orientation = orientation) } }
    fun setTheme(theme: AppTheme) = viewModelScope.launch { managePreferencesUseCase.updatePreferences { it.copy(theme = theme) } }
    fun setLanguage(language: AppLanguage) = viewModelScope.launch { managePreferencesUseCase.updatePreferences { it.copy(appLanguage = language) } }
    fun setFirstMoveBehavior(behavior: FirstMoveBehavior) = viewModelScope.launch { 
        val prefs = managePreferencesUseCase.userPreferences.first()
        if (prefs.activeMatchId != null) _showStickyWarning.emit(Unit)
        managePreferencesUseCase.updatePreferences { it.copy(firstMoveBehavior = behavior) } 
    }
    fun setNextMoveBehavior(behavior: NextMoveBehavior) = viewModelScope.launch { 
        val prefs = managePreferencesUseCase.userPreferences.first()
        if (prefs.activeMatchId != null) _showStickyWarning.emit(Unit)
        managePreferencesUseCase.updatePreferences { it.copy(nextMoveBehavior = behavior) } 
    }
    fun setAiStrength(strength: Int) = viewModelScope.launch { 
        val prefs = managePreferencesUseCase.userPreferences.first()
        if (prefs.activeMatchId != null) _showStickyWarning.emit(Unit)
        managePreferencesUseCase.updatePreferences { it.copy(aiStrength = strength) } 
    }
    fun setManualMaxDepth(depth: Int) = viewModelScope.launch { 
        val prefs = managePreferencesUseCase.userPreferences.first()
        if (prefs.activeMatchId != null) _showStickyWarning.emit(Unit)
        managePreferencesUseCase.updatePreferences { it.copy(manualMaxDepth = depth) } 
    }

    fun setP1Symbol(symbol: String) { _p1SymbolLocal.value = symbol }
    fun setP2Symbol(symbol: String) { _p2SymbolLocal.value = symbol }
    fun setP1Color(color: Long) = viewModelScope.launch { managePreferencesUseCase.updatePreferences { it.copy(p1Color = color) } }
    fun setP2Color(color: Long) = viewModelScope.launch { managePreferencesUseCase.updatePreferences { it.copy(p2Color = color) } }
    fun setP1Name(name: String) { _p1NameLocal.value = name }
    fun setP2Name(name: String) { _p2NameLocal.value = name }
}
