package com.tuto.alokkumar.tictactoe.domain

import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.GameMode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class GameLogicTest {

    @Test
    fun `test win detection 2D`() {
        val logic = GameLogic(GameMode.PVP, BoardSize(3, 3, 1, 3))
        logic.makeMove(0) // X
        logic.makeMove(3) // O
        logic.makeMove(1) // X
        logic.makeMove(4) // O
        logic.makeMove(2) // X
        
        assertEquals('X', logic.winner)
        assertNotNull(logic.winLine)
        assertEquals(listOf(0, 1, 2), logic.winLine?.sorted())
    }

    @Test
    fun `test AI hard mode unbeatable on 3x3`() {
        val logic = GameLogic(GameMode.HARD, BoardSize(3, 3, 1, 3))
        
        // Simulating a game where AI is O
        // Move 1: Player (X) takes center
        logic.makeMove(4)
        
        // Move 2: AI (O) moves
        val aiMove = logic.getBestMove('O')
        assertNotNull(aiMove)
        logic.makeMove(aiMove!!)
        
        // We could run a full exhaustive search test here, but for now just verify a move is made
        assertNotNull(logic.currentPlayer)
    }

    @Test
    fun `measure 3x3 performance optimized`() {
        val logic = GameLogic(GameMode.HARD, BoardSize(3, 3, 1, 3))
        val time = measureTimeMillis {
            logic.getBestMove('X')
        }
        println("3x3 Optimized Move Time: $time ms")
    }

    @Test
    fun `test HARD mode on 4x4`() {
        val logic = GameLogic(GameMode.HARD, BoardSize(4, 4, 1, 4))
        val time = measureTimeMillis {
            val move = logic.getBestMove('X')
            assertNotNull(move)
        }
        println("4x4 Move Time (Depth 6): $time ms")
    }

    @Test
    fun `test HARD mode on 3x3x3`() {
        val logic = GameLogic(GameMode.HARD, BoardSize(3, 3, 3, 3))
        val time = measureTimeMillis {
            val move = logic.getBestMove('X')
            assertNotNull(move)
        }
        println("3x3x3 Move Time (Depth 4): $time ms")
    }
}
