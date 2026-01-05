package com.tuto.alokkumar.tictactoe.core.pref

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tuto.alokkumar.tictactoe.data.AppTheme
import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.data.GameMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore("settings_prefs")

class PreferencesManager(private val appContext: Context) {

    companion object {
        private val KEY_GAME_MODE = stringPreferencesKey("game_mode")
        private val KEY_HISTORY = stringPreferencesKey("game_history")
        private val KEY_THEME = stringPreferencesKey("theme")
        private val KEY_BGM = booleanPreferencesKey("bgm_enabled")
        private val KEY_SOUND = booleanPreferencesKey("sound_enabled")
        private val IMMERSIVE_MODE = booleanPreferencesKey("immersive_mode")
        private val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    }

    // --- FLOWS ---
    val gameModeFlow = appContext.dataStore.data.map { prefs ->
        GameMode.valueOf(prefs[KEY_GAME_MODE] ?: GameMode.HARD.name)
    }

    val themeFlow = appContext.dataStore.data.map { prefs ->
        AppTheme.valueOf(prefs[KEY_THEME] ?: AppTheme.SYSTEM.name)
    }

    val immersiveFlow = appContext.dataStore.data.map { prefs ->
        prefs[IMMERSIVE_MODE] ?: false
    }

    val dynamicColorFlow = appContext.dataStore.data.map { prefs ->
        prefs[DYNAMIC_COLOR] ?: false
    }

    val bgmEnabledFlow = appContext.dataStore.data.map { prefs ->
        prefs[KEY_BGM] ?: false
    }

    val soundEnabledFlow = appContext.dataStore.data.map { prefs ->
        prefs[KEY_SOUND] ?: true
    }

    // --- SETTERS ---
    suspend fun setGameMode(mode: GameMode) {
        appContext.dataStore.edit { it[KEY_GAME_MODE] = mode.name }
    }

    suspend fun setTheme(theme: AppTheme) {
        appContext.dataStore.edit { it[KEY_THEME] = theme.name }
    }

    suspend fun toggleImmersiveMode() {
        val current = immersiveFlow.first()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            appContext.dataStore.edit { it[IMMERSIVE_MODE] = !current }
        } else {
            appContext.dataStore.edit { it[IMMERSIVE_MODE] = false }
            withContext(Dispatchers.Main) {
                Toast.makeText(appContext, "Fullscreen not supported", Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun toggleDynamicColor() {
        val current = dynamicColorFlow.first()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            appContext.dataStore.edit { it[DYNAMIC_COLOR] = !current }
        } else {
            appContext.dataStore.edit { it[DYNAMIC_COLOR] = false }
            withContext(Dispatchers.Main) {
                Toast.makeText(appContext, "DynamicColor not supported", Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun toggleBgm() {
        val current = bgmEnabledFlow.first()
        appContext.dataStore.edit { it[KEY_BGM] = !current }
    }

    suspend fun toggleSound() {
        val current = soundEnabledFlow.first()
        appContext.dataStore.edit { it[KEY_SOUND] = !current }
    }


    // --- GAME HISTORY ---
    suspend fun addGameHistory(history: GameHistory) {
        val list = getGameHistoryListOnce().toMutableList()
        list.add(0, history)
        val json = Json.encodeToString(list)

        appContext.dataStore.edit { it[KEY_HISTORY] = json }
    }

    fun getGameHistoryFlow() = appContext.dataStore.data.map { prefs ->
        prefs[KEY_HISTORY]?.let { Json.decodeFromString<List<GameHistory>>(it) } ?: emptyList()
    }

    suspend fun removeGameHistory(history: GameHistory) {
        val list = getGameHistoryListOnce().toMutableList()
        list.remove(history)
        val json = Json.encodeToString(list)

        appContext.dataStore.edit { it[KEY_HISTORY] = json }
    }

    private suspend fun getGameHistoryListOnce(): List<GameHistory> {
        val stored = appContext.dataStore.data.map { it[KEY_HISTORY] }.first()
        return stored?.let { Json.decodeFromString(it) } ?: emptyList()
    }

    suspend fun clearAllGameHistory() {
        appContext.dataStore.edit { it.remove(KEY_HISTORY) }
    }
}
