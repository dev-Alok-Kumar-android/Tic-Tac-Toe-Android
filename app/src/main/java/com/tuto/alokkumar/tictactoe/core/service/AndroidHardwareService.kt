package com.tuto.alokkumar.tictactoe.core.service

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.tuto.alokkumar.tictactoe.core.sound.SoundManager
import com.tuto.alokkumar.tictactoe.domain.HardwareService
import com.tuto.alokkumar.tictactoe.domain.model.GameFeedback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidHardwareService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val soundManager: SoundManager
) : HardwareService {

    override fun playFeedback(event: GameFeedback, hapticEnabled: Boolean) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(VibratorManager::class.java)
            vibratorManager?.defaultVibrator ?: return
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        }

        val hasVibrator = vibrator.hasVibrator()

        when (event) {
            GameFeedback.MOVE -> {
                if (hapticEnabled && hasVibrator) vibrate(vibrator, 30)
                soundManager.playSound("move")
            }
            GameFeedback.WIN -> {
                if (hapticEnabled && hasVibrator) vibrateWaveform(vibrator, longArrayOf(0, 100, 50, 200))
                soundManager.playSound("win")
            }
            GameFeedback.LOSE -> {
                if (hapticEnabled && hasVibrator) vibrate(vibrator, 50)
                soundManager.playSound("lose")
            }
            GameFeedback.DRAW -> {
                if (hapticEnabled && hasVibrator) vibrate(vibrator, 50)
                soundManager.playSound("draw")
            }
        }
    }

    private fun vibrate(vibrator: Vibrator, duration: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }

    private fun vibrateWaveform(vibrator: Vibrator, pattern: LongArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }

    override fun startBgm() {
        soundManager.playBgm(context)
    }

    override fun stopBgm() {
        soundManager.stopBgm()
    }
}
