package com.tuto.alokkumar.tictactoe.core.pref

import android.content.Context
import com.tuto.alokkumar.tictactoe.data.AppTheme
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.BoardStyle
import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.Orientation

/**
 * Global singleton entry point for user preference access and modifications.
 *
 * Wraps [PreferencesManager] to expose DataStore flows and suspend operations globally
 * throughout the app.
 */
object Preferences {

    private lateinit var manager: PreferencesManager

    /**
     * Initializes the underlying [PreferencesManager] instance.
     *
     * @param context Application context used to configure DataStore.
     */
    fun init(context: Context) {
        if (!::manager.isInitialized) {
            manager = PreferencesManager(context.applicationContext)
        }
    }

    /** Flow indicating whether Background Music (BGM) is enabled. */
    val bgmEnabledFlow get() = manager.bgmEnabledFlow
    /** Flow emitting the current game difficulty/mode setting. */
    val mode get() = manager.gameModeFlow
    /** Flow emitting the selected board dimensions configuration. */
    val boardSizeFlow get() = manager.boardSizeFlow
    /** Flow indicating whether game sound effects are enabled. */
    val soundEnabledFlow get() = manager.soundEnabledFlow
    /** Flow indicating whether fullscreen immersive mode is active. */
    val immersiveFlow get() = manager.immersiveFlow
    /** Flow emitting current application theme mode. */
    val theme get() = manager.themeFlow
    /** Flow indicating whether Material You Dynamic Color is enabled. */
    val dynamicColorFlow get() = manager.dynamicColorFlow
    /** Flow emitting the current game mode. */
    val gameModeFlow get() = manager.gameModeFlow
    /** Flow emitting the theme preference. */
    val themeFlow get() = manager.themeFlow

    /** Flow emitting whether animated background effects are turned on. */
    val bgAnimationEnabledFlow get() = manager.bgAnimationEnabledFlow
    /** Flow emitting selected board visualization style (e.g. Classic vs 3D Layered). */
    val boardStyleFlow get() = manager.boardStyleFlow
    /** Flow emitting selected [Orientation]. */
    val orientationFlow get() = manager.orientationFlow

    /** Updates selected [GameMode]. */
    suspend fun setGameMode(mode: GameMode) = manager.setGameMode(mode)
    /** Updates selected [BoardSize]. */
    suspend fun setBoardSize(size: BoardSize) = manager.setBoardSize(size)
    /** Updates selected [AppTheme]. */
    suspend fun setTheme(theme: AppTheme) = manager.setTheme(theme)
    /** Toggles immersive fullscreen setting. */
    suspend fun toggleImmersiveMode() = manager.toggleImmersiveMode()
    /** Toggles dynamic color system theme preference. */
    suspend fun toggleDynamicColor() = manager.toggleDynamicColor()
    /** Toggles sound effects state. */
    suspend fun toggleSound() = manager.toggleSound()
    /** Toggles background music state. */
    suspend fun toggleBgm() = manager.toggleBgm()
    /** Toggles background canvas animation state. */
    suspend fun toggleBgAnimation() = manager.toggleBgAnimation()
    /** Updates board rendering [BoardStyle]. */
    suspend fun setBoardStyle(style: BoardStyle) = manager.setBoardStyle(style)
    /** Sets app orientation preference. */
    suspend fun setOrientation(orientation: Orientation) = manager.setOrientation(orientation)
    /** Persists a new [GameHistory] item. */
    suspend fun addGameHistory(history: GameHistory) = manager.addGameHistory(history)
    /** Flow emitting the stored list of played game history records. */
    fun getGameHistoryFlow() = manager.getGameHistoryFlow()
    /** Removes a specific [GameHistory] record from persistent storage. */
    suspend fun removeGameHistory(history: GameHistory) = manager.removeGameHistory(history)
    /** Clears all stored game history entries. */
    suspend fun clearAllGameHistory() = manager.clearAllGameHistory()
}
