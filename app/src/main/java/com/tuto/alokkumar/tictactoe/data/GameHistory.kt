package com.tuto.alokkumar.tictactoe.data

import kotlinx.serialization.Serializable

/**
 * Represents a historical record of a completed or saved Tic Tac Toe game.
 *
 * @property dateMillis Timestamp in milliseconds indicating when the match occurred or was saved.
 * @property difficulty The [AiDifficulty] configuration used during the match.
 * @property state The final or saved snapshot of [GameState] representing the match board and metrics.
 * @property gameMode The [GameMode] (PvP vs AI) used during the match.
 */
@Serializable
data class GameHistory(
    val matchId: String,
    val dateMillis: Long = System.currentTimeMillis(),
    val difficulty: AiDifficulty,
    val state: GameState,
    val gameMode: GameMode = GameMode.VS_AI,
    val humanSymbol: String = "X",
    val p1Symbol: String = "X",
    val p2Symbol: String = "O",
    val p1Name: String = "Player 1",
    val p2Name: String = "Player 2",
    val p1Color: Long = 0xFFE91E63,
    val p2Color: Long = 0xFF2196F3
)
