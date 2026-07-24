package com.tuto.alokkumar.tictactoe.data

import kotlinx.serialization.Serializable

/**
 * Represents persistent configuration choices made by the user within settings.
 *
 * @property gameMode The active difficulty mode/AI mode (e.g. PvP, Easy, Medium, Hard).
 * @property boardSize Grid dimensions (X, Y, Z coordinates) and winning streak condition.
 * @property theme App UI styling theme (Light, Dark, or System default).
 * @property bgmEnabled True if looping background music track is enabled.
 * @property soundEnabled True if tactical sound effects are enabled.
 * @property useImmersiveMode True if immersive fullscreen layout is enabled (hides status/navigation bars).
 * @property useDynamicColors True if dynamic system coloring is active on supported Android versions.
 * @property useDynamicColorScheme True if alternative dynamic palette behavior is active.
 * @property appLanguage Active locale selection for application text.
 */
@Serializable
data class UserPreferences(
    val gameMode: GameMode = GameMode.HARD,
    val boardSize: BoardSize = BoardSize(),
    val theme: AppTheme = AppTheme.SYSTEM,
    val bgmEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val useImmersiveMode: Boolean = false,
    val useDynamicColors: Boolean = false,
    val useDynamicColorScheme: Boolean = false,
    val appLanguage: AppLanguage = AppLanguage.ENGLISH,
)

/**
 * Game session modes defining AI difficulty levels or local versus modes.
 */
@Serializable
enum class GameMode {
    /** Player-versus-Player local multiplayer. */
    PVP,
    /** Low difficulty AI opponent that selects completely random cells. */
    EASY,
    /** Moderate difficulty AI opponent that checks for direct win/blocks. */
    MEDIUM,
    /** Challenging difficulty AI utilizing Minimax with Alpha-Beta pruning. */
    HARD
}

/**
 * Defines the dimensions of a 2D or 3D Tic Tac Toe board.
 *
 * @property x Number of columns (horizontal dimension).
 * @property y Number of rows (vertical dimension).
 * @property z Number of layers (depth dimension for 3D play).
 * @property winCondition The contiguous streak length of identical tokens required to win.
 */
@Serializable
data class BoardSize(
    val x: Int = 3,
    val y: Int = 3,
    val z: Int = 1,
    val winCondition: Int = 3
)

/** Application visual layout theme modes. */
@Serializable
enum class AppTheme { LIGHT, DARK, SYSTEM }

/** Application supported localization languages. */
@Serializable
enum class AppLanguage { ENGLISH, HINDI }

/** Aesthetic render style of the grid. */
@Serializable
enum class BoardStyle { CLASSIC, LAYERED_3D }

/** Device orientation lock modes. */
@Serializable
enum class Orientation { PORTRAIT, LANDSCAPE, AUTO, SYSTEM }