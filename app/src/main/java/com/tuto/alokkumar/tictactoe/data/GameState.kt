package com.tuto.alokkumar.tictactoe.data

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

/**
 * Persistable entity for game state storage.
 */
@Keep
@Serializable
data class GameStateEntity(
    val board: List<String?>,
    val currentPlayerId: String,
    val winnerId: String?,
    val isDraw: Boolean,
    val winLine: List<Int>?,
    val lastMove: Int?,
    val p1Wins: Int,
    val p2Wins: Int,
    val draws: Int,
    val boardSize: BoardSize
)

/**
 * Summary statistics for game history dashboard.
 */
data class GameStats(
    val totalGames: Int = 0,
    val p1Wins: Int = 0,
    val p2Wins: Int = 0,
    val draws: Int = 0,
    val winRate: Int = 0
)
