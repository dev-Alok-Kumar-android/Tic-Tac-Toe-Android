package com.tuto.alokkumar.tictactoe.data.repository

import com.tuto.alokkumar.tictactoe.core.pref.PreferencesManager
import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.data.UserPreferences
import com.tuto.alokkumar.tictactoe.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val preferencesManager: PreferencesManager
) : GameRepository {

    override val userPreferences: Flow<UserPreferences> = preferencesManager.userPreferencesFlow
    
    override val gameHistory: Flow<List<GameHistory>> = preferencesManager.getGameHistoryFlow()

    override suspend fun updatePreferences(transform: (UserPreferences) -> UserPreferences) {
        preferencesManager.updatePrefs(transform)
    }

    override suspend fun saveGame(history: GameHistory) {
        preferencesManager.addGameHistory(history)
    }

    override suspend fun deleteGames(matchIds: List<String>) {
        preferencesManager.removeGameHistories(matchIds)
    }

    override suspend fun clearHistory() {
        preferencesManager.clearAllGameHistory()
    }
}
