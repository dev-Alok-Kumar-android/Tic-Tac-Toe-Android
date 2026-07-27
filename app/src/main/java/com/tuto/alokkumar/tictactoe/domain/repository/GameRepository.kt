package com.tuto.alokkumar.tictactoe.domain.repository

import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.data.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining data access for game preferences and history.
 */
interface GameRepository {
    
    /** Flow of the current user preferences. */
    val userPreferences: Flow<UserPreferences>
    
    /** Flow of the list of past games. */
    val gameHistory: Flow<List<GameHistory>>
    
    /** Updates user preferences with a transformation function. */
    suspend fun updatePreferences(transform: (UserPreferences) -> UserPreferences)
    
    /** Saves a new game to the history. */
    suspend fun saveGame(history: GameHistory)
    
    /** Deletes specified games from the history by their IDs. */
    suspend fun deleteGames(matchIds: List<String>)
    
    /** Clears all game history. */
    suspend fun clearHistory()
}
