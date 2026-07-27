package com.tuto.alokkumar.tictactoe.core.pref

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tuto.alokkumar.tictactoe.data.AiDifficulty
import com.tuto.alokkumar.tictactoe.data.AppLanguage
import com.tuto.alokkumar.tictactoe.data.AppTheme
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.BoardStyle
import com.tuto.alokkumar.tictactoe.data.FirstMoveBehavior
import com.tuto.alokkumar.tictactoe.data.GameHistory
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.GameStateEntity
import com.tuto.alokkumar.tictactoe.data.NextMoveBehavior
import com.tuto.alokkumar.tictactoe.data.Orientation
import com.tuto.alokkumar.tictactoe.data.UserPreferences
import com.tuto.alokkumar.tictactoe.domain.model.PlayerId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

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
            decodeUserPreferences(json)
        } else {
            // Initial migration from legacy flat keys
            UserPreferences(
                aiDifficulty = safeEnum { AiDifficulty.valueOf(prefs[KEY_GAME_MODE]?.uppercase() ?: "") } ?: AiDifficulty.HARD,
                gameMode = if (prefs[KEY_GAME_MODE]?.uppercase() == "PVP") GameMode.PVP else GameMode.VS_AI,
                boardSize = try { prefs[KEY_BOARD_SIZE]?.let { Json.decodeFromString<BoardSize>(it) } ?: BoardSize() } catch (_: Exception) { BoardSize() },
                theme = safeEnum { AppTheme.valueOf(prefs[KEY_THEME]?.uppercase() ?: "") } ?: AppTheme.SYSTEM,
                bgmEnabled = prefs[KEY_BGM] ?: true,
                soundEnabled = prefs[KEY_SOUND] ?: true,
                immersiveMode = prefs[IMMERSIVE_MODE] ?: false,
                dynamicColor = prefs[DYNAMIC_COLOR] ?: false,
                bgAnimationEnabled = prefs[KEY_BG_ANIMATION] ?: false,
                boardStyle = safeEnum { BoardStyle.valueOf(prefs[KEY_BOARD_STYLE]?.uppercase() ?: "") } ?: BoardStyle.LAYERED_3D,
                orientation = safeEnum { Orientation.valueOf(prefs[KEY_ORIENTATION]?.uppercase() ?: "") } ?: Orientation.SYSTEM,
                p1Name = "Player 1",
                p2Name = "Player 2",
            )
        }
    }

    private fun decodeUserPreferences(json: String): UserPreferences {
        return try {
            val jsonElement = Json.parseToJsonElement(json).jsonObject
            UserPreferences(
                aiDifficulty = safeEnum { 
                    val raw = jsonElement["aiDifficulty"]?.jsonPrimitive?.content ?: jsonElement["gameMode"]?.jsonPrimitive?.content
                    if (raw?.uppercase() == "PVP") AiDifficulty.HARD else AiDifficulty.valueOf(raw?.uppercase() ?: "HARD") 
                } ?: AiDifficulty.HARD,
                gameMode = safeEnum { 
                    val raw = jsonElement["gameMode"]?.jsonPrimitive?.content ?: jsonElement["matchType"]?.jsonPrimitive?.content
                    if (raw?.uppercase() == "PVP") {
                        GameMode.PVP
                    } else if (raw?.uppercase() in listOf("EASY", "MEDIUM", "HARD", "IMPOSSIBLE")) {
                        GameMode.VS_AI
                    } else {
                        GameMode.valueOf(raw?.uppercase() ?: "VS_AI")
                    }
                } ?: GameMode.VS_AI,
                boardSize = try { jsonElement["boardSize"]?.let { Json.decodeFromJsonElement<BoardSize>(it) } ?: BoardSize() } catch (_: Exception) { BoardSize() },
                theme = safeEnum { AppTheme.valueOf(jsonElement["theme"]?.jsonPrimitive?.content?.uppercase() ?: "SYSTEM") } ?: AppTheme.SYSTEM,
                bgmEnabled = jsonElement["bgmEnabled"]?.jsonPrimitive?.booleanOrNull ?: true,
                soundEnabled = jsonElement["soundEnabled"]?.jsonPrimitive?.booleanOrNull ?: true,
                immersiveMode = jsonElement["immersiveMode"]?.jsonPrimitive?.booleanOrNull ?: false,
                dynamicColor = jsonElement["dynamicColor"]?.jsonPrimitive?.booleanOrNull ?: false,
                bgAnimationEnabled = jsonElement["bgAnimationEnabled"]?.jsonPrimitive?.booleanOrNull ?: false,
                boardStyle = safeEnum { BoardStyle.valueOf(jsonElement["boardStyle"]?.jsonPrimitive?.content?.uppercase() ?: "LAYERED_3D") } ?: BoardStyle.LAYERED_3D,
                orientation = safeEnum { Orientation.valueOf(jsonElement["orientation"]?.jsonPrimitive?.content?.uppercase() ?: "SYSTEM") } ?: Orientation.SYSTEM,
                appLanguage = safeEnum { AppLanguage.valueOf(jsonElement["appLanguage"]?.jsonPrimitive?.content?.uppercase() ?: "ENGLISH") } ?: AppLanguage.ENGLISH,
                firstMoveBehavior = safeEnum { FirstMoveBehavior.valueOf(jsonElement["firstMoveBehavior"]?.jsonPrimitive?.content?.uppercase() ?: "PLAYER_X") } ?: FirstMoveBehavior.PLAYER_X,
                nextMoveBehavior = safeEnum { NextMoveBehavior.valueOf(jsonElement["nextMoveBehavior"]?.jsonPrimitive?.content?.uppercase() ?: "ALTERNATING") } ?: NextMoveBehavior.ALTERNATING,
                aiStrength = jsonElement["aiStrength"]?.jsonPrimitive?.intOrNull ?: 75,
                manualMaxDepth = jsonElement["manualMaxDepth"]?.jsonPrimitive?.intOrNull ?: 6,
                hapticEnabled = jsonElement["hapticEnabled"]?.jsonPrimitive?.booleanOrNull ?: true,
                p1Symbol = jsonElement["p1Symbol"]?.jsonPrimitive?.content ?: "X",
                p2Symbol = jsonElement["p2Symbol"]?.jsonPrimitive?.content ?: "O",
                p1Name = jsonElement["p1Name"]?.jsonPrimitive?.content ?: "Player 1",
                p2Name = jsonElement["p2Name"]?.jsonPrimitive?.content ?: "Player 2",
                p1Color = jsonElement["p1Color"]?.jsonPrimitive?.longOrNull ?: 0xFFE91E63,
                p2Color = jsonElement["p2Color"]?.jsonPrimitive?.longOrNull ?: 0xFF2196F3,
                activeMatchId = jsonElement["activeMatchId"]?.jsonPrimitive?.contentOrNull
            )
        } catch (_: Exception) { UserPreferences() }
    }

    private inline fun <reified T : Enum<T>> safeEnum(block: () -> T): T? {
        return try { block() } catch (_: Exception) { null }
    }

    // --- UNIFIED SETTER ---
    suspend fun updatePrefs(transform: (UserPreferences) -> UserPreferences) {
        appContext.dataStore.edit { prefs ->
            val json = prefs[KEY_USER_PREFS]
            val current = if (json != null) decodeUserPreferences(json) else {
                UserPreferences(
                    aiDifficulty = safeEnum { AiDifficulty.valueOf(prefs[KEY_GAME_MODE]?.uppercase() ?: "") } ?: AiDifficulty.HARD,
                    gameMode = if (prefs[KEY_GAME_MODE]?.uppercase() == "PVP") GameMode.PVP else GameMode.VS_AI,
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
            
            val updated = transform(current)
            prefs[KEY_USER_PREFS] = Json.encodeToString(updated)
            
            // Cleanup legacy keys
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


    // --- GAME HISTORY ---
    suspend fun addGameHistory(history: GameHistory) {
        appContext.dataStore.edit { prefs ->
            val rawHistory = prefs[KEY_HISTORY]
            val list = rawHistory?.let { decodeGameHistoryList(it) }?.toMutableList() ?: mutableListOf()
            
            // Atomic Upsert: Find if this match already exists and update it
            val index = list.indexOfFirst { it.matchId == history.matchId }
            if (index != -1) {
                list[index] = history
            } else {
                list.add(0, history)
            }
            
            prefs[KEY_HISTORY] = Json.encodeToString(list)
        }
    }

    fun getGameHistoryFlow() = appContext.dataStore.data.map { prefs ->
        prefs[KEY_HISTORY]?.let { decodeGameHistoryList(it) } ?: emptyList()
    }

    private fun decodeGameHistoryList(json: String): List<GameHistory> {
        return try {
            val jsonArray = Json.parseToJsonElement(json).jsonArray
            jsonArray.map { element ->
                val obj = element.jsonObject
                
                val rawMode = obj["mode"]?.jsonPrimitive?.content?.uppercase()
                
                val matchType = if ((rawMode == "PVP") || (obj["matchType"]?.jsonPrimitive?.content == "PVP") || (obj["gameMode"]?.jsonPrimitive?.content == "PVP")) {
                    GameMode.PVP
                } else {
                    GameMode.VS_AI
                }
                
                val rawDifficulty = obj["difficulty"]?.jsonPrimitive?.content?.uppercase() ?: rawMode
                val finalDifficulty = safeEnum { 
                    if (rawDifficulty == "PVP") AiDifficulty.HARD else AiDifficulty.valueOf(rawDifficulty ?: "HARD")
                } ?: AiDifficulty.HARD
                
                val recordBoardSize = try { 
                    obj["boardSize"]?.let { Json.decodeFromJsonElement<BoardSize>(it) } ?: BoardSize() 
                } catch (_: Exception) { BoardSize() }

                val fallbackState = GameStateEntity(
                    board = List(recordBoardSize.x * recordBoardSize.y * recordBoardSize.z) { null },
                    currentPlayerId = PlayerId.P1.name,
                    winnerId = null,
                    isDraw = false,
                    winLine = null,
                    lastMove = null,
                    p1Wins = 0,
                    p2Wins = 0,
                    draws = 0,
                    boardSize = recordBoardSize
                )

                GameHistory(
                    matchId = obj["matchId"]?.jsonPrimitive?.content ?: obj["dateMillis"]?.jsonPrimitive?.content ?: System.currentTimeMillis().toString(),
                    dateMillis = obj["dateMillis"]?.jsonPrimitive?.longOrNull ?: System.currentTimeMillis(),
                    difficulty = finalDifficulty,
                    gameMode = matchType,
                    state = try { obj["state"]?.let { Json.decodeFromJsonElement<GameStateEntity>(it) } ?: fallbackState } catch (_: Exception) { fallbackState },
                    humanSymbol = obj["humanSymbol"]?.jsonPrimitive?.content ?: "X",
                    p1Symbol = obj["p1Symbol"]?.jsonPrimitive?.content ?: "X",
                    p2Symbol = obj["p2Symbol"]?.jsonPrimitive?.content ?: "O",
                    p1Name = obj["p1Name"]?.jsonPrimitive?.content ?: "Player 1",
                    p2Name = obj["p2Name"]?.jsonPrimitive?.content ?: "Player 2",
                    p1Color = obj["p1Color"]?.jsonPrimitive?.longOrNull ?: 0xFFE91E63,
                    p2Color = obj["p2Color"]?.jsonPrimitive?.longOrNull ?: 0xFF2196F3,
                    aiStrength = obj["aiStrength"]?.jsonPrimitive?.intOrNull ?: 75,
                    manualMaxDepth = obj["manualMaxDepth"]?.jsonPrimitive?.intOrNull ?: 6
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun removeGameHistories(matchIds: List<String>) {
        appContext.dataStore.edit { prefs ->
            val raw = prefs[KEY_HISTORY]
            if (raw != null) {
                val list = decodeGameHistoryList(raw).toMutableList()
                list.removeAll { it.matchId in matchIds }
                prefs[KEY_HISTORY] = Json.encodeToString(list)
            }
        }
    }

    suspend fun clearAllGameHistory() {
        appContext.dataStore.edit { it.remove(KEY_HISTORY) }
    }
}
