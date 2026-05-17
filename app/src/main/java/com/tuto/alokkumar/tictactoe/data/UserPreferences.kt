package com.tuto.alokkumar.tictactoe.data

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val gameMode: GameMode = GameMode.HARD,
    val boardSize: BoardSize = BoardSize(),
    val theme: AppTheme = AppTheme.SYSTEM,
    val bgmEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val useImmersiveMode: Boolean = false,
    val useDynamicColors: Boolean = false,
    val useDynamicColorScheme: Boolean = false,
    val appLanguage: AppLanguage = AppLanguage.ENGLISH,
) // isko avi use nahi kiya lekin baad me kar sakta hoon

@Serializable
enum class GameMode { PVP, EASY, MEDIUM, HARD }

@Serializable
data class BoardSize(
    val x: Int = 3,
    val y: Int = 3,
    val z: Int = 1,
)

@Serializable
enum class AppTheme { LIGHT, DARK, SYSTEM }

@Serializable
enum class AppLanguage { ENGLISH, HINDI }