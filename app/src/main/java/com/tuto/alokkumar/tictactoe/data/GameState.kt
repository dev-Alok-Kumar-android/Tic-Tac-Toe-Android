package com.tuto.alokkumar.tictactoe.data

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Immutable snapshot representing the active or final state of a game.
 *
 * @property matchId Unique identifier for the specific game session.
 * @property board Flattened list of cell contents. Represents a 3D grid layout when [BoardSize.z] > 1.
 *                 Cells contain 'X', 'O', or null if empty.
 * @property currentPlayer Character representation of the player whose turn it currently is ('X' or 'O').
 * @property winner The winning player character ('X' or 'O'), 'D' for a draw, or null if the match is ongoing.
 * @property winLine Indices of the board cells forming the winning line sequence, or null if no winner.
 * @property lastMove Index of the last placed token, used for highlighting the latest move.
 * @property xWins Total count of wins achieved by player 'X' during the current session.
 * @property oWins Total count of wins achieved by player 'O' during the current session.
 * @property draws Total count of draws/ties occurred during the current session.
 * @property boardSize Dimensions of the board layout, represented by a [BoardSize] object.
 */
@Serializable
data class GameState(
    val matchId: String = UUID.randomUUID().toString(),
    val board: List<String?> = List(9) { null },
    val currentPlayer: String = "X",
    val winner: String? = null,
    val winLine: List<Int>? = null,
    val lastMove: Int? = null,
    val xWins: Int = 0,
    val oWins: Int = 0,
    val draws: Int = 0,
    val boardSize: BoardSize = BoardSize()
)

/**
 * Summary statistics for game history dashboard.
 */
data class GameStats(
    val totalGames: Int = 0,
    val xWins: Int = 0,
    val oWins: Int = 0,
    val draws: Int = 0,
    val winRate: Int = 0
)
