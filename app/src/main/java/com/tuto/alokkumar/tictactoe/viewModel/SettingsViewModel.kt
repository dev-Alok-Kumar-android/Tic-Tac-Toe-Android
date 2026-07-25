package com.tuto.alokkumar.tictactoe.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuto.alokkumar.tictactoe.core.pref.PreferencesManager
import com.tuto.alokkumar.tictactoe.core.sound.SoundManager
import com.tuto.alokkumar.tictactoe.data.AppLanguage
import com.tuto.alokkumar.tictactoe.data.AppTheme
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.BoardStyle
import com.tuto.alokkumar.tictactoe.data.FirstMoveBehavior
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.NextMoveBehavior
import com.tuto.alokkumar.tictactoe.data.Orientation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel managing global application preferences and configuration state.
 *
 * Exposes observable [kotlinx.coroutines.flow.StateFlow]s mapped directly from DataStore flows
 * using [stateIn] for reactive UI consumption. Handles validation for complex types like [BoardSize].
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: PreferencesManager,
    private val soundManager: SoundManager
) : ViewModel() {

    init {
        // Synchronize SoundManager flags with user preferences
        viewModelScope.launch {
            preferences.userPreferencesFlow.collect { prefs ->
                soundManager.isBgmEnabled = prefs.bgmEnabled
                soundManager.isSoundEnabled = prefs.soundEnabled
                
                // Immediately stop music resources if it was disabled
                if (!prefs.bgmEnabled) {
                    soundManager.stopBgm()
                }
            }
        }
    }

    fun playBgm(context: android.content.Context) {
        soundManager.playBgm(context)
    }

    fun pauseBgm() {
        soundManager.pauseBgm()
    }

    /** Current difficulty setting for AI matches. Defaults to [GameMode.HARD]. */
    val selectedGameMode = preferences.gameModeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), GameMode.HARD
    )

    /** Configured dimensions for 2D/3D boards. */
    val boardSize = preferences.boardSizeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), BoardSize()
    )

    /** Active visual theme (Light, Dark, System). */
    val theme = preferences.themeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), AppTheme.SYSTEM
    )

    /** Flag for fullscreen immersive layout behavior. */
    val immersiveMode =
        preferences.immersiveFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    /** Flag indicating if background music should play. */
    val bgmEnabled = preferences.bgmEnabledFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    /** Flag indicating if action sound effects should play. */
    val soundEnabled = preferences.soundEnabledFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    /** Flag for Material You dynamic wallpaper colors. */
    val dynamicColor = preferences.dynamicColorFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    /** Flag for animated canvas line background effects. */
    val bgAnimationEnabled = preferences.bgAnimationEnabledFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    /** Visual render style of the grid (Classic or 3D Layered). */
    val boardStyle = preferences.boardStyleFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), BoardStyle.LAYERED_3D
    )

    /** Configured device screen orientation. */
    val orientation = preferences.orientationFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), Orientation.SYSTEM
    )

    /** Behavior for the first move of a session. */
    val firstMoveBehavior = preferences.firstMoveBehaviorFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), FirstMoveBehavior.PLAYER_X
    )

    /** Behavior for starting subsequent games. */
    val nextMoveBehavior = preferences.nextMoveBehaviorFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), NextMoveBehavior.ALTERNATING
    )

    /** Granular AI skill level (1-100). */
    val aiStrength = preferences.aiStrengthFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 75
    )

    /** Flag for manual AI depth override. */
    val isAdvancedAiEnabled = preferences.isAdvancedAiEnabledFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    /** Manual search depth limit. */
    val manualMaxDepth = preferences.manualMaxDepthFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 6
    )

    /** Active application language. */
    val appLanguage = preferences.userPreferencesFlow.map { it.appLanguage }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.ENGLISH
    )

    /** Updates and persists the [GameMode]. */
    fun setGameMode(mode: GameMode) {
        viewModelScope.launch {
            preferences.setGameMode(mode)
        }
    }

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

    /** Toggles system bar visibility behavior. */
    fun toggleImmersiveMode() {
        viewModelScope.launch {
            preferences.toggleImmersiveMode()
        }
    }

    /** Toggles dynamic system colors. */
    fun toggleDynamicColor() {
        viewModelScope.launch {
            preferences.toggleDynamicColor()
        }
    }

    /** Toggles background music state. */
    fun toggleBgm() {
        viewModelScope.launch {
            preferences.toggleBgm()
        }
    }

    /** Toggles tactical sound effects. */
    fun toggleSound() {
        viewModelScope.launch {
            preferences.toggleSound()
        }
    }

    /** Toggles canvas background animations. */
    fun toggleBgAnimation() {
        viewModelScope.launch {
            preferences.toggleBgAnimation()
        }
    }

    /** Updates aesthetic grid rendering style. */
    fun setBoardStyle(style: BoardStyle) {
        viewModelScope.launch {
            preferences.setBoardStyle(style)
        }
    }

    /** Updates device screen orientation lock preference. */
    fun setOrientation(orientation: Orientation) {
        viewModelScope.launch {
            preferences.setOrientation(orientation)
        }
    }

    /** Updates application visual theme mode. */
    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            preferences.setTheme(theme)
        }
    }

    /** Updates application language. */
    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            preferences.updatePrefs { it.copy(appLanguage = language) }
        }
    }

    /** Updates [FirstMoveBehavior]. */
    fun setFirstMoveBehavior(behavior: FirstMoveBehavior) {
        viewModelScope.launch {
            preferences.setFirstMoveBehavior(behavior)
        }
    }

    /** Updates [NextMoveBehavior]. */
    fun setNextMoveBehavior(behavior: NextMoveBehavior) {
        viewModelScope.launch {
            preferences.setNextMoveBehavior(behavior)
        }
    }

    /** Updates AI skill level (1-100). */
    fun setAiStrength(strength: Int) {
        viewModelScope.launch {
            preferences.updatePrefs { it.copy(aiStrength = strength) }
        }
    }

    /** Toggles advanced AI settings section. */
    fun toggleAdvancedAi() {
        viewModelScope.launch {
            val current = isAdvancedAiEnabled.value
            preferences.updatePrefs { it.copy(isAdvancedAiEnabled = !current) }
        }
    }

    /** Updates manual search depth. */
    fun setManualMaxDepth(depth: Int) {
        viewModelScope.launch {
            preferences.updatePrefs { it.copy(manualMaxDepth = depth) }
        }
    }
}
