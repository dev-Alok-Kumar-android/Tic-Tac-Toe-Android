package com.tuto.alokkumar.tictactoe.core.pref

import android.content.Context
import com.tuto.alokkumar.tictactoe.data.AppTheme
import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.data.GameMode


object Preferences {

    private lateinit var manager: PreferencesManager

    fun init(context: Context) {
        if (!::manager.isInitialized) {
            manager = PreferencesManager(context.applicationContext)
        }
    }

    val bgmEnabledFlow get() = manager.bgmEnabledFlow
    val mode get() = manager.gameModeFlow
    val soundEnabledFlow get() = manager.soundEnabledFlow
    val immersiveFlow get() = manager.immersiveFlow
    val theme get() = manager.themeFlow
    val dynamicColorFlow get() = manager.dynamicColorFlow
    val gameModeFlow get() = manager.gameModeFlow
    val themeFlow get() = manager.themeFlow

    suspend fun setGameMode(mode: GameMode) = manager.setGameMode(mode)
    suspend fun setTheme(theme: AppTheme) = manager.setTheme(theme)
    suspend fun toggleImmersiveMode() = manager.toggleImmersiveMode()
    suspend fun toggleDynamicColor() = manager.toggleDynamicColor()
    suspend fun toggleSound() = manager.toggleSound()
    suspend fun toggleBgm() = manager.toggleBgm()
    suspend fun addGameHistory(history: GameHistory) = manager.addGameHistory(history)
    fun getGameHistoryFlow() = manager.getGameHistoryFlow()
    suspend fun removeGameHistory(history: GameHistory) = manager.removeGameHistory(history)
    suspend fun clearAllGameHistory() = manager.clearAllGameHistory()
}
