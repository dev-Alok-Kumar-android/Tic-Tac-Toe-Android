package com.tuto.alokkumar.tictactoe.domain.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

/**
 * Stable identifier for participants.
 */
@Keep
@Serializable
enum class PlayerId { P1, P2 }

/**
 * Represents the current outcome of a match.
 */
sealed interface MatchResult {
    data object Ongoing : MatchResult
    data object Draw : MatchResult
    data class Winner(val id: PlayerId) : MatchResult
}

/**
 * Type-safe feedback events for hardware (Sounds/Haptics).
 */
enum class GameFeedback {
    MOVE, WIN, LOSE, DRAW
}
