package com.tuto.alokkumar.tictactoe.domain

object AiMove {

    fun randomMove(board: List<Char?>): Int? {
        val moves = board.indices.filter { board[it] == null }
        return if (moves.isNotEmpty()) moves.random() else null
    }

    fun mediumMove(
        board: List<Char?>,
        ai: Char,
        winLines: List<List<Int>>,
    ): Int? {
        val human = if (ai == 'X') 'O' else 'X'
        val emptyCells = board.indices.filter { board[it] == null }
        val winningMoves = emptyCells.filter {
            wouldWin(board, it, ai, winLines)
        }
        if (winningMoves.isNotEmpty()) {
            return winningMoves.random()
        }
        val blockingMoves = emptyCells.filter {
            wouldWin(board, it, human, winLines)
        }
        if (blockingMoves.isNotEmpty()) {
            return blockingMoves.random()
        }
        return randomMove(board)
    }


    fun heuristicMove(
        board: List<Char?>,
        ai: Char,
        winLines: List<List<Int>>,
    ): Int? {
        val human = if (ai == 'X') 'O' else 'X'
        val emptyCells = board.indices.filter { board[it] == null }

        var bestScore = Int.MIN_VALUE
        val bestMoves = mutableListOf<Int>()

        for (i in emptyCells) {
            val score = scoreMove(board, i, ai, winLines) - scoreMove(board, i, human, winLines)
            if (score > bestScore) {
                bestScore = score
                bestMoves.clear()
                bestMoves.add(i)
            } else if (score == bestScore) {
                bestMoves.add(i)
            }
        }
        return bestMoves.randomOrNull()
    }

    private fun scoreMove(
        board: List<Char?>,
        index: Int,
        player: Char,
        winLines: List<List<Int>>,
    ): Int {
        var score = 0

        for (line in winLines) {
            if (index !in line) continue

            val values = line.map {
                if (it == index) player else board[it]
            }

            val countPlayer = values.count { it == player }
            val countEmpty = values.count { it == null }

            score += when {
                countPlayer == line.size -> 1000   // winning move
                countPlayer == line.size - 1 && countEmpty == 1 -> 100
                countPlayer == line.size - 2 && countEmpty == 2 -> 10
                else -> 1
            }
//            if (isCenter(index, board.size)) score += 15
        }
        return score
    }


    fun minimaxMove2D(
        board: MutableList<Char?>,
        aiSymbol: Char,
        winLines: List<List<Int>>,
        maxDepth: Int = Int.MAX_VALUE,
    ): Int? {
        var bestScore = Int.MIN_VALUE
        val bestMoves = mutableListOf<Int>()

        for (i in board.indices) {
            if (board[i] == null) {
                val tempBoard = board.toMutableList()
                tempBoard[i] = aiSymbol
                val score = minimax(
                    depth = 0,
                    isMaximizing = false,
                    aiSymbol = aiSymbol,
                    alphaInit = Int.MIN_VALUE,
                    betaInit = Int.MAX_VALUE,
                    board = tempBoard,
                    winLines = winLines,
                    maxDepth = maxDepth
                )

                if (score > bestScore) {
                    bestScore = score
                    bestMoves.clear()
                    bestMoves.add(i)
                } else if (score == bestScore) {
                    bestMoves.add(i)
                }
            }
        }
        return bestMoves.randomOrNull()
    }

    private fun minimax(
        depth: Int,
        isMaximizing: Boolean,
        aiSymbol: Char,
        alphaInit: Int,
        betaInit: Int,
        board: MutableList<Char?>,
        winLines: List<List<Int>>,
        maxDepth: Int,
    ): Int {
        val playerSymbol = if (aiSymbol == 'X') 'O' else 'X'
        val result = getWinnerForMinimax(winLines, board)

        if (result == aiSymbol) return 100 - depth
        if (result == playerSymbol) return depth - 100
        if (result == 'D') return 0

        if (depth >= maxDepth) {
            return run {
                val human = if (aiSymbol == 'X') 'O' else 'X'
                var score = 0
                for (i in board.indices) {
                    if (board[i] == aiSymbol) {
                        score += when (i) {
                            4 -> 5  // Center
                            0, 2, 6, 8 -> 3 // Corners
                            else -> 1
                        }
                    } else if (board[i] == human) {
                        score -= when (i) {
                            4 -> 5
                            0, 2, 6, 8 -> 3
                            else -> 1
                        }
                    }
                }
                score
            }
        }

        var alpha = alphaInit
        var beta = betaInit

        if (isMaximizing) {
            var maxEval = Int.MIN_VALUE
            for (i in board.indices) {
                if (board[i] == null) {
                    board[i] = aiSymbol
                    val eval =
                        minimax(depth + 1, false, aiSymbol, alpha, beta, board, winLines, maxDepth)
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
                    val eval =
                        minimax(depth + 1, true, aiSymbol, alpha, beta, board, winLines, maxDepth)
                    board[i] = null
                    minEval = minOf(minEval, eval)
                    beta = minOf(beta, eval)
                    if (beta <= alpha) break
                }
            }
            return minEval
        }
    }

    private fun getWinnerForMinimax(
        winLines: List<List<Int>>,
        board: MutableList<Char?>,
    ): Char? {
        for (line in winLines) {
            val first = board[line[0]] ?: continue
            if (line.all { board[it] == first }) return first
        }
        if (board.none { it == null }) return 'D'
        return null
    }

    private fun wouldWin(
        board: List<Char?>,
        index: Int,
        player: Char,
        winLines: List<List<Int>>,
    ): Boolean {
        val temp = board.toMutableList()
        temp[index] = player
        return winLines.any { line ->
            line.all { temp[it] == player }
        }
    }
}
