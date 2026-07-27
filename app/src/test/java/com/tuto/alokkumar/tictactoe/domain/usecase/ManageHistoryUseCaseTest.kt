package com.tuto.alokkumar.tictactoe.domain.usecase

import com.tuto.alokkumar.tictactoe.data.AiDifficulty
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.data.GameStateEntity
import com.tuto.alokkumar.tictactoe.domain.repository.GameRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ManageHistoryUseCaseTest {

    private val repository: GameRepository = mockk(relaxed = true)
    private val useCase = ManageHistoryUseCase(repository)

    @Test
    fun `saveGame calls repository`() = runBlocking {
        val history = mockk<GameHistory>()
        useCase.saveGame(history)
        coVerify { repository.saveGame(history) }
    }

    @Test
    fun `getStats calculates correctly`() {
        val histories = listOf(
            createFakeHistory(p1Wins = 1, p2Wins = 0, draws = 0, winnerId = "P1"),
            createFakeHistory(p1Wins = 0, p2Wins = 1, draws = 0, winnerId = "P2"),
            createFakeHistory(p1Wins = 0, p2Wins = 0, draws = 1, isDraw = true)
        )
        
        val stats = useCase.getStats(histories)
        
        assertEquals(3, stats.totalGames)
        assertEquals(1, stats.p1Wins)
        assertEquals(1, stats.p2Wins)
        assertEquals(1, stats.draws)
        assertEquals(33, stats.winRate) // 1/3 * 100
    }

    private fun createFakeHistory(
        p1Wins: Int, 
        p2Wins: Int, 
        draws: Int, 
        winnerId: String? = null,
        isDraw: Boolean = false
    ): GameHistory {
        return GameHistory(
            matchId = "test",
            difficulty = AiDifficulty.HARD,
            state = GameStateEntity(
                board = emptyList(),
                currentPlayerId = "P1",
                winnerId = winnerId,
                isDraw = isDraw,
                winLine = null,
                lastMove = null,
                p1Wins = p1Wins,
                p2Wins = p2Wins,
                draws = draws,
                boardSize = BoardSize()
            )
        )
    }
}
