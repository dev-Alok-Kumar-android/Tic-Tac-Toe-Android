package com.tuto.alokkumar.tictactoe.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuto.alokkumar.tictactoe.core.pref.Preferences
import com.tuto.alokkumar.tictactoe.data.AppTheme
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.core.pref.PreferencesManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel() : ViewModel() {

    val selectedGameMode = Preferences.gameModeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), GameMode.EASY
    )

    val theme = Preferences.themeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), AppTheme.SYSTEM
    )

    val immersiveMode =
        Preferences.immersiveFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val bgmEnabled = Preferences.bgmEnabledFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val soundEnabled = Preferences.soundEnabledFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val dynamicColor = Preferences.dynamicColorFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    fun setGameMode(mode: GameMode) {
        viewModelScope.launch {
            Preferences.setGameMode(mode)
        }
    }

    fun toggleImmersiveMode() {
        viewModelScope.launch {
            Preferences.toggleImmersiveMode()
        }
    }

    fun toggleDynamicColor() {
        viewModelScope.launch {
            Preferences.toggleDynamicColor()
        }
    }

    fun toggleBgm() {
        viewModelScope.launch {
            Preferences.toggleBgm()
        }
    }

    fun toggleSound() {
        viewModelScope.launch {
            Preferences.toggleSound()
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            Preferences.setTheme(theme)
        }
    }
}
