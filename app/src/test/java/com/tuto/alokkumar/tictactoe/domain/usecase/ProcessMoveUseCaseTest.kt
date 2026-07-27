package com.tuto.alokkumar.tictactoe.domain.usecase

import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.domain.GameRules
import com.tuto.alokkumar.tictactoe.domain.model.GameState
import com.tuto.alokkumar.tictactoe.domain.model.MatchResult
import com.tuto.alokkumar.tictactoe.domain.model.PlayerId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ProcessMoveUseCaseTest {

    private val useCase = ProcessMoveUseCase()
    private val boardSize = BoardSize(3, 3, 1, 3)
    private val winLines = GameRules.computeWinningLines(boardSize)

    @Test
    fun `valid move updates board and switches player`() {
        val initialState = GameState(boardSize = boardSize, currentPlayerId = PlayerId.P1)
        
        val newState = useCase(initialState, 0, winLines)
        
        assertEquals(PlayerId.P1, newState.board[0])
        assertEquals(PlayerId.P2, newState.currentPlayerId)
        assertTrue(newState.result is MatchResult.Ongoing)
    }

    @Test
    fun `winning move detects winner and updates score`() {
        val state = GameState(
            boardSize = boardSize,
            board = listOf(
                PlayerId.P1, PlayerId.P1, null,
                PlayerId.P2, PlayerId.P2, null,
                null, null, null
            ),
            currentPlayerId = PlayerId.P1,
            p1Wins = 0
        )
        
        val newState = useCase(state, 2, winLines)
        
        assertTrue(newState.result is MatchResult.Winner)
        assertEquals(PlayerId.P1, (newState.result as MatchResult.Winner).id)
        assertEquals(1, newState.p1Wins)
        assertNotNull(newState.winLine)
    }

    @Test
    fun `move on occupied cell returns same state`() {
        val state = GameState(
            boardSize = boardSize,
            board = listOf(PlayerId.P1, null, null, null, null, null, null, null, null)
        )
        
        val newState = useCase(state, 0, winLines)
        
        assertEquals(state, newState)
    }

    @Test
    fun `full board without winner results in draw`() {
        val state = GameState(
            boardSize = boardSize,
            board = listOf(
                PlayerId.P1, PlayerId.P2, PlayerId.P1,
                PlayerId.P1, PlayerId.P2, PlayerId.P2,
                PlayerId.P2, PlayerId.P1, null
            ),
            currentPlayerId = PlayerId.P2
        )
        
        val newState = useCase(state, 8, winLines)
        
        assertTrue(newState.result is MatchResult.Draw)
        assertEquals(1, newState.draws)
    }

    @Test
    fun `3D move processing works without crash`() {
        val size3D = BoardSize(3, 3, 3, 3)
        val lines3D = GameRules.computeWinningLines(size3D)
        val state = GameState(boardSize = size3D)
        
        // Move at index 16 (Middle layer, top-middle cell)
        val newState = useCase(state, 16, lines3D)
        
        assertEquals(PlayerId.P1, newState.board[16])
        assertEquals(27, newState.board.size)
    }
}
