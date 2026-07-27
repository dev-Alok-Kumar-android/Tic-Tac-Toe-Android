package com.tuto.alokkumar.tictactoe.domain.usecase

import com.tuto.alokkumar.tictactoe.data.AiDifficulty
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.domain.model.PlayerId
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class GetAiMoveUseCaseTest {

    private val useCase = GetAiMoveUseCase()
    private val boardSize = BoardSize(3, 3, 1, 3)
    private val winLines = emptyList<List<Int>>() // Minimax will use its own win detection if needed, or we provide it

    @Test
    fun `returns a valid move for empty board`() = runBlocking {
        val board = List(9) { null as PlayerId? }
        val move = useCase(
            board = board,
            aiPlayerId = PlayerId.P2,
            difficulty = AiDifficulty.HARD,
            boardSize = boardSize,
            winLines = winLines
        )
        assertNotNull(move)
    }

    @Test
    fun `returns null if board is full`() = runBlocking {
        val board = List(9) { PlayerId.P1 }
        val move = useCase(
            board = board,
            aiPlayerId = PlayerId.P2,
            difficulty = AiDifficulty.HARD,
            boardSize = boardSize,
            winLines = winLines
        )
        assertNull(move)
    }
}
