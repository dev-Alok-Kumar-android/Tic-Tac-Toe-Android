package com.tuto.alokkumar.tictactoe.core.sound

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import com.tuto.alokkumar.tictactoe.R

/**
 * Manages game sound effects using [SoundPool] and background music using [MediaPlayer].
 *
 * Preloads raw audio resources for low-latency playback during user interaction.
 *
 * @property isBgmEnabled Flag enabling or disabling background music.
 * @property isSoundEnabled Flag enabling or disabling action sound effects.
 */
class SoundManager(
    var isBgmEnabled: Boolean = true,
    var isSoundEnabled: Boolean = true
) {
    private var bgmPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<String, Int>()

    /**
     * Initializes the [SoundPool] and loads raw audio assets (move, win, lose, draw).
     *
     * @param context Application context used to load raw resources.
     */
    fun init(context: Context) {
        if (soundPool != null) return

        soundPool = SoundPool.Builder().setMaxStreams(5).build()

        soundPool?.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                Log.d("SoundManager", "Sound loaded: $sampleId")
            } else {
                Log.e("SoundManager", "Failed to load sound: $sampleId")
            }
        }

        soundMap["move"] = soundPool?.load(context, R.raw.pop, 1) ?: 0
        soundMap["win"] = soundPool?.load(context, R.raw.game_success_alert, 1) ?: 0
        soundMap["lose"] = soundPool?.load(context, R.raw.over, 1) ?: 0
        soundMap["draw"] = soundPool?.load(context, R.raw.tf_notification, 1) ?: 0
    }

    // 🎵 Background music
    /**
     * Starts background music playback in loop mode if enabled.
     *
     * @param context Context used to create [MediaPlayer].
     */
    fun playBgm(context: Context) {
        if (!isBgmEnabled) return
        try {
            if (bgmPlayer == null) {
                bgmPlayer = MediaPlayer.create(context.applicationContext, R.raw.bgm)
                bgmPlayer?.isLooping = true
            }
            if (bgmPlayer?.isPlaying == false) {
                bgmPlayer?.start()
            }
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing BGM", e)
        }
    }

    /**
     * Stops background music playback and frees media resources.
     */
    fun stopBgm() {
        try {
            bgmPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
        } catch (e: Exception) {
            Log.e("SoundManager", "Error stopping BGM", e)
        } finally {
            bgmPlayer = null
        }
    }

    /**
     * Pauses background music.
     */
    fun pauseBgm() {
        if (bgmPlayer?.isPlaying == true) {
            bgmPlayer?.pause()
        }
    }

    // 🔊 Sound effects
    /**
     * Plays a preloaded audio sample if sound effect setting is enabled.
     *
     * @param name Key name of the sound effect ("move", "win", "lose", "draw").
     */
    fun playSound(name: String) {
        if (!isSoundEnabled || soundPool == null) return
        soundMap[name]?.let { id ->
            if (id != 0) {
                Log.d("SoundManager", "playSound: $name")
                soundPool?.play(id, 1f, 1f, 0, 0, 1f)
            }
        }
    }

    /**
     * Releases active [MediaPlayer] and [SoundPool] instances to reclaim system memory.
     */
    fun release() {
        stopBgm()
        soundPool?.release()
        soundPool = null
        soundMap.clear()
    }
}
