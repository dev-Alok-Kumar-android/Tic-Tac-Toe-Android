package com.tuto.alokkumar.tictactoe.core.pref

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tuto.alokkumar.tictactoe.data.AppTheme
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.BoardStyle
import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.Orientation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore("settings_prefs")

/**
 * Manages application preferences using AndroidX DataStore Preferences.
 *
 * Provides reactive [kotlinx.coroutines.flow.Flow] access to game settings, app themes,
 * audio settings, and serialization/deserialization for match history list.
 *
 * @param appContext Application context used for accessing Jetpack DataStore.
 */
class PreferencesManager(private val appContext: Context) {

    companion object {
        private val KEY_GAME_MODE = stringPreferencesKey("game_mode")
        private val KEY_BOARD_SIZE = stringPreferencesKey("board_size")
        private val KEY_HISTORY = stringPreferencesKey("game_history")
        private val KEY_THEME = stringPreferencesKey("theme")
        private val KEY_BGM = booleanPreferencesKey("bgm_enabled")
        private val KEY_SOUND = booleanPreferencesKey("sound_enabled")
        private val IMMERSIVE_MODE = booleanPreferencesKey("immersive_mode")
        private val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        private val KEY_BG_ANIMATION = booleanPreferencesKey("bg_animation_enabled")
        private val KEY_BOARD_STYLE = stringPreferencesKey("board_style")
        private val KEY_ORIENTATION = stringPreferencesKey("orientation")
    }

    // --- FLOWS ---
    /** Emits selected [GameMode]. Defaults to [GameMode.HARD]. */
    val gameModeFlow = appContext.dataStore.data.map { prefs ->
        GameMode.valueOf(prefs[KEY_GAME_MODE] ?: GameMode.HARD.name)
    }

    /** Emits configured [BoardSize]. Defaults to 3x3 2D board. */
    val boardSizeFlow = appContext.dataStore.data.map { prefs ->
        prefs[KEY_BOARD_SIZE]?.let { Json.decodeFromString<BoardSize>(it) } ?: BoardSize()
    }

    /** Emits application visual [AppTheme]. Defaults to [AppTheme.SYSTEM]. */
    val themeFlow = appContext.dataStore.data.map { prefs ->
        AppTheme.valueOf(prefs[KEY_THEME] ?: AppTheme.SYSTEM.name)
    }

    /** Emits whether fullscreen immersive mode is enabled. */
    val immersiveFlow = appContext.dataStore.data.map { prefs ->
        prefs[IMMERSIVE_MODE] ?: false
    }

    /** Emits whether dynamic system color (Material You) is enabled. */
    val dynamicColorFlow = appContext.dataStore.data.map { prefs ->
        prefs[DYNAMIC_COLOR] ?: false
    }

    /** Emits whether background audio music is enabled. */
    val bgmEnabledFlow = appContext.dataStore.data.map { prefs ->
        prefs[KEY_BGM] ?: false
    }

    /** Emits whether game action sound effects are enabled. */
    val soundEnabledFlow = appContext.dataStore.data.map { prefs ->
        prefs[KEY_SOUND] ?: true
    }

    /** Emits whether canvas line background animations are enabled. */
    val bgAnimationEnabledFlow = appContext.dataStore.data.map { prefs ->
        prefs[KEY_BG_ANIMATION] ?: false
    }

    /** Emits selected board rendering [BoardStyle]. Defaults to [BoardStyle.CLASSIC]. */
    val boardStyleFlow = appContext.dataStore.data.map { prefs ->
        BoardStyle.valueOf(prefs[KEY_BOARD_STYLE] ?: BoardStyle.CLASSIC.name)
    }

    /** Emits selected [Orientation]. Defaults to [Orientation.SYSTEM]. */
    val orientationFlow = appContext.dataStore.data.map { prefs ->
        Orientation.valueOf(prefs[KEY_ORIENTATION] ?: Orientation.SYSTEM.name)
    }

    // --- SETTERS ---
    /** Updates and persists the active [GameMode]. */
    suspend fun setGameMode(mode: GameMode) {
        appContext.dataStore.edit { it[KEY_GAME_MODE] = mode.name }
    }

    /** Serializes and persists board size parameters [BoardSize]. */
    suspend fun setBoardSize(size: BoardSize) {
        appContext.dataStore.edit { it[KEY_BOARD_SIZE] = Json.encodeToString(size) }
    }

    /** Persists application screen orientation choice [Orientation]. */
    suspend fun setOrientation(orientation: Orientation) {
        appContext.dataStore.edit { it[KEY_ORIENTATION] = orientation.name }
    }

    /** Sets and persists user theme [AppTheme]. */
    suspend fun setTheme(theme: AppTheme) {
        appContext.dataStore.edit { it[KEY_THEME] = theme.name }
    }

    /** Toggles immersive mode if supported by device OS level (Android 11 / API 30+). */
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

    /** Toggles Material You Dynamic Color feature on Android 12+ (API 31+). */
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

    /** Toggles background music preference. */
    suspend fun toggleBgm() {
        val current = bgmEnabledFlow.first()
        appContext.dataStore.edit { it[KEY_BGM] = !current }
    }

    /** Toggles sound effects preference. */
    suspend fun toggleSound() {
        val current = soundEnabledFlow.first()
        appContext.dataStore.edit { it[KEY_SOUND] = !current }
    }

    /** Toggles animated canvas lines in menu/game background. */
    suspend fun toggleBgAnimation() {
        val current = bgAnimationEnabledFlow.first()
        appContext.dataStore.edit { it[KEY_BG_ANIMATION] = !current }
    }

    /** Sets board rendering style preference [BoardStyle]. */
    suspend fun setBoardStyle(style: BoardStyle) {
        appContext.dataStore.edit { it[KEY_BOARD_STYLE] = style.name }
    }


    // --- GAME HISTORY ---
    /** Prepends a played match record [GameHistory] to stored JSON array in DataStore. */
    suspend fun addGameHistory(history: GameHistory) {
        val list = getGameHistoryListOnce().toMutableList()
        list.add(0, history)
        val json = Json.encodeToString(list)

        appContext.dataStore.edit { it[KEY_HISTORY] = json }
    }

    /** Flow emitting list of saved match history records [GameHistory]. */
    fun getGameHistoryFlow() = appContext.dataStore.data.map { prefs ->
        prefs[KEY_HISTORY]?.let { Json.decodeFromString<List<GameHistory>>(it) } ?: emptyList()
    }

    /** Deletes specified match entry from saved history list. */
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

    /** Clears all stored match history entries. */
    suspend fun clearAllGameHistory() {
        appContext.dataStore.edit { it.remove(KEY_HISTORY) }
    }
}
