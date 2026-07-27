package com.tuto.alokkumar.tictactoe.domain.usecase

import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.FirstMoveBehavior
import com.tuto.alokkumar.tictactoe.domain.GameRules
import com.tuto.alokkumar.tictactoe.domain.model.MatchProbabilities
import com.tuto.alokkumar.tictactoe.domain.model.PlayerId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

/**
 * Executes a Monte Carlo simulation (Random Play) to determine the statistical 
 * fairness of a given board configuration.
 */
class CalculateMatchProbabilitiesUseCase @Inject constructor() {

    suspend operator fun invoke(
        boardSize: BoardSize,
        firstMoveBehavior: FirstMoveBehavior,
        iterations: Int = 1000
    ): MatchProbabilities = withContext(Dispatchers.Default) {
        val winLines = GameRules.computeWinningLines(boardSize)
        val totalCells = boardSize.x * boardSize.y * boardSize.z
        
        // Optimization: Map each cell index to the winLines that contain it
        val linesByCell = Array(totalCells) { mutableListOf<List<Int>>() }
        for (line in winLines) {
            for (cell in line) {
                linesByCell[cell].add(line)
            }
        }

        var p1Wins = 0
        var p2Wins = 0
        var draws = 0

        // Adjust iterations for very large boards to prevent UI lag
        val finalIterations = if (totalCells > 125) 200 else iterations

        repeat(finalIterations) {
            val startingPlayer = when (firstMoveBehavior) {
                FirstMoveBehavior.PLAYER_X -> PlayerId.P1
                FirstMoveBehavior.PLAYER_O -> PlayerId.P2
                FirstMoveBehavior.RANDOM -> if (Random.nextBoolean()) PlayerId.P1 else PlayerId.P2
            }
            
            val result = simulateMatch(totalCells, startingPlayer, linesByCell)
            when (result) {
                PlayerId.P1 -> p1Wins++
                PlayerId.P2 -> p2Wins++
                null -> draws++
            }
        }

        MatchProbabilities(
            p1WinChance = p1Wins.toFloat() / finalIterations,
            p2WinChance = p2Wins.toFloat() / finalIterations,
            drawChance = draws.toFloat() / finalIterations,
            sampleSize = finalIterations
        )
    }

    private fun simulateMatch(
        totalCells: Int,
        startingPlayer: PlayerId,
        linesByCell: Array<MutableList<List<Int>>>
    ): PlayerId? {
        val board = arrayOfNulls<PlayerId>(totalCells)
        val availableIndices = (0 until totalCells).toMutableList()
        availableIndices.shuffle()

        var currentPlayer = startingPlayer

        for (moveIndex in availableIndices) {
            board[moveIndex] = currentPlayer
            
            // Optimization: Only check lines that contain the last move
            if (checkWinnerEfficient(board, linesByCell[moveIndex], currentPlayer)) {
                return currentPlayer
            }
            
            currentPlayer = if (currentPlayer == PlayerId.P1) PlayerId.P2 else PlayerId.P1
        }

        return null // Draw
    }

    private fun checkWinnerEfficient(
        board: Array<PlayerId?>,
        linesToCheck: List<List<Int>>,
        player: PlayerId
    ): Boolean {
        for (line in linesToCheck) {
            var win = true
            for (cell in line) {
                if (board[cell] != player) {
                    win = false
                    break
                }
            }
            if (win) return true
        }
        return false
    }
}
