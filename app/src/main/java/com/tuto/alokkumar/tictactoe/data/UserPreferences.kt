package com.tuto.alokkumar.tictactoe.data

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Represents persistent configuration choices made by the user within settings.
 *
 * @property aiDifficulty The active difficulty mode for the AI (e.g. Easy, Medium, Hard).
 * @property boardSize Grid dimensions (X, Y, Z coordinates) and winning streak condition.
 * @property theme App UI styling theme (Light, Dark, or System default).
 * @property bgmEnabled True if looping background music track is enabled.
 * @property soundEnabled True if tactical sound effects are enabled.
 * @property immersiveMode True if immersive fullscreen layout is enabled (hides status/navigation bars).
 * @property dynamicColor True if dynamic system coloring is active on supported Android versions.
 * @property bgAnimationEnabled True if animated background lines are enabled.
 * @property boardStyle Visual render style of the board (Classic or 3D Layered).
 * @property orientation Fixed screen orientation lock preference.
 * @property appLanguage Active locale selection for application text.
 * @property firstMoveBehavior Who starts the very first game of a session.
 * @property nextMoveBehavior Logic for who starts subsequent games in a session.
 * @property aiStrength Granular AI skill level (1-100).
 * @property manualMaxDepth User-defined maximum Minimax search depth.
 * @property hapticEnabled True if tactile vibration feedback is active.
 * @property gameMode High-level match category (PvP vs AI).
 */
@Keep
@Serializable
data class UserPreferences(
    val aiDifficulty: AiDifficulty = AiDifficulty.HARD,
    val boardSize: BoardSize = BoardSize(),
    val theme: AppTheme = AppTheme.SYSTEM,
    val bgmEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val immersiveMode: Boolean = false,
    val dynamicColor: Boolean = false,
    val bgAnimationEnabled: Boolean = false,
    val boardStyle: BoardStyle = BoardStyle.LAYERED_3D,
    val orientation: Orientation = Orientation.SYSTEM,
    val appLanguage: AppLanguage = AppLanguage.ENGLISH,
    val firstMoveBehavior: FirstMoveBehavior = FirstMoveBehavior.PLAYER_X,
    val nextMoveBehavior: NextMoveBehavior = NextMoveBehavior.ALTERNATING,
    val aiStrength: Int = 75,
    val manualMaxDepth: Int = 6,
    val hapticEnabled: Boolean = true,
    val gameMode: GameMode = GameMode.VS_AI,
    val p1Symbol: String = "X",
    val p2Symbol: String = "O",
    val p1Name: String = "Player 1",
    val p2Name: String = "Player 2",
    val p1Color: Long = 0xFFE91E63,
    val p2Color: Long = 0xFF2196F3,
    val activeMatchId: String? = null
)

@Keep
@Serializable
enum class GameMode { PVP, VS_AI }

@Keep
@Serializable
enum class AiDifficulty { EASY, MEDIUM, HARD, IMPOSSIBLE }

@Keep
@Serializable
enum class FirstMoveBehavior { PLAYER_X, PLAYER_O, RANDOM }

@Keep
@Serializable
enum class NextMoveBehavior { FIXED, ALTERNATING, WINNER_STARTS, LOSER_STARTS, RANDOM }

/**
 * Defines the dimensions of a 2D or 3D Tic Tac Toe board.
 *
 * @property x Number of columns (horizontal dimension).
 * @property y Number of rows (vertical dimension).
 * @property z Number of layers (depth dimension for 3D play).
 * @property winCondition The contiguous streak length of identical tokens required to win.
 */
@Keep
@Serializable
@Parcelize
data class BoardSize(
    val x: Int = 3,
    val y: Int = 3,
    val z: Int = 1,
    val winCondition: Int = 3
) : Parcelable

@Keep
@Serializable
enum class AppTheme { LIGHT, DARK, SYSTEM }

@Keep
@Serializable
enum class AppLanguage { ENGLISH, HINDI }

@Keep
@Serializable
enum class BoardStyle { CLASSIC, LAYERED_3D }

@Keep
@Serializable
enum class Orientation { PORTRAIT, LANDSCAPE, AUTO, SYSTEM }
