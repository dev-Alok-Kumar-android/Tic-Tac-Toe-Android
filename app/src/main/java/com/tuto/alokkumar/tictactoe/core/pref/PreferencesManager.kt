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
import com.tuto.alokkumar.tictactoe.data.FirstMoveBehavior
import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.NextMoveBehavior
import com.tuto.alokkumar.tictactoe.data.Orientation
import com.tuto.alokkumar.tictactoe.data.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore("settings_prefs")

/**
 * Manages application preferences using AndroidX DataStore Preferences.
 *
 * Consolidates all user settings into a single [UserPreferences] data class stored as JSON.
 */
class PreferencesManager(private val appContext: Context) {

    companion object {
        private val KEY_USER_PREFS = stringPreferencesKey("user_preferences")
        private val KEY_HISTORY = stringPreferencesKey("game_history")

        // Legacy keys for migration
        private val KEY_GAME_MODE = stringPreferencesKey("game_mode")
        private val KEY_BOARD_SIZE = stringPreferencesKey("board_size")
        private val KEY_THEME = stringPreferencesKey("theme")
        private val KEY_BGM = booleanPreferencesKey("bgm_enabled")
        private val KEY_SOUND = booleanPreferencesKey("sound_enabled")
        private val IMMERSIVE_MODE = booleanPreferencesKey("immersive_mode")
        private val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        private val KEY_BG_ANIMATION = booleanPreferencesKey("bg_animation_enabled")
        private val KEY_BOARD_STYLE = stringPreferencesKey("board_style")
        private val KEY_ORIENTATION = stringPreferencesKey("orientation")
    }

    /**
     * Reactive flow emitting the entire [UserPreferences] object.
     * Includes automatic migration logic for legacy flat-key preferences.
     */
    val userPreferencesFlow: Flow<UserPreferences> = appContext.dataStore.data.map { prefs ->
        val json = prefs[KEY_USER_PREFS]
        if (json != null) {
            try {
                Json.decodeFromString<UserPreferences>(json)
            } catch (_: Exception) {
                UserPreferences()
            }
        } else {
            // Migration logic: Construct from legacy keys if they exist
            UserPreferences(
                gameMode = safeEnum { GameMode.valueOf(prefs[KEY_GAME_MODE]?.uppercase() ?: "") } ?: GameMode.HARD,
                boardSize = try { prefs[KEY_BOARD_SIZE]?.let { Json.decodeFromString<BoardSize>(it) } ?: BoardSize() } catch (_: Exception) { BoardSize() },
                theme = safeEnum { AppTheme.valueOf(prefs[KEY_THEME]?.uppercase() ?: "") } ?: AppTheme.SYSTEM,
                bgmEnabled = prefs[KEY_BGM] ?: true,
                soundEnabled = prefs[KEY_SOUND] ?: true,
                immersiveMode = prefs[IMMERSIVE_MODE] ?: false,
                dynamicColor = prefs[DYNAMIC_COLOR] ?: false,
                bgAnimationEnabled = prefs[KEY_BG_ANIMATION] ?: false,
                boardStyle = safeEnum { BoardStyle.valueOf(prefs[KEY_BOARD_STYLE]?.uppercase() ?: "") } ?: BoardStyle.LAYERED_3D,
                orientation = safeEnum { Orientation.valueOf(prefs[KEY_ORIENTATION]?.uppercase() ?: "") } ?: Orientation.SYSTEM
            )
        }
    }

    private inline fun <reified T : Enum<T>> safeEnum(block: () -> T): T? {
        return try { block() } catch (_: Exception) { null }
    }

    // --- DERIVED FLOWS ---
    val gameModeFlow = userPreferencesFlow.map { it.gameMode }
    val boardSizeFlow = userPreferencesFlow.map { it.boardSize }
    val themeFlow = userPreferencesFlow.map { it.theme }
    val immersiveFlow = userPreferencesFlow.map { it.immersiveMode }
    val dynamicColorFlow = userPreferencesFlow.map { it.dynamicColor }
    val bgmEnabledFlow = userPreferencesFlow.map { it.bgmEnabled }
    val soundEnabledFlow = userPreferencesFlow.map { it.soundEnabled }
    val bgAnimationEnabledFlow = userPreferencesFlow.map { it.bgAnimationEnabled }
    val boardStyleFlow = userPreferencesFlow.map { it.boardStyle }
    val orientationFlow = userPreferencesFlow.map { it.orientation }
    val firstMoveBehaviorFlow = userPreferencesFlow.map { it.firstMoveBehavior }
    val nextMoveBehaviorFlow = userPreferencesFlow.map { it.nextMoveBehavior }
    val aiStrengthFlow = userPreferencesFlow.map { it.aiStrength }
    val isAdvancedAiEnabledFlow = userPreferencesFlow.map { it.isAdvancedAiEnabled }
    val manualMaxDepthFlow = userPreferencesFlow.map { it.manualMaxDepth }

