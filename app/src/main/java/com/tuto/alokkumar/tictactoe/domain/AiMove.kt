package com.tuto.alokkumar.tictactoe.domain

import com.tuto.alokkumar.tictactoe.data.AiDifficulty
import com.tuto.alokkumar.tictactoe.data.BoardSize
import kotlin.math.pow

/**
 * AI Decision-making utility offering various difficulty level calculations.
 */
object AiMove {

    /**
     * Calculates the best move for any board configuration and difficulty.
     */
    fun getBestMove(
        board: List<Char?>,
        ai: Char,
        difficulty: AiDifficulty,
        boardSize: BoardSize,
        winLines: List<List<Int>>,
        strength: Int = 100,
        manualDepth: Int = 6
    ): Int? {
        val moves = board.indices.filter { board[it] == null }
        if (moves.isEmpty()) return null

        // 1. Skill Level Randomness
        val errorChance = (100 - strength) * 0.9 / 100.0
        if (Math.random() < errorChance && difficulty != AiDifficulty.IMPOSSIBLE) {
            return moves.random()
        }

        return when (difficulty) {
            AiDifficulty.EASY -> moves.random()
            AiDifficulty.MEDIUM -> mediumMove(board, ai, winLines)
            AiDifficulty.HARD, AiDifficulty.IMPOSSIBLE -> {
                val totalCells = boardSize.x * boardSize.y * boardSize.z
                
                // Safety Cap: Even if user picks depth 12, large boards will crash.
                // We cap it based on total cells to keep the UI responsive.
                val safetyCap = when {
                    totalCells <= 16 -> 12
                    totalCells <= 25 -> 6
                    totalCells <= 64 -> 4
                    totalCells <= 100 -> 2
                    else -> 1
                }
                
                val finalDepth = manualDepth.coerceAtMost(safetyCap)
                
                optimizedMinimaxMove(board.toMutableList(), ai, winLines, boardSize, finalDepth)
            }
        }
    }

    private fun optimizedMinimaxMove(
        board: MutableList<Char?>,
        ai: Char,
        winLines: List<List<Int>>,
        boardSize: BoardSize,
        maxDepth: Int
    ): Int? {
        val emptyCells = board.indices.filter { board[it] == null }
        if (emptyCells.isEmpty()) return null

        var bestScore = Int.MIN_VALUE
        val bestMoves = mutableListOf<Int>()

        val evaluatedSymmetries = mutableSetOf<String>()

        for (i in emptyCells) {
            board[i] = ai
            val canonical = BoardSymmetry.getCanonicalForm(board, boardSize)
            if (evaluatedSymmetries.contains(canonical)) {
                board[i] = null
                continue
            }
            evaluatedSymmetries.add(canonical)

            val score = minimax(
                depth = 1,
                isMax = false,
                ai = ai,
                alpha = Int.MIN_VALUE,
                beta = Int.MAX_VALUE,
                board = board,
                winLines = winLines,
                boardSize = boardSize,
                maxDepth = maxDepth
            )
            board[i] = null

            if (score > bestScore) {
                bestScore = score
                bestMoves.clear()
                bestMoves.add(i)
            } else if (score == bestScore) {
                bestMoves.add(i)
            }
        }
        return bestMoves.randomOrNull() ?: emptyCells.random()
    }

    private fun minimax(
        depth: Int,
        isMax: Boolean,
        ai: Char,
        alpha: Int,
        beta: Int,
        board: MutableList<Char?>,
        winLines: List<List<Int>>,
        boardSize: BoardSize,
        maxDepth: Int
    ): Int {
        val player = if (ai == 'X') 'O' else 'X'
        
        val winner = getWinnerForMinimax(winLines, board)
        if (winner == ai) return 100 - depth
        if (winner == player) return depth - 100
        if (winner == 'D') return 0

        if (depth >= maxDepth) {
            return evaluateHeuristic(board, ai, winLines)
        }

        var a = alpha
        var b = beta

        if (isMax) {
            var best = Int.MIN_VALUE
            for (i in board.indices) {
                if (board[i] == null) {
                    board[i] = ai
                    val score = minimax(depth + 1, false, ai, a, b, board, winLines, boardSize, maxDepth)
                    board[i] = null
                    best = maxOf(best, score)
                    a = maxOf(a, best)
                    if (b <= a) break
                }
            }
            return best
        } else {
            var best = Int.MAX_VALUE
            for (i in board.indices) {
                if (board[i] == null) {
                    board[i] = player
                    val score = minimax(depth + 1, true, ai, a, b, board, winLines, boardSize, maxDepth)
                    board[i] = null
                    best = minOf(best, score)
                    b = minOf(b, best)
                    if (b <= a) break
                }
            }
            return best
        }
    }

    private fun evaluateHeuristic(board: List<Char?>, ai: Char, winLines: List<List<Int>>): Int {
        val player = if (ai == 'X') 'O' else 'X'
        var score = 0
        for (line in winLines) {
            val values = line.map { board[it] }
            val aiCount = values.count { it == ai }
            val playerCount = values.count { it == player }

            if (aiCount > 0 && playerCount == 0) {
                score += 10.0.pow((aiCount - 1).toDouble()).toInt()
            } else if (playerCount > 0 && aiCount == 0) {
                score -= 10.0.pow((playerCount - 1).toDouble()).toInt()
            }
        }
        return score
    }

    private fun getWinnerForMinimax(winLines: List<List<Int>>, board: List<Char?>): Char? {
        for (line in winLines) {
            val first = board[line[0]] ?: continue
            if (line.all { board[it] == first }) return first
        }
        if (board.none { it == null }) return 'D'
        return null
    }

    private fun mediumMove(board: List<Char?>, ai: Char, winLines: List<List<Int>>): Int {
        val player = if (ai == 'X') 'O' else 'X'
        val emptyCells = board.indices.filter { board[it] == null }

        for (i in emptyCells) {
            if (wouldWin(board, i, ai, winLines)) return i
        }
        for (i in emptyCells) {
            if (wouldWin(board, i, player, winLines)) return i
        }
        return if (board.size > 4 && board[board.size / 2] == null) board.size / 2 else emptyCells.random()
    }

    private fun wouldWin(board: List<Char?>, index: Int, player: Char, winLines: List<List<Int>>): Boolean {
        val temp = board.toMutableList()
        temp[index] = player
        return winLines.any { line -> line.all { temp[it] == player } }
    }
}
