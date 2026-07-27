package com.tuto.alokkumar.tictactoe.domain.model

/**
 * Statistical likelihood of match outcomes based on simulation.
 */
data class MatchProbabilities(
    val p1WinChance: Float,
    val p2WinChance: Float,
    val drawChance: Float,
    val sampleSize: Int
)