    // --- UNIFIED SETTER ---
    suspend fun updatePrefs(transform: (UserPreferences) -> UserPreferences) {
        appContext.dataStore.edit { prefs ->
            val current = prefs[KEY_USER_PREFS]?.let {
                try { Json.decodeFromString<UserPreferences>(it) } catch (_: Exception) { null }
            } ?: UserPreferences(
                // On first write, we can also pull from legacy if it's the first time
                gameMode = safeEnum { GameMode.valueOf(prefs[KEY_GAME_MODE]?.uppercase() ?: "") } ?: GameMode.HARD,
                boardSize = try { prefs[KEY_BOARD_SIZE]?.let { Json.decodeFromString<BoardSize>(it) } ?: BoardSize() } catch (_: Exception) { BoardSize() },
                theme = safeEnum { AppTheme.valueOf(prefs[KEY_THEME]?.uppercase() ?: "") } ?: AppTheme.SYSTEM,
                bgmEnabled = prefs[KEY_BGM] ?: true,
                soundEnabled = prefs[KEY_SOUND] ?: true,
                immersiveMode = prefs[IMMERSIVE_MODE] ?: false,
                dynamicColor = prefs[DYNAMIC_COLOR] ?: false,
                bgAnimationEnabled = prefs[KEY_BG_ANIMATION] ?: false,
                boardStyle = safeEnum { BoardStyle.valueOf(prefs[KEY_BOARD_STYLE]?.uppercase() ?: "") } ?: BoardStyle.LAYERED_3D,
                orientation = safeEnum { Orientation.valueOf(prefs[KEY_ORIENTATION]?.uppercase() ?: "") } ?: Orientation.SYSTEM
            )
            
            val updated = transform(current)
            prefs[KEY_USER_PREFS] = Json.encodeToString(updated)
            
            // Cleanup legacy keys once migrated
            prefs.remove(KEY_GAME_MODE)
            prefs.remove(KEY_BOARD_SIZE)
            prefs.remove(KEY_THEME)
            prefs.remove(KEY_BGM)
            prefs.remove(KEY_SOUND)
            prefs.remove(IMMERSIVE_MODE)
            prefs.remove(DYNAMIC_COLOR)
            prefs.remove(KEY_BG_ANIMATION)
            prefs.remove(KEY_BOARD_STYLE)
            prefs.remove(KEY_ORIENTATION)
        }
    }

    // --- SETTERS ---
    suspend fun setGameMode(mode: GameMode) = updatePrefs { it.copy(gameMode = mode) }
    suspend fun setBoardSize(size: BoardSize) = updatePrefs { it.copy(boardSize = size) }
    suspend fun setOrientation(orientation: Orientation) = updatePrefs { it.copy(orientation = orientation) }
    suspend fun setTheme(theme: AppTheme) = updatePrefs { it.copy(theme = theme) }
    suspend fun setBoardStyle(style: BoardStyle) = updatePrefs { it.copy(boardStyle = style) }
    suspend fun setFirstMoveBehavior(behavior: FirstMoveBehavior) = updatePrefs { it.copy(firstMoveBehavior = behavior) }
    suspend fun setNextMoveBehavior(behavior: NextMoveBehavior) = updatePrefs { it.copy(nextMoveBehavior = behavior) }

    suspend fun toggleImmersiveMode() {
        val current = immersiveFlow.first()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            updatePrefs { it.copy(immersiveMode = !current) }
        } else {
            updatePrefs { it.copy(immersiveMode = false) }
            withContext(Dispatchers.Main) {
                Toast.makeText(appContext, "Fullscreen not supported", Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun toggleDynamicColor() {
        val current = dynamicColorFlow.first()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            updatePrefs { it.copy(dynamicColor = !current) }
        } else {
            updatePrefs { it.copy(dynamicColor = false) }
            withContext(Dispatchers.Main) {
                Toast.makeText(appContext, "DynamicColor not supported", Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun toggleBgm() = updatePrefs { it.copy(bgmEnabled = !it.bgmEnabled) }
    suspend fun toggleSound() = updatePrefs { it.copy(soundEnabled = !it.soundEnabled) }
    suspend fun toggleBgAnimation() = updatePrefs { it.copy(bgAnimationEnabled = !it.bgAnimationEnabled) }


    // --- GAME HISTORY ---
    /** Prepends a played match record [GameHistory] to stored JSON array in DataStore. */
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
