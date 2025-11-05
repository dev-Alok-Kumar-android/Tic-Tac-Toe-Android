package com.tuto.alokkumar.tictactoe.domain

import com.tuto.alokkumar.tictactoe.data.GameMode

/**
 * Core Tic Tac Toe logic and AI behavior (Easy, Medium, Hard).
 * Pure Kotlin file — no Android or Compose imports.
 */
class GameLogic(
    var gameMode: GameMode = GameMode.PVP
) {
    private val board = MutableList<Char?>(9) { null }
    var currentPlayer: Char = 'X'
        private set
    var winner: Char? = null
        private set

    fun resetGame() {
        for (i in board.indices) board[i] = null
        currentPlayer = 'X'
        winner = null
    }

    fun getBoard(): List<Char?> = board.toList()

    fun setBoard(newBoard: List<Char?>, current: Char) {
        for (i in board.indices) {
            board[i] = newBoard[i]
        }
        currentPlayer = current
        winner = null
    }

    fun makeMove(index: Int): Boolean {
        if (index !in 0..8 || board[index] != null || winner != null) return false
        board[index] = currentPlayer
        checkGameState()
        if (winner == null) switchPlayer()
        return true
    }

    private fun switchPlayer() {
        currentPlayer = if (currentPlayer == 'X') 'O' else 'X'
    }

    private fun checkGameState() {
        val winningCombos = listOf(
            listOf(0, 1, 2),
            listOf(3, 4, 5),
            listOf(6, 7, 8),
            listOf(0, 3, 6),
            listOf(1, 4, 7),
            listOf(2, 5, 8),
            listOf(0, 4, 8),
            listOf(2, 4, 6)
        )

        for (combo in winningCombos) {
            val (a, b, c) = combo
            val symbol = board[a]
            if (symbol != null && symbol == board[b] && symbol == board[c]) {
                winner = symbol
                return
            }
        }

        if (board.none { it == null }) {
            winner = 'D' // Draw
        }
    }

    /**
     * Returns the AI's best move based on difficulty level.
     */
    fun getBestMove(aiSymbol: Char): Int? {
        val availableMoves = board
            .mapIndexedNotNull { i, cell -> if (cell == null) i else null }

        if (availableMoves.isEmpty()) return null

        return when (gameMode) {
            GameMode.EASY -> getRandomMove(availableMoves)
            GameMode.MEDIUM -> getMediumMove(aiSymbol, availableMoves)
            GameMode.HARD -> getMinimaxMove(aiSymbol)
            else -> null
        }
    }

    // ---------------- EASY MODE ----------------
    private fun getRandomMove(moves: List<Int>): Int {
        return moves.random()
    }

    // ---------------- MEDIUM MODE ----------------
    private fun getMediumMove(aiSymbol: Char, moves: List<Int>): Int {
        val playerSymbol = if (aiSymbol == 'X') 'O' else 'X'

        // 1️⃣ Try to win
        for (i in moves) {
            board[i] = aiSymbol
            if (isWinning(aiSymbol)) {
                board[i] = null
                return i
            }
            board[i] = null
        }

        // 2️⃣ Try to block opponent
        for (i in moves) {
            board[i] = playerSymbol
            if (isWinning(playerSymbol)) {
                board[i] = null
                return i
            }
            board[i] = null
        }

        // 3️⃣ Otherwise random
        return getRandomMove(moves)
    }

    // ---------------- HARD MODE (MINIMAX + ALPHA-BETA) ----------------
    private fun getMinimaxMove(aiSymbol: Char): Int {
        var bestScore = Int.MIN_VALUE
        var bestMove = -1

        for (i in board.indices) {
            if (board[i] == null) {
                board[i] = aiSymbol
                val score = minimax(0, false, aiSymbol, Int.MIN_VALUE, Int.MAX_VALUE)
                board[i] = null
                if (score > bestScore) {
                    bestScore = score
                    bestMove = i
                }
            }
        }
        return bestMove
    }

    private fun minimax(
        depth: Int,
        isMaximizing: Boolean,
        aiSymbol: Char,
        alphaInit: Int,
        betaInit: Int
    ): Int {
        val playerSymbol = if (aiSymbol == 'X') 'O' else 'X'
        val result = getWinnerForAI()
        var alpha = alphaInit
        var beta = betaInit

        when (result) {
            aiSymbol -> return 10 - depth
            playerSymbol -> return depth - 10
            'D' -> return 0
        }

        if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            for (i in board.indices) {
                if (board[i] == null) {
                    board[i] = aiSymbol
                    val eval = minimax(depth + 1, false, aiSymbol, alpha, beta)
                    board[i] = null
                    maxEval = maxOf(maxEval, eval)
                    alpha = maxOf(alpha, eval)
                    if (beta <= alpha) break
                }
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            for (i in board.indices) {
                if (board[i] == null) {
                    board[i] = playerSymbol
                    val eval = minimax(depth + 1, true, aiSymbol, alpha, beta)
                    board[i] = null
                    minEval = minOf(minEval, eval)
                    beta = minOf(beta, eval)
                    if (beta <= alpha) break
                }
            }
            return minEval
        }
    }

    // Helper for Medium and Minimax checks
    private fun isWinning(symbol: Char): Boolean {
        val combos = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )
        return combos.any { (a, b, c) ->
            board[a] == symbol && board[b] == symbol && board[c] == symbol
        }
    }

    private fun getWinnerForAI(): Char? {
        val combos = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )
        for (combo in combos) {
            val (a, b, c) = combo
            val s = board[a]
            if (s != null && s == board[b] && s == board[c]) return s
        }
        if (board.none { it == null }) return 'D'
        return null
    }
}
