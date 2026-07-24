package com.tuto.alokkumar.tictactoe.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuto.alokkumar.tictactoe.core.pref.Preferences
import com.tuto.alokkumar.tictactoe.data.AppTheme
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.BoardStyle
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.Orientation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel managing global application preferences and configuration state.
 *
 * Exposes observable [kotlinx.coroutines.flow.StateFlow]s mapped directly from DataStore flows
 * using [stateIn] for reactive UI consumption. Handles validation for complex types like [BoardSize].
 */
class SettingsViewModel() : ViewModel() {

    /** Current difficulty setting for AI matches. Defaults to [GameMode.EASY]. */
    val selectedGameMode = Preferences.gameModeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), GameMode.EASY
    )

    /** Configured dimensions for 2D/3D boards. */
    val boardSize = Preferences.boardSizeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), BoardSize()
    )

    /** Active visual theme (Light, Dark, System). */
    val theme = Preferences.themeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), AppTheme.SYSTEM
    )

    /** Flag for fullscreen immersive layout behavior. */
    val immersiveMode =
        Preferences.immersiveFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    /** Flag indicating if background music should play. */
    val bgmEnabled = Preferences.bgmEnabledFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    /** Flag indicating if action sound effects should play. */
    val soundEnabled = Preferences.soundEnabledFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    /** Flag for Material You dynamic wallpaper colors. */
    val dynamicColor = Preferences.dynamicColorFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    /** Flag for animated canvas line background effects. */
    val bgAnimationEnabled = Preferences.bgAnimationEnabledFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    /** Visual render style of the grid (Classic or 3D Layered). */
    val boardStyle = Preferences.boardStyleFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), BoardStyle.CLASSIC
    )

    /** Configured device screen orientation. */
    val orientation = Preferences.orientationFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), Orientation.SYSTEM
    )

    /** Updates and persists the [GameMode]. */
    fun setGameMode(mode: GameMode) {
        viewModelScope.launch {
            Preferences.setGameMode(mode)
        }
    }

    /**
     * Persists board dimensions.
     * Includes sanity checks to ensure winCondition is achievable within provided bounds.
     */
    fun setBoardSize(size: BoardSize) {
        viewModelScope.launch {
            // Validate winCondition: can't be more than max(x, y, z)
            val maxDimension = maxOf(size.x, size.y, size.z)
            val validatedSize = if (size.winCondition > maxDimension) {
                size.copy(winCondition = maxDimension)
            } else if (size.winCondition < 3) {
                size.copy(winCondition = 3)
            } else {
                size
            }
            Preferences.setBoardSize(validatedSize)
        }
    }

    /** Toggles system bar visibility behavior. */
    fun toggleImmersiveMode() {
        viewModelScope.launch {
            Preferences.toggleImmersiveMode()
        }
    }

    /** Toggles dynamic system colors. */
    fun toggleDynamicColor() {
        viewModelScope.launch {
            Preferences.toggleDynamicColor()
        }
    }

    /** Toggles background music state. */
    fun toggleBgm() {
        viewModelScope.launch {
            Preferences.toggleBgm()
        }
    }

    /** Toggles tactical sound effects. */
    fun toggleSound() {
        viewModelScope.launch {
            Preferences.toggleSound()
        }
    }

    /** Toggles canvas background animations. */
    fun toggleBgAnimation() {
        viewModelScope.launch {
            Preferences.toggleBgAnimation()
        }
    }

    /** Updates aesthetic grid rendering style. */
    fun setBoardStyle(style: BoardStyle) {
        viewModelScope.launch {
            Preferences.setBoardStyle(style)
        }
    }

    /** Updates device screen orientation lock preference. */
    fun setOrientation(orientation: Orientation) {
        viewModelScope.launch {
            Preferences.setOrientation(orientation)
        }
    }

    /** Updates application visual theme mode. */
    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            Preferences.setTheme(theme)
        }
    }
}
