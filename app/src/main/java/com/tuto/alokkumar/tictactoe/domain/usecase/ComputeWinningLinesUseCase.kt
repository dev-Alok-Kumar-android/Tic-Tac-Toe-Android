package com.tuto.alokkumar.tictactoe.domain.usecase

import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.domain.GameRules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case to calculate all possible winning line combinations for a specific board size.
 * Includes thread-safe caching to avoid redundant heavy O(N^3) calculations.
 */
@Singleton
class ComputeWinningLinesUseCase @Inject constructor() {

    private val cache = mutableMapOf<BoardSize, List<List<Int>>>()

    suspend operator fun invoke(boardSize: BoardSize): List<List<Int>> = withContext(Dispatchers.Default) {
        cache[boardSize] ?: run {
            val lines = GameRules.computeWinningLines(boardSize)
            cache[boardSize] = lines
            lines
        }
    }
}
