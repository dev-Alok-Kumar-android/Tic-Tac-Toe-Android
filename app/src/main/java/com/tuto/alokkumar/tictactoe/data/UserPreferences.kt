package com.tuto.alokkumar.tictactoe.data

import kotlinx.serialization.Serializable

data class UserPreferences(
    val gameMode: GameMode = GameMode.HARD,
    val theme: AppTheme = AppTheme.SYSTEM,
    val bgmEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val immersiveMode: Boolean = false,
    val dynamicColor: Boolean = false
)

@Serializable
enum class GameMode { PVP, EASY, MEDIUM, HARD }

@Serializable
enum class AppTheme { LIGHT, DARK, SYSTEM }

@Serializable
enum class AppLanguage { ENGLISH, HINDI }