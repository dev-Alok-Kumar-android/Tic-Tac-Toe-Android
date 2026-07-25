package com.tuto.alokkumar.tictactoe.domain

import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.GameMode
import kotlin.math.pow

/**
 * AI Decision-making utility offering various difficulty level calculations.
 */
object AiMove {

    private val transpositionTable = mutableMapOf<String, Int>()
    private const val MAX_TABLE_SIZE = 10000

    /**
     * Calculates the best move for any board configuration and difficulty.
     */
    fun getBestMove(
        board: List<Char?>,
        ai: Char,
        gameMode: GameMode,
        boardSize: BoardSize,
        winLines: List<List<Int>>,
        strength: Int = 100,
        isManualDepth: Boolean = false,
        manualDepth: Int = 6
    ): Int? {
        transpositionTable.clear()
        val moves = board.indices.filter { board[it] == null }
        if (moves.isEmpty()) return null

        // 1. Skill Level Randomness: Lower strength increases chance of a non-optimal random move.
        // Formula: strength 100 -> 0% error, strength 1 -> 90% error
        val errorChance = (100 - strength) * 0.9 / 100.0
        if (Math.random() < errorChance && gameMode != GameMode.IMPOSSIBLE) {
            return moves.random()
        }

        return when (gameMode) {
            GameMode.EASY -> moves.random()
            GameMode.MEDIUM -> mediumMove(board, ai, winLines)
            GameMode.HARD, GameMode.IMPOSSIBLE -> {
                // Determine max depth based on complexity OR manual override
                val totalCells = boardSize.x * boardSize.y * boardSize.z
                val maxDepth = if (isManualDepth) {
                    manualDepth 
                } else if (gameMode == GameMode.IMPOSSIBLE && totalCells <= 9) {
                    9 // Always perfect for 3x3
                } else {
                    when {
                        totalCells <= 9 -> 9  // Full search for 3x3
                        totalCells <= 16 -> 6 // 4x4
                        totalCells <= 25 -> 4 // 5x5
                        else -> 3 // Very large or 3D 3x3x3 (27 cells)
                    }
                }
                optimizedMinimaxMove(board.toMutableList(), ai, winLines, boardSize, maxDepth)
            }
            else -> moves.random()
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

        // Use symmetry to reduce initial branching
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
                depth = 0,
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
        
        // Cache Check
        val canonical = BoardSymmetry.getCanonicalForm(board, boardSize)
        val cacheKey = "$canonical:$isMax:$depth"
        transpositionTable[cacheKey]?.let { return it }

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
            putInCache(cacheKey, best)
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
            putInCache(cacheKey, best)
            return best
        }
    }

    private fun putInCache(key: String, value: Int) {
        if (transpositionTable.size >= MAX_TABLE_SIZE) {
            transpositionTable.clear() // Simple purge when full
        }
        transpositionTable[key] = value
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

        // 1. Win
        for (i in emptyCells) {
            if (wouldWin(board, i, ai, winLines)) return i
        }
        // 2. Block
        for (i in emptyCells) {
            if (wouldWin(board, i, player, winLines)) return i
        }
        // 3. Center/Random
        return if (board.size > 4 && board[board.size / 2] == null) board.size / 2 else emptyCells.random()
    }

    private fun wouldWin(board: List<Char?>, index: Int, player: Char, winLines: List<List<Int>>): Boolean {
        val temp = board.toMutableList()
        temp[index] = player
        return winLines.any { line -> line.all { temp[it] == player } }
    }
}
