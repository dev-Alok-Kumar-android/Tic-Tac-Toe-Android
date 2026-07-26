package com.tuto.alokkumar.tictactoe.domain

import com.tuto.alokkumar.tictactoe.data.AiDifficulty
import com.tuto.alokkumar.tictactoe.data.BoardSize

/**
 * Scalable Tic Tac Toe logic supporting 2D and 3D boards.
 */
class GameLogic(
    var aiDifficulty: AiDifficulty = AiDifficulty.HARD,
    private var boardSize: BoardSize = BoardSize()
) {
    private var totalCells = boardSize.x * boardSize.y * boardSize.z
    private var board = MutableList<Char?>(totalCells) { null }
    private var winningLines: List<List<Int>> = computeWinningLines()
    
    var currentPlayer: Char = 'X'
        private set

    var winner: Char? = null
        private set

    var winLine: List<Int>? = null
        private set

    var lastMove: Int? = null
        private set

    /**
     * Reconfigures the logic for a different board size.
     * Useful when restoring games from history with different dimensions.
     */
    fun reconfigure(newSize: BoardSize) {
        boardSize = newSize
        totalCells = boardSize.x * boardSize.y * boardSize.z
        board = MutableList(totalCells) { null }
        winningLines = computeWinningLines()
        resetGame()
    }

    fun resetGame(startPlayer: Char = 'X') {
        for (i in board.indices) board[i] = null
        currentPlayer = startPlayer
        winner = null
        winLine = null
        lastMove = null
    }

    fun getBoard(): List<Char?> = board.toList()

    fun setBoard(newBoard: List<Char?>, current: Char, size: BoardSize? = null) {
        size?.let { 
            if (it != boardSize) reconfigure(it)
        }
        
        if (newBoard.size == board.size) {
            for (i in board.indices) board[i] = newBoard[i]
        }
        currentPlayer = current
        lastMove = null
        checkGameState()
    }

    fun makeMove(index: Int): Boolean {
        if (index !in 0 until totalCells || board[index] != null || winner != null) return false
        board[index] = currentPlayer
        lastMove = index
        checkGameState()
        if (winner == null) switchPlayer()
        return true
    }

    private fun switchPlayer() {
        currentPlayer = if (currentPlayer == 'X') 'O' else 'X'
    }

    private fun checkGameState() {
        val win = findWinner()
        if (win != null) {
            winner = win.first
            winLine = win.second
            return
        }
        if (board.none { it == null }) winner = 'D'
    }

    private fun findWinner(): Pair<Char, List<Int>>? {
        for (line in winningLines) {
            val symbol = board[line[0]] ?: continue
            if (line.all { board[it] == symbol }) return symbol to line
        }
        return null
    }

    private fun computeWinningLines(): List<List<Int>> {
        val x = boardSize.x
        val y = boardSize.y
        val z = boardSize.z
        val target = boardSize.winCondition
        val lines = mutableListOf<List<Int>>()

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
                            if (nextIdx != -1) line.add(nextIdx) else { valid = false; break }
                        }
                        if (valid && line.size == target) {
                            val sortedLine = line.sorted()
                            if (!lines.contains(sortedLine)) lines.add(sortedLine)
                        }
                    }
                }
            }
        }
        return lines
    }

    fun getBestMove(
        aiSymbol: Char,
        strength: Int = 100,
        isManualDepth: Boolean = false,
        manualDepth: Int = 6
    ): Int? {
        return AiMove.getBestMove(
            board = board,
            ai = aiSymbol,
            difficulty = aiDifficulty,
            boardSize = boardSize,
            winLines = winningLines,
            strength = strength,
            isManualDepth = isManualDepth,
            manualDepth = manualDepth
        )
    }
}
