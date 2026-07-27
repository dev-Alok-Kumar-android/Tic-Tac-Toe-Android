package com.tuto.alokkumar.tictactoe

import androidx.annotation.Keep
import com.tuto.alokkumar.tictactoe.data.AiDifficulty
import kotlinx.serialization.Serializable

@Keep
@Serializable
sealed interface Route {
    @Keep
    @Serializable
    data object Menu : Route

    @Keep
    @Serializable
    data class Game(
        val mode: AiDifficulty?,
        val boardSize: com.tuto.alokkumar.tictactoe.data.BoardSize
    ) : Route

    @Keep
    @Serializable
    data object RestoreGame : Route

    @Keep
    @Serializable
    data object History : Route

    @Keep
    @Serializable
    data object Settings : Route

    @Keep
    @Serializable
    data object About : Route
}
