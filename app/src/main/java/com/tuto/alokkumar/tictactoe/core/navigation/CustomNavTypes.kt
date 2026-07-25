package com.tuto.alokkumar.tictactoe.core.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.tuto.alokkumar.tictactoe.data.BoardSize
import kotlinx.serialization.json.Json

/**
 * Custom [NavType] for [BoardSize] to support type-safe navigation.
 */
val BoardSizeNavType = object : NavType<BoardSize>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): BoardSize? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): BoardSize {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: BoardSize): String {
        return Uri.encode(Json.encodeToString(value))
    }

    override fun put(bundle: Bundle, key: String, value: BoardSize) {
        bundle.putString(key, Json.encodeToString(value))
    }
}
