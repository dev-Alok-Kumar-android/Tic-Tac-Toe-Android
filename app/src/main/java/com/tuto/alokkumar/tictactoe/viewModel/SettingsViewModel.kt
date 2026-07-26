package com.tuto.alokkumar.tictactoe.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuto.alokkumar.tictactoe.core.pref.PreferencesManager
import com.tuto.alokkumar.tictactoe.core.sound.SoundManager
import com.tuto.alokkumar.tictactoe.data.AiDifficulty
import com.tuto.alokkumar.tictactoe.data.AppLanguage
import com.tuto.alokkumar.tictactoe.data.AppTheme
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.BoardStyle
import com.tuto.alokkumar.tictactoe.data.FirstMoveBehavior
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.NextMoveBehavior
import com.tuto.alokkumar.tictactoe.data.Orientation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * ViewModel managing global application preferences and configuration state.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: PreferencesManager,
    private val soundManager: SoundManager
) : ViewModel() {

    private val _p1NameLocal = MutableStateFlow("Player 1")
    val p1Name = _p1NameLocal.asStateFlow()

    private val _p2NameLocal = MutableStateFlow("Player 2")
    val p2Name = _p2NameLocal.asStateFlow()

    private val _p1SymbolLocal = MutableStateFlow("X")
    val p1Symbol = _p1SymbolLocal.asStateFlow()

    private val _p2SymbolLocal = MutableStateFlow("O")
    val p2Symbol = _p2SymbolLocal.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.userPreferencesFlow.collect { prefs ->
                soundManager.isBgmEnabled = prefs.bgmEnabled
                soundManager.isSoundEnabled = prefs.soundEnabled
                if (!prefs.bgmEnabled) soundManager.stopBgm()
                
                // Sync local state when persistence changes (e.g. from other screens)
                if (_p1NameLocal.value != prefs.p1Name) _p1NameLocal.value = prefs.p1Name
                if (_p2NameLocal.value != prefs.p2Name) _p2NameLocal.value = prefs.p2Name
                if (_p1SymbolLocal.value != prefs.p1Symbol) _p1SymbolLocal.value = prefs.p1Symbol
                if (_p2SymbolLocal.value != prefs.p2Symbol) _p2SymbolLocal.value = prefs.p2Symbol
            }
        }
        
        // Setup debounced persistence for player customization
        setupDebouncedPersistence()
    }

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    private fun setupDebouncedPersistence() {
        viewModelScope.launch {
            _p1NameLocal.debounce(800.milliseconds).distinctUntilChanged().collectLatest { name ->
                preferences.updatePrefs { it.copy(p1Name = name) }
            }
        }
        viewModelScope.launch {
            _p2NameLocal.debounce(800.milliseconds).distinctUntilChanged().collectLatest { name ->
                preferences.updatePrefs { it.copy(p2Name = name) }
            }
        }
        viewModelScope.launch {
            _p1SymbolLocal.debounce(800.milliseconds).distinctUntilChanged().collectLatest { symbol ->
                if (symbol.isNotEmpty()) preferences.updatePrefs { it.copy(p1Symbol = symbol) }
            }
        }
        viewModelScope.launch {
            _p2SymbolLocal.debounce(800.milliseconds).distinctUntilChanged().collectLatest { symbol ->
                if (symbol.isNotEmpty()) preferences.updatePrefs { it.copy(p2Symbol = symbol) }
            }
        }
    }

    fun playBgm(context: android.content.Context) = soundManager.playBgm(context)
    fun pauseBgm() = soundManager.pauseBgm()

    val selectedAiDifficulty = preferences.aiDifficultyFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), AiDifficulty.HARD
    )

    val matchType = preferences.gameModeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), GameMode.VS_AI
    )

    val boardSize = preferences.boardSizeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), BoardSize()
    )

    val theme = preferences.themeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), AppTheme.SYSTEM
    )

    val immersiveMode = preferences.immersiveFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    val bgmEnabled = preferences.bgmEnabledFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val soundEnabled = preferences.soundEnabledFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val dynamicColor = preferences.dynamicColorFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val bgAnimationEnabled = preferences.bgAnimationEnabledFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    val boardStyle = preferences.boardStyleFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), BoardStyle.LAYERED_3D
    )

    val orientation = preferences.orientationFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), Orientation.SYSTEM
    )

    val firstMoveBehavior = preferences.firstMoveBehaviorFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), FirstMoveBehavior.PLAYER_X
    )

    val nextMoveBehavior = preferences.nextMoveBehaviorFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), NextMoveBehavior.ALTERNATING
    )

    val aiStrength = preferences.aiStrengthFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 75
    )

    val isAdvancedAiEnabled = preferences.isAdvancedAiEnabledFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    val manualMaxDepth = preferences.manualMaxDepthFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 6
    )

    val hapticEnabled = preferences.hapticEnabledFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val appLanguage = preferences.userPreferencesFlow.map { it.appLanguage }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.ENGLISH
    )

    val p1Color = preferences.userPreferencesFlow.map { it.p1Color }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0xFFE91E63
    )
    val p2Color = preferences.userPreferencesFlow.map { it.p2Color }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0xFF2196F3
    )

    fun setAiDifficulty(difficulty: AiDifficulty) {
        viewModelScope.launch { preferences.setAiDifficulty(difficulty) }
    }

    fun setMatchType(mode: GameMode) {
        viewModelScope.launch { preferences.setGameMode(mode) }
    }

    fun setBoardSize(size: BoardSize) {
        viewModelScope.launch {
            val maxDim = maxOf(size.x, size.y, size.z)
            val minWinCondition = minOf(3, maxDim)
            val clampedWinCondition = size.winCondition.coerceIn(minWinCondition, maxDim)
            preferences.setBoardSize(size.copy(winCondition = clampedWinCondition))
        }
    }

    fun toggleImmersiveMode() = viewModelScope.launch { preferences.toggleImmersiveMode() }
    fun toggleDynamicColor() = viewModelScope.launch { preferences.toggleDynamicColor() }
    fun toggleBgm() = viewModelScope.launch { preferences.toggleBgm() }
    fun toggleSound() = viewModelScope.launch { preferences.toggleSound() }
    fun toggleBgAnimation() = viewModelScope.launch { preferences.toggleBgAnimation() }
    fun toggleHaptic() = viewModelScope.launch { preferences.updatePrefs { it.copy(hapticEnabled = !it.hapticEnabled) } }
    
    fun setBoardStyle(style: BoardStyle) = viewModelScope.launch { preferences.setBoardStyle(style) }
    fun setOrientation(orientation: Orientation) = viewModelScope.launch { preferences.setOrientation(orientation) }
    fun setTheme(theme: AppTheme) = viewModelScope.launch { preferences.setTheme(theme) }
    fun setLanguage(language: AppLanguage) = viewModelScope.launch { preferences.updatePrefs { it.copy(appLanguage = language) } }
    fun setFirstMoveBehavior(behavior: FirstMoveBehavior) = viewModelScope.launch { preferences.setFirstMoveBehavior(behavior) }
    fun setNextMoveBehavior(behavior: NextMoveBehavior) = viewModelScope.launch { preferences.setNextMoveBehavior(behavior) }
    fun setAiStrength(strength: Int) = viewModelScope.launch { preferences.updatePrefs { it.copy(aiStrength = strength) } }
    fun toggleAdvancedAi() = viewModelScope.launch {
        val current = isAdvancedAiEnabled.value
        preferences.updatePrefs { it.copy(isAdvancedAiEnabled = !current) }
    }
    fun setManualMaxDepth(depth: Int) = viewModelScope.launch { preferences.updatePrefs { it.copy(manualMaxDepth = depth) } }

    fun setP1Symbol(symbol: String) {
        _p1SymbolLocal.value = symbol
    }
    fun setP2Symbol(symbol: String) {
        _p2SymbolLocal.value = symbol
    }
    fun setP1Color(color: Long) = viewModelScope.launch { preferences.updatePrefs { it.copy(p1Color = color) } }
    fun setP2Color(color: Long) = viewModelScope.launch { preferences.updatePrefs { it.copy(p2Color = color) } }
    fun setP1Name(name: String) {
        _p1NameLocal.value = name
    }
    fun setP2Name(name: String) {
        _p2NameLocal.value = name
    }
}
