package com.tuto.alokkumar.tictactoe.domain

import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.GameMode

/**
 * Scalable Tic Tac Toe logic supporting 2D and 3D boards of any dimensions.
 *
 * Implements game state evaluation, player switching, move verification, winning line search
 * across multi-dimensional grids, and triggers automated AI move calculations.
 *
 * @property gameMode Interactive mode defining difficulty level and AI/PvP behavior.
 * @property boardSize Dimensions of the current play board grid.
 */
class GameLogic(
    var gameMode: GameMode = GameMode.PVP,
    private val boardSize: BoardSize = BoardSize()
) {
    private val totalCells = boardSize.x * boardSize.y * boardSize.z
    private val board = MutableList<Char?>(totalCells) { null }

    /** Precomputed list of all possible winning line index combinations. */
    private val winningLines: List<List<Int>> = computeWinningLines()
    
    /** Current player whose turn it is to place a symbol ('X' or 'O'). */
    var currentPlayer: Char = 'X'
        private set

    /** Stores the winner of the current match: 'X', 'O', 'D' (Draw), or null if active. */
    var winner: Char? = null
        private set

    /** Stored flat indices of cells forming the winning contiguous line. Null if no winner. */
    var winLine: List<Int>? = null
        private set

    /** Stores the flat index of the absolute latest placed move. Null if game was just reset. */
    var lastMove: Int? = null
        private set

    /**
     * Resets the game state, clearing board cells and returning starting turn to the specified player.
     * 
     * @param startPlayer The player character ('X' or 'O') who should start the next game.
     */
    fun resetGame(startPlayer: Char = 'X') {
        for (i in board.indices) board[i] = null
        currentPlayer = startPlayer
        winner = null
        winLine = null
        lastMove = null
    }

    /**
     * Retrieves an immutable read-only view of the board cell values.
     */
    fun getBoard(): List<Char?> = board.toList()

    /**
     * Configures/reloads the game logic state with a predefined list of moves.
     * Often used during historical state restore.
     *
     * @param newBoard Grid state matching internal dimensions size.
     * @param current The player token whose turn it is now.
     */
    fun setBoard(newBoard: List<Char?>, current: Char) {
        if (newBoard.size == board.size) {
            for (i in board.indices) {
                board[i] = newBoard[i]
            }
        }
        currentPlayer = current
        lastMove = null // We don't track last move from loaded history for now
        checkGameState() // Re-check if winner exists in loaded state
    }

    /**
     * Places current player's token on the selected cell index if valid.
     * Automatically evaluates resulting state and triggers player swap.
     *
     * @param index Flattened index of target grid cell.
     * @return True if token was successfully placed, false otherwise.
     */
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

        if (board.none { it == null }) {
            winner = 'D' // Draw
        }
    }

    /**
     * Iterates through precomputed winning lines to detect a winner.
     */
    private fun findWinner(): Pair<Char, List<Int>>? {
        for (line in winningLines) {
            val symbol = board[line[0]] ?: continue
            if (line.all { board[it] == symbol }) {
                return symbol to line
            }
        }
        return null
    }

    /**
     * Generates all possible winning line index sequences based on board dimensions and win condition.
     */
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

        // Potential move directions: (dx, dy, dz)
        val directions = listOf(
            Triple(1, 0, 0), Triple(0, 1, 0), Triple(0, 0, 1), // Axes
            Triple(1, 1, 0), Triple(1, -1, 0), Triple(1, 0, 1), Triple(1, 0, -1), Triple(0, 1, 1), Triple(0, 1, -1), // 2D Diagonals
            Triple(1, 1, 1), Triple(1, 1, -1), Triple(1, -1, 1), Triple(1, -1, -1) // 3D Diagonals
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
                            // Deduplicate lines by sorting indices
                            val sortedLine = line.sorted()
                            if (!lines.contains(sortedLine)) {
                                lines.add(sortedLine)
                            }
                        }
                    }
                }
            }
        }
        return lines
    }

    /**
     * Calculates the ideal AI action cell depending on the configured game mode difficulty.
     *
     * @param aiSymbol Character token used by the active AI routine.
     * @return Ideal flat index cell choice, or null if board is fully occupied.
     */
    fun getBestMove(aiSymbol: Char): Int? {
        return AiMove.getBestMove(board, aiSymbol, gameMode, boardSize, winningLines)
    }
}
