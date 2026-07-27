package com.tuto.alokkumar.tictactoe.domain.usecase

import com.tuto.alokkumar.tictactoe.data.BoardSize
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class ComputeWinningLinesUseCaseTest {

    private val useCase = ComputeWinningLinesUseCase()

    @Test
    fun `calculates correct number of lines for 3x3`() = runBlocking {
        val size = BoardSize(3, 3, 1, 3)
        val lines = useCase(size)
        
        // 3 horizontal, 3 vertical, 2 diagonal = 8
        assertEquals(8, lines.size)
    }

    @Test
    fun `calculates correct number of lines for 3x3x3`() = runBlocking {
        val size = BoardSize(3, 3, 3, 3)
        val lines = useCase(size)
        
        // 3D Tic Tac Toe (3x3x3) has 49 winning lines
        assertEquals(49, lines.size)
    }

    @Test
    fun `caching returns same instance for same board size`() = runBlocking {
        val size = BoardSize(3, 3, 1, 3)
        
        val lines1 = useCase(size)
        val lines2 = useCase(size)
        
        assertSame(lines1, lines2)
    }
}
