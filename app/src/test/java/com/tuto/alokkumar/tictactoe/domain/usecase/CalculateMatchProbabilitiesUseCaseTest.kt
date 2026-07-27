package com.tuto.alokkumar.tictactoe.domain.usecase

import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.FirstMoveBehavior
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CalculateMatchProbabilitiesUseCaseTest {

    private val useCase = CalculateMatchProbabilitiesUseCase()

    @Test
    fun `test simulation produces valid probabilities`() = runBlocking {
        val size = BoardSize(3, 3, 1, 3)
        val result = useCase(size, FirstMoveBehavior.PLAYER_X, iterations = 100)
        
        // Sum should be 1.0 (approx due to float)
        val sum = result.p1WinChance + result.p2WinChance + result.drawChance
        assertTrue(sum > 0.99f && sum < 1.01f)
        
        // In 3x3, P1 (first player) should usually have a higher random win chance than P2
        assertTrue(result.p1WinChance > result.p2WinChance)
    }

    @Test
    fun `test impossible win condition results in 100 percent draw`() = runBlocking {
        // 3x3 board but need 10 in a row to win (impossible)
        val size = BoardSize(3, 3, 1, 10)
        val result = useCase(size, FirstMoveBehavior.PLAYER_X, iterations = 50)
        
        assertTrue(result.drawChance == 1.0f)
        assertTrue(result.p1WinChance == 0.0f)
        assertTrue(result.p2WinChance == 0.0f)
    }
}
