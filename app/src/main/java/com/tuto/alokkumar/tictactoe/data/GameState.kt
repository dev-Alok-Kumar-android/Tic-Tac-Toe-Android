package com.tuto.alokkumar.tictactoe.data

import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val board: List<Char?> = List(9) { null },
    val currentPlayer: Char = 'X',
    val winner: Char? = null,
    val xWins: Int = 0,
    val oWins: Int = 0,
    val draws: Int = 0
)

@Serializable
data class GameState3D(
    val board: List<Char?> = List(27) { null },
    val currentPlayer: Char = 'X',
    val winner: Char? = null,
    val activeLayer: Int = 0,
    val winLine: List<Int>? = null,
    val xWins: Int = 0,
    val oWins: Int = 0,
    val draws: Int = 0
)