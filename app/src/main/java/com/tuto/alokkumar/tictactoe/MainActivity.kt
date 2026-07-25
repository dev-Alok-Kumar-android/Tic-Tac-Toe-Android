package com.tuto.alokkumar.tictactoe

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.tuto.alokkumar.tictactoe.core.pref.PreferencesManager
import com.tuto.alokkumar.tictactoe.core.sound.SoundManager
import com.tuto.alokkumar.tictactoe.core.util.ContextUtils
import com.tuto.alokkumar.tictactoe.data.AppLanguage
import com.tuto.alokkumar.tictactoe.ui.theme.TicTacToeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * Primary entry point Activity for the Tic Tac Toe application.
 *
 * Responsibilities:
 * 1. **Initialization**: Bootstraps singleton managers.
 * 2. **Edge-to-Edge**: Configures system window insets for modern full-screen immersion.
 * 3. **Lifecycle Management**: Orchestrates background music pausing/resuming during OS transitions.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var soundManager: SoundManager

    @Inject
    lateinit var preferences: PreferencesManager

    override fun attachBaseContext(newBase: Context) {
        // Blocks to get the initial language preference
        val lang = runBlocking {
            try {
                // Since this is called before Hilt is fully injected, 
                // we might need to create a temporary instance or use a simpler read.
                // However, PreferencesManager just needs a context.
                val prefs = PreferencesManager(newBase)
                val userPrefs = prefs.userPreferencesFlow.first()
                if (userPrefs.appLanguage == AppLanguage.HINDI) "hi" else "en"
            } catch (_: Exception) {
                "en"
            }
        }
        super.attachBaseContext(ContextUtils.updateLocale(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // React to language changes and recreate activity
        lifecycleScope.launch {
            preferences.userPreferencesFlow.collect { userPrefs ->
                val currentLang = if (userPrefs.appLanguage == AppLanguage.HINDI) "hi" else "en"
                val configLang = resources.configuration.locales[0].language
                if (currentLang != configLang) {
                    recreate()
                }
            }
        }
        
        soundManager.init(applicationContext)

        setContent {
            TicTacToeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                       AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Save battery and user experience by pausing BGM when app is backgrounded
        soundManager.pauseBgm()
    }

    override fun onResume() {
        super.onResume()
        // Management moved to AppNavigation for lifecycle synchronization
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up audio resources
        soundManager.release()
    }
}