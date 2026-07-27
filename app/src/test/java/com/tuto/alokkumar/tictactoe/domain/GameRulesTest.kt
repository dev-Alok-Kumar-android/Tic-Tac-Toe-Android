package com.tuto.alokkumar.tictactoe.domain

import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.domain.model.PlayerId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GameRulesTest {

    @Test
    fun `test win detection 2D`() {
        val boardSize = BoardSize(3, 3, 1, 3)
        val winLines = GameRules.computeWinningLines(boardSize)
        
        val board = listOf(
            PlayerId.P1, PlayerId.P1, PlayerId.P1,
            PlayerId.P2, PlayerId.P2, null,
            null, null, null
        )
        
        val result = GameRules.findWinner(board, winLines)
        
        assertEquals(PlayerId.P1, result?.first)
        assertEquals(listOf(0, 1, 2), result?.second?.sorted())
    }

    @Test
    fun `test draw detection`() {
        val board = listOf(
            PlayerId.P1, PlayerId.P2, PlayerId.P1,
            PlayerId.P1, PlayerId.P2, PlayerId.P2,
            PlayerId.P2, PlayerId.P1, PlayerId.P1
        )
        val boardSize = BoardSize(3, 3, 1, 3)
        val winLines = GameRules.computeWinningLines(boardSize)
        
        val winner = GameRules.findWinner(board, winLines)
        val isFull = GameRules.isBoardFull(board)
        
        assertEquals(null, winner)
        assertEquals(true, isFull)
    }
}
