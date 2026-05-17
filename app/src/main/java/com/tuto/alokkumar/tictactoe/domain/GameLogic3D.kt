package com.tuto.alokkumar.tictactoe.domain

class GameLogic3D {

    // Index calculation: layer * 9 + row * 3 + col
    val WIN_LINES = listOf(
        // --- 24 lines WITHIN LAYERS (8 lines per layer * 3 layers) ---
        // Layer 0
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Rows
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Cols
        listOf(0, 4, 8), listOf(2, 4, 6),                 // Diagonals

        // Layer 1
        listOf(9, 10, 11), listOf(12, 13, 14), listOf(15, 16, 17),
        listOf(9, 12, 15), listOf(10, 13, 16), listOf(11, 14, 17),
        listOf(9, 13, 17), listOf(11, 13, 15),

        // Layer 2
        listOf(18, 19, 20), listOf(21, 22, 23), listOf(24, 25, 26),
        listOf(18, 21, 24), listOf(19, 22, 25), listOf(20, 23, 26),
        listOf(18, 22, 26), listOf(20, 22, 24),

        // --- 9 VERTICAL PILLARS (Same row/col across layers) ---
        listOf(0, 9, 18), listOf(1, 10, 19), listOf(2, 11, 20),
        listOf(3, 12, 21), listOf(4, 13, 22), listOf(5, 14, 23),
        listOf(6, 15, 24), listOf(7, 16, 25), listOf(8, 17, 26),

        // --- 12 CROSS-LAYER DIAGONALS ---
        // Fixed Column diagonals
        listOf(0, 12, 24), listOf(6, 12, 18), // Col 0
        listOf(1, 13, 25), listOf(7, 13, 19), // Col 1
        listOf(2, 14, 26), listOf(8, 14, 20), // Col 2
        // Fixed Row diagonals
        listOf(0, 10, 20), listOf(2, 10, 18), // Row 0
        listOf(3, 13, 23), listOf(5, 13, 21), // Row 1
        listOf(6, 16, 26), listOf(8, 16, 24), // Row 2

        // --- 4 MAIN SPACE DIAGONALS (Corner to opposite Corner) ---
        listOf(0, 13, 26), // Top-Front-Left to Bottom-Back-Right
        listOf(2, 13, 24), // Top-Front-Right to Bottom-Back-Left
        listOf(6, 13, 20), // Top-Back-Left to Bottom-Front-Right
        listOf(8, 13, 18)  // Top-Back-Right to Bottom-Front-Left
    )

    private val board = MutableList<Char?>(27) { null }
    var currentPlayer: Char = 'X'
        private set

    var winner: Char? = null
        private set

    var winningLine: List<Int>? = null
        private set

    fun reset() {
        board.fill(null)
        currentPlayer = 'X'
        winner = null
    }

    fun getBoard(): List<Char?> = board.toList()

    fun makeMove(index: Int): Boolean {
        if (index !in 0..26 || board[index] != null || winner != null) return false

        board[index] = currentPlayer
        checkWinner()
        if (winner == null) switchPlayer()
        return true
    }

    private fun switchPlayer() {
        currentPlayer = if (currentPlayer == 'X') 'O' else 'X'
    }

    private fun checkWinner() {
        for (line in WIN_LINES) {
            val a = board[line[0]]
            if (a != null && line.all { board[it] == a }) {
                winner = a
                winningLine = line
                return
            }
        }

        if (board.none { it == null }) {
            winner = 'D'
            winningLine = null
        }
    }
}
