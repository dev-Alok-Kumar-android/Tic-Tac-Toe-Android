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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
        Sound.pauseBgm()
    }

    override fun onResume() {
        super.onResume()
        if (Sound.manager.isBgmEnabled) {
            Sound.playBgm(this)
        }
    }
}