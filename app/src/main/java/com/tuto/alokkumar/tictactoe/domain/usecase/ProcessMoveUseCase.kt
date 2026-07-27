package com.tuto.alokkumar.tictactoe.domain.usecase

import com.tuto.alokkumar.tictactoe.domain.GameRules
import com.tuto.alokkumar.tictactoe.domain.model.GameState
import com.tuto.alokkumar.tictactoe.domain.model.MatchResult
import com.tuto.alokkumar.tictactoe.domain.model.PlayerId
import javax.inject.Inject

/**
 * Use case to apply a player move to the current game state and evaluate results.
 */
class ProcessMoveUseCase @Inject constructor() {

    operator fun invoke(
        currentState: GameState,
        index: Int,
        winLines: List<List<Int>>
    ): GameState {
        if (!GameRules.isValidMove(currentState.board, currentState.result, index)) {
            return currentState
        }

        val newBoard = currentState.board.toMutableList()
        newBoard[index] = currentState.currentPlayerId

        val winResult = GameRules.findWinner(newBoard, winLines)
        val result = when {
            winResult != null -> MatchResult.Winner(winResult.first)
            GameRules.isBoardFull(newBoard) -> MatchResult.Draw
            else -> MatchResult.Ongoing
        }

        val nextPlayerId = if (result is MatchResult.Ongoing) {
            if (currentState.currentPlayerId == PlayerId.P1) PlayerId.P2 else PlayerId.P1
        } else {
            currentState.currentPlayerId
        }

        return currentState.copy(
            board = newBoard,
            currentPlayerId = nextPlayerId,
            result = result,
            winLine = winResult?.second,
            lastMove = index,
            p1Wins = currentState.p1Wins + if (result is MatchResult.Winner && result.id == PlayerId.P1) 1 else 0,
            p2Wins = currentState.p2Wins + if (result is MatchResult.Winner && result.id == PlayerId.P2) 1 else 0,
            draws = currentState.draws + if (result is MatchResult.Draw) 1 else 0
        )
    }
}
