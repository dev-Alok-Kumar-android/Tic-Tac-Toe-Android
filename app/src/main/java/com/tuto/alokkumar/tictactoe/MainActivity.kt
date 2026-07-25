package com.tuto.alokkumar.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.tuto.alokkumar.tictactoe.core.sound.SoundManager
import com.tuto.alokkumar.tictactoe.ui.theme.TicTacToeTheme
import dagger.hilt.android.AndroidEntryPoint
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Note: Global managers initialization now handled by Hilt injection
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