package com.tuto.alokkumar.tictactoe.domain

import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.domain.model.MatchResult
import com.tuto.alokkumar.tictactoe.domain.model.PlayerId

/**
 * Pure stateless engine for Tic Tac Toe rules and validation.
 */
object GameRules {

    /**
     * Checks if a cell is empty and game is active.
     */
    fun isValidMove(board: List<PlayerId?>, result: MatchResult, index: Int): Boolean {
        return index in board.indices && 
               board[index] == null && 
               result is MatchResult.Ongoing
    }

    /**
     * Evaluates the board and returns the winner ID and the winning line, or null.
     */
    fun findWinner(board: List<PlayerId?>, winLines: List<List<Int>>): Pair<PlayerId, List<Int>>? {
        for (line in winLines) {
            // Safety check: Ensure all indices in the winning line are within board boundaries
            if (line.any { it >= board.size }) continue
            
            val player = board[line[0]] ?: continue
            if (line.all { board[it] == player }) {
                return player to line
            }
        }
        return null
    }

    /**
     * Returns true if no empty cells remain.
     */
    fun isBoardFull(board: List<PlayerId?>): Boolean {
        return board.none { it == null }
    }

    /**
     * Precomputes all possible winning line indices for a given board size.
     * Uses HashSet for O(1) deduplication to handle large boards (up to 10x10x10).
     */
    fun computeWinningLines(boardSize: BoardSize): List<List<Int>> {
        val x = boardSize.x
        val y = boardSize.y
        val z = boardSize.z
        val target = boardSize.winCondition
        
        // Use Set for O(1) deduplication during generation
        val lineSet = mutableSetOf<List<Int>>()

        fun getIndex(ix: Int, iy: Int, iz: Int): Int {
            if (ix !in 0 until x || iy !in 0 until y || iz !in 0 until z) return -1
            return iz * (x * y) + iy * x + ix
        }

        val directions = listOf(
            Triple(1, 0, 0), Triple(0, 1, 0), Triple(0, 0, 1),
            Triple(1, 1, 0), Triple(1, -1, 0), Triple(1, 0, 1), Triple(1, 0, -1), Triple(0, 1, 1), Triple(0, 1, -1),
            Triple(1, 1, 1), Triple(1, 1, -1), Triple(1, -1, 1), Triple(1, -1, -1)
        )

        for (iz in 0 until z) {
            for (iy in 0 until y) {
                for (ix in 0 until x) {
                    for ((dx, dy, dz) in directions) {
                        val line = mutableListOf<Int>()
                        var valid = true
                        for (step in 0 until target) {
                            val nextIdx = getIndex(ix + dx * step, iy + dy * step, iz + dz * step)
                            if (nextIdx != -1) {
                                line.add(nextIdx)
                            } else {
                                valid = false
                                break
                            }
                        }
                        if (valid && line.size == target) {
                            // Sort to handle bidirectional lines as duplicates
                            lineSet.add(line.sorted())
                        }
                    }
                }
            }
        }
        return lineSet.toList()
    }
}
