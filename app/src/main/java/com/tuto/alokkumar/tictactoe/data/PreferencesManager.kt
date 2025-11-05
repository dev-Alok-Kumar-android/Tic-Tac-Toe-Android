package com.tuto.alokkumar.tictactoe.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore("settings_prefs")

class PreferencesManager(private val context: Context) {
    companion object {
        private val KEY_GAME_MODE = stringPreferencesKey("game_mode")
        private val KEY_BGM = booleanPreferencesKey("bgm_enabled")
        private val KEY_SOUND = booleanPreferencesKey("sound_enabled")
        private val KEY_THEME = booleanPreferencesKey("theme_dark")
        private val IMMERSIVE_MODE = booleanPreferencesKey("immersive_mode")
        private val KEY_HISTORY = stringPreferencesKey("game_history")
    }

    val gameModeFlow = context.dataStore.data.map { prefs ->
        val modeName = prefs[KEY_GAME_MODE] ?: GameMode.HARD.name
        GameMode.valueOf(modeName)
    }

    val immersiveFlow = context.dataStore.data.map { prefs ->
        prefs[IMMERSIVE_MODE] ?: false
    }

    val bgmEnabledFlow = context.dataStore.data.map { prefs ->
        prefs[KEY_BGM] ?: true
    }

    val soundEnabledFlow = context.dataStore.data.map { prefs ->
        prefs[KEY_SOUND] ?: true
    }

    val themeDarkFlow = context.dataStore.data.map { prefs ->
        prefs[KEY_THEME] ?: true
    }

    suspend fun setGameMode(mode: GameMode) {
        context.dataStore.edit { prefs ->
            prefs[KEY_GAME_MODE] = mode.name
        }
    }

    suspend fun toggleImmersiveMode() {
        val current = context.dataStore.data.map {
            it[IMMERSIVE_MODE] ?: true }.first()
        context.dataStore.edit { prefs ->
            prefs[IMMERSIVE_MODE] = !current
        }
    }

    suspend fun toggleBgm() {
        val current = context.dataStore.data.map {
            it[KEY_BGM] ?: false }.first()
        context.dataStore.edit { prefs ->
            prefs[KEY_BGM] = !current
        }
    }

    suspend fun toggleSound() {
        val current = context.dataStore.data.map {
            it[KEY_SOUND] ?: false }.first()
        context.dataStore.edit { prefs ->
            prefs[KEY_SOUND] = !current
        }
    }

    suspend fun toggleDarkTheme() {
        val currentValue = context.dataStore.data
            .map { it[KEY_THEME] ?: false }
            .first()

        context.dataStore.edit { prefs ->
            prefs[KEY_THEME] = !currentValue
        }
    }

    // 🎮 Game History
    suspend fun addGameHistory(history: GameHistory) {
        val currentList = getGameHistoryListOnce().toMutableList()
        currentList.add(0, history) // newest first
        val json = Json.encodeToString(currentList)
        context.dataStore.edit { prefs ->
            prefs[KEY_HISTORY] = json
        }
    }

    fun getGameHistoryFlow() = context.dataStore.data.map { prefs ->
        prefs[KEY_HISTORY]?.let { Json.decodeFromString<List<GameHistory>>(it) } ?: emptyList()
    }

    suspend fun removeGameHistory(history: GameHistory) {
        val currentList = getGameHistoryListOnce().toMutableList()
        currentList.remove(history)
        val json = Json.encodeToString(currentList)
        context.dataStore.edit { prefs ->
            prefs[KEY_HISTORY] = json
        }
    }

    private suspend fun getGameHistoryListOnce(): List<GameHistory> {
        val prefs = context.dataStore.data.map { it[KEY_HISTORY] }.first()
        return prefs?.let { Json.decodeFromString(it) } ?: emptyList()
    }

    suspend fun clearAllGameHistory() {
        context.dataStore.edit { it.remove(KEY_HISTORY) }
    }
}
