package com.tuto.alokkumar.tictactoe.core.util

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.Locale

/**
 * Utility for wrapping context with a specific Locale to support in-app language switching.
 */
object ContextUtils {

    fun updateLocale(context: Context, localeCode: String): ContextWrapper {
        val locale = Locale.forLanguageTag(localeCode)
        Locale.setDefault(locale)
        
        val resources = context.resources
        val configuration = Configuration(resources.configuration)
        
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        
        val newContext = context.createConfigurationContext(configuration)
        return ContextWrapper(newContext)
    }
}
