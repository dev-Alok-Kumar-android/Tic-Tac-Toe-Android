package com.tuto.alokkumar.tictactoe.domain.usecase

import com.tuto.alokkumar.tictactoe.data.UserPreferences
import com.tuto.alokkumar.tictactoe.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case to read and update user preferences.
 */
class ManagePreferencesUseCase @Inject constructor(
    private val repository: GameRepository
) {
    val userPreferences: Flow<UserPreferences> = repository.userPreferences

    suspend fun updatePreferences(transform: (UserPreferences) -> UserPreferences) {
        repository.updatePreferences(transform)
    }
}
