package com.tuto.alokkumar.tictactoe.data

import kotlinx.serialization.Serializable

@Serializable
data class GameHistory(
    val dateMillis: Long = System.currentTimeMillis(),
    val mode: GameMode,
    val state: GameState
)
