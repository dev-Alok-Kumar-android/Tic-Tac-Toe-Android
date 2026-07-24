package com.tuto.alokkumar.tictactoe.core.sound

import android.content.Context

/**
 * Singleton wrapper around [SoundManager] providing convenient app-wide sound triggers.
 */
object Sound {
    /** Delegated [SoundManager] instance handling sound audio pools and player lifecycle. */
    val manager = SoundManager()

    /** Initialises sound effect resources using context. */
    fun init(context: Context) = manager.init(context.applicationContext)

    /** Starts playing continuous looping background music. */
    fun playBgm(context: Context) = manager.playBgm(context)

    /** Stops and releases background music playback resources. */
    fun stopBgm() = manager.stopBgm()

    /** Pauses background music. */
    fun pauseBgm() = manager.pauseBgm()

    /**
     * Plays a specific loaded sound effect by key name (e.g. "move", "win", "lose", "draw").
     */
    fun play(name: String) = manager.playSound(name)

    /** Enables or disables background music playback. */
    fun setBgmEnabled(enabled: Boolean) {
        manager.isBgmEnabled = enabled
        if (!enabled) stopBgm()
    }

    /** Enables or disables sound effects playback. */
    fun setSoundEnabled(enabled: Boolean) { manager.isSoundEnabled = enabled }

    /** Releases all [SoundPool] and [android.media.MediaPlayer] instances. */
    fun release() = manager.release()

}
