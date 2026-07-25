package com.tuto.alokkumar.tictactoe

import com.tuto.alokkumar.tictactoe.data.GameMode
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Menu : Route

    @Serializable
    data class Game(
        val mode: GameMode
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
