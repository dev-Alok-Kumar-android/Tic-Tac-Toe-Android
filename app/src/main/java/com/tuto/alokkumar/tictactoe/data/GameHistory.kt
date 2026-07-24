package com.tuto.alokkumar.tictactoe.data

import kotlinx.serialization.Serializable

/**
 * Represents a historical record of a completed or saved Tic Tac Toe game.
 *
 * @property dateMillis Timestamp in milliseconds indicating when the match occurred or was saved.
 * @property mode The [GameMode] configuration used during the match.
 * @property state The final or saved snapshot of [GameState] representing the match board and metrics.
 */
@Serializable
data class GameHistory(
    val dateMillis: Long = System.currentTimeMillis(),
    val mode: GameMode,
    val state: GameState
)
