package com.tuto.alokkumar.tictactoe.ui.components

import android.os.Build
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun ImmersiveMode(enabled: Boolean) {
    val activity = LocalActivity.current

    LaunchedEffect(enabled) {
        val window = activity?.window ?: return@LaunchedEffect
        val controller = window.insetsController ?: return@LaunchedEffect

        if (enabled) {
            controller.hide(WindowInsets.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            controller.show(WindowInsets.Type.systemBars())
        }
    }
}
