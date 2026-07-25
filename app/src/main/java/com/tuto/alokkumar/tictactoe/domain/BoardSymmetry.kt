package com.tuto.alokkumar.tictactoe.domain

import com.tuto.alokkumar.tictactoe.data.BoardSize

/**
 * Utility for board symmetry reduction (Rotation & Reflections).
 * Primarily used to reduce search space in Minimax.
 */
object BoardSymmetry {

    /**
     * Returns a canonical string representation of the board state.
     * Any two symmetric boards will return the same canonical string.
     */
    fun getCanonicalForm(board: List<Char?>, size: BoardSize): String {
        // 3D symmetry is complex, fallback to literal string for now
        if (size.z > 1 || size.x != size.y) {
            return board.joinToString("") { it?.toString() ?: "." }
        }

        val symmetries = getAll2DSymmetries(board, size.x)
        return symmetries.minOfOrNull { it.joinToString("") { c -> c?.toString() ?: "." } } ?: ""
    }

    private fun getAll2DSymmetries(board: List<Char?>, n: Int): List<List<Char?>> {
        val results = mutableListOf<List<Char?>>()
        var current = board
        
        // 4 Rotations
        repeat(4) {
            results.add(current)
            current = rotate90(current, n)
        }
        
        // Reflection
        val flipped = flip(board, n)
        current = flipped
        repeat(4) {
            results.add(current)
            current = rotate90(current, n)
        }
        
        return results
    }

    private fun rotate90(board: List<Char?>, n: Int): List<Char?> {
        val next = MutableList<Char?>(n * n) { null }
        for (r in 0 until n) {
            for (c in 0 until n) {
                next[c * n + (n - 1 - r)] = board[r * n + c]
            }
        }
        return next
    }

    private fun flip(board: List<Char?>, n: Int): List<Char?> {
        val next = MutableList<Char?>(n * n) { null }
        for (r in 0 until n) {
            for (c in 0 until n) {
                next[r * n + (n - 1 - c)] = board[r * n + c]
            }
        }
        return next
    }
}
