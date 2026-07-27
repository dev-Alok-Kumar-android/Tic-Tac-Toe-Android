package com.tuto.alokkumar.tictactoe.domain.usecase

import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.data.GameStats
import com.tuto.alokkumar.tictactoe.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to manage the collection of past match records and calculate stats.
 */
class ManageHistoryUseCase @Inject constructor(
    private val repository: GameRepository
) {
    val gameHistory: Flow<List<GameHistory>> = repository.gameHistory

    suspend fun saveGame(history: GameHistory) {
        repository.saveGame(history)
    }

    suspend fun deleteGames(matchIds: List<String>) {
        repository.deleteGames(matchIds)
    }

    suspend fun clearHistory() {
        repository.clearHistory()
    }

    /** Calculates summary statistics for a given history list. */
    fun getStats(histories: List<GameHistory>): GameStats {
        if (histories.isEmpty()) return GameStats()
        
        var p1Wins = 0
        var p2Wins = 0
        var draws = 0
        
        histories.forEach { h ->
            p1Wins += h.state.p1Wins
            p2Wins += h.state.p2Wins
            draws += h.state.draws
        }
        
        val total = p1Wins + p2Wins + draws
        val winRate = if (total > 0) ((p1Wins.toFloat() / total) * 100).toInt() else 0
        
        return GameStats(total, p1Wins, p2Wins, draws, winRate)
    }
}
