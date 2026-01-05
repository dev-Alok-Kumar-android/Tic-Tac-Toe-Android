package com.tuto.alokkumar.tictactoe.core.sound

import android.content.Context

object Sound {
    val manager = SoundManager()

    fun init(context: Context) = manager.init(context.applicationContext)

    fun playBgm(context: Context) = manager.playBgm(context)

    fun stopBgm() = manager.stopBgm()

    fun pauseBgm() = manager.pauseBgm()

    fun play(name: String) = manager.playSound(name)

    fun setBgmEnabled(enabled: Boolean) {
        manager.isBgmEnabled = enabled
        if (!enabled) stopBgm()
    }

    fun setSoundEnabled(enabled: Boolean) { manager.isSoundEnabled = enabled }

    fun release() = manager.release()

}
