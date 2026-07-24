package com.tuto.alokkumar.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.tuto.alokkumar.tictactoe.core.pref.Preferences
import com.tuto.alokkumar.tictactoe.core.sound.Sound
import com.tuto.alokkumar.tictactoe.ui.theme.TicTacToeTheme

/**
 * Primary entry point Activity for the Tic Tac Toe application.
 *
 * Responsibilities:
 * 1. **Initialization**: Bootstraps singleton managers ([Sound], [Preferences]).
 * 2. **Edge-to-Edge**: Configures system window insets for modern full-screen immersion.
 * 3. **Lifecycle Management**: Orchestrates background music pausing/resuming during OS transitions.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize global dependencies before UI setup
        Sound.init(applicationContext)
        Preferences.init(applicationContext)
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
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
        Sound.pauseBgm()
    }

    override fun onResume() {
        super.onResume()
        // Management moved to AppNavigation for lifecycle synchronization
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up audio resources
        Sound.release()
    }
}