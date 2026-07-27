package com.tuto.alokkumar.tictactoe.domain.model

import com.tuto.alokkumar.tictactoe.data.AiDifficulty

/**
 * Represents a participant in a Tic Tac Toe match.
 */
sealed interface Player {
    val id: PlayerId
    val name: String
    val symbol: String
    val color: Long

    /**
     * Human player using the local device.
     */
    data class Local(
        override val id: PlayerId,
        override val name: String,
        override val symbol: String,
        override val color: Long
    ) : Player

    /**
     * Automated opponent powered by Minimax.
     */
    data class Ai(
        override val id: PlayerId,
        override val name: String,
        override val symbol: String,
        override val color: Long,
        val difficulty: AiDifficulty,
        val strength: Int = 100,
        val manualMaxDepth: Int = 6
    ) : Player

    /**
     * Potential future implementation for Network/Bluetooth play.
     */
    @Suppress("unused")
    data class Remote(
        override val id: PlayerId,
        override val name: String,
        override val symbol: String,
        override val color: Long,
        val connectionId: String
    ) : Player
}
