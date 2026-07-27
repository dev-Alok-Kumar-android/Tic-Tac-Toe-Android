package com.tuto.alokkumar.tictactoe.domain.usecase

import com.tuto.alokkumar.tictactoe.data.AiDifficulty
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.domain.AiMove
import com.tuto.alokkumar.tictactoe.domain.model.PlayerId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case to calculate the optimal move for an AI opponent.
 */
class GetAiMoveUseCase @Inject constructor() {

    suspend operator fun invoke(
        board: List<PlayerId?>,
        aiPlayerId: PlayerId,
        difficulty: AiDifficulty,
        boardSize: BoardSize,
        winLines: List<List<Int>>,
        strength: Int = 100,
        manualDepth: Int = 6
    ): Int? = withContext(Dispatchers.Default) {
        // Map PlayerId back to Char for the legacy Minimax engine
        val charBoard = board.map { 
            when (it) {
                PlayerId.P1 -> 'X'
                PlayerId.P2 -> 'O'
                null -> null
            }
        }
        val aiChar = if (aiPlayerId == PlayerId.P1) 'X' else 'O'

        AiMove.getBestMove(
            board = charBoard,
            ai = aiChar,
            difficulty = difficulty,
            boardSize = boardSize,
            winLines = winLines,
            strength = strength,
            manualDepth = manualDepth
        )
    }
}
