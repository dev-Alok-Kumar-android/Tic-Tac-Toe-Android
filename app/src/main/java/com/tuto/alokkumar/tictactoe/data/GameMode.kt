package com.tuto.alokkumar.tictactoe.data

import kotlinx.serialization.Serializable

@Serializable
enum class GameMode {
    PVP,
    EASY,
    MEDIUM,
    HARD
}