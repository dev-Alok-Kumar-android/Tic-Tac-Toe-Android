package com.tuto.alokkumar.tictactoe

import com.tuto.alokkumar.tictactoe.data.AiDifficulty
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Menu : Route

    @Serializable
    data class Game(
        val mode: AiDifficulty?,
        val boardSize: com.tuto.alokkumar.tictactoe.data.BoardSize
    ) : Route

    @Serializable
    data object RestoreGame : Route

    @Serializable
    data object History : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object About : Route
}
