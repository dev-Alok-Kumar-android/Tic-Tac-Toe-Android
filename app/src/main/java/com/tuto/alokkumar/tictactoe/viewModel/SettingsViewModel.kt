package com.tuto.alokkumar.tictactoe.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.PreferencesManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val prefs: PreferencesManager) : ViewModel() {

    val selectedGameMode = prefs.gameModeFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), GameMode.EASY
    )

    val immersiveMode =
        prefs.immersiveFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val bgmEnabled = prefs.bgmEnabledFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val soundEnabled = prefs.soundEnabledFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    val themeDark = prefs.themeDarkFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    fun setGameMode(mode: GameMode) {
        viewModelScope.launch {
            prefs.setGameMode(mode)
        }
    }

    fun toggleImmersiveMode() {
        viewModelScope.launch {
            prefs.toggleImmersiveMode()
        }
    }

    fun toggleBgm() {
        viewModelScope.launch {
            prefs.toggleBgm()
        }
    }

    fun toggleSound() {
        viewModelScope.launch {
            prefs.toggleSound()
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            prefs.toggleDarkTheme()
        }
    }
}
