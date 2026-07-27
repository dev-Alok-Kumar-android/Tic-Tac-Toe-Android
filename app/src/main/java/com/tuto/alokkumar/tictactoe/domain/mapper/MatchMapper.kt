package com.tuto.alokkumar.tictactoe.domain.mapper

import com.tuto.alokkumar.tictactoe.data.GameStateEntity
import com.tuto.alokkumar.tictactoe.domain.model.GameState
import com.tuto.alokkumar.tictactoe.domain.model.MatchResult
import com.tuto.alokkumar.tictactoe.domain.model.PlayerId
import com.tuto.alokkumar.tictactoe.ui.model.GameStateUi

/**
 * Mappers to convert between Domain, UI, and Data models.
 */
object MatchMapper {

    // --- DOMAIN -> UI ---
    fun mapToUi(
        domainState: GameState,
        p1Symbol: String,
        p2Symbol: String
    ): GameStateUi {
        return GameStateUi(
            matchId = domainState.matchId,
            board = domainState.board.map { id ->
                when (id) {
                    PlayerId.P1 -> p1Symbol
                    PlayerId.P2 -> p2Symbol
                    null -> null
                }
            },
            currentPlayerSymbol = if (domainState.currentPlayerId == PlayerId.P1) p1Symbol else p2Symbol,
            winnerSymbol = when (val result = domainState.result) {
                is MatchResult.Winner -> if (result.id == PlayerId.P1) p1Symbol else p2Symbol
                is MatchResult.Draw -> "D"
                is MatchResult.Ongoing -> null
            },
            winLine = domainState.winLine,
            lastMove = domainState.lastMove,
            p1Wins = domainState.p1Wins,
            p2Wins = domainState.p2Wins,
            draws = domainState.draws,
            boardSize = domainState.boardSize
        )
    }

    // --- DOMAIN -> DATA (Persistence) ---
    fun mapToEntity(domainState: GameState): GameStateEntity {
        val (winnerId, isDraw) = when (val res = domainState.result) {
            is MatchResult.Winner -> res.id.name to false
            is MatchResult.Draw -> null to true
            is MatchResult.Ongoing -> null to false
        }
        return GameStateEntity(
            board = domainState.board.map { it?.name },
            currentPlayerId = domainState.currentPlayerId.name,
            winnerId = winnerId,
            isDraw = isDraw,
            winLine = domainState.winLine,
            lastMove = domainState.lastMove,
            p1Wins = domainState.p1Wins,
            p2Wins = domainState.p2Wins,
            draws = domainState.draws,
            boardSize = domainState.boardSize
        )
    }

    // --- DATA -> DOMAIN (Restoration) ---
    fun mapToDomain(entity: GameStateEntity, matchId: String): GameState {
        val result = when {
            entity.winnerId != null -> MatchResult.Winner(PlayerId.valueOf(entity.winnerId.uppercase()))
            entity.isDraw -> MatchResult.Draw
            else -> MatchResult.Ongoing
        }
        return GameState(
            matchId = matchId,
            boardSize = entity.boardSize,
            board = entity.board.map { id ->
                id?.let { PlayerId.valueOf(it.uppercase()) }
            },
            currentPlayerId = PlayerId.valueOf(entity.currentPlayerId.uppercase()),
            result = result,
            winLine = entity.winLine,
            lastMove = entity.lastMove,
            p1Wins = entity.p1Wins,
            p2Wins = entity.p2Wins,
            draws = entity.draws
        )
    }
}
