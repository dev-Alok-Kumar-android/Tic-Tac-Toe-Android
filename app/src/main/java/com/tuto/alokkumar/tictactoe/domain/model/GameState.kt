package com.tuto.alokkumar.tictactoe.domain.model

import com.tuto.alokkumar.tictactoe.data.BoardSize
import java.util.UUID

/**
 * Pure Kotlin Domain Model for the game state.
 * Dynamic board sizing ensures compatibility with 2D and 3D play.
 */
data class GameState(
    val matchId: String = UUID.randomUUID().toString(),
    val boardSize: BoardSize = BoardSize(),
    val board: List<PlayerId?> = List(boardSize.x * boardSize.y * boardSize.z) { null },
    val currentPlayerId: PlayerId = PlayerId.P1,
    val result: MatchResult = MatchResult.Ongoing,
    val winLine: List<Int>? = null,
    val lastMove: Int? = null,
    val p1Wins: Int = 0,
    val p2Wins: Int = 0,
    val draws: Int = 0
) {
    val isFinished: Boolean get() = result !is MatchResult.Ongoing
}
