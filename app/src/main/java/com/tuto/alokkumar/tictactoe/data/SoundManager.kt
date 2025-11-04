package com.tuto.alokkumar.tictactoe.data

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import com.tuto.alokkumar.tictactoe.R

class SoundManager(
    var isBgmEnabled: Boolean = true,
    var isSoundEnabled: Boolean = true

) {
    private var bgmPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<String, Int>()


    fun init(context: Context) {
        soundPool = SoundPool.Builder().setMaxStreams(5).build()
        soundMap["move"] = soundPool?.load(context.applicationContext, R.raw.pop, 1) ?: 0
        soundMap["win"] = soundPool?.load(context.applicationContext, R.raw.game_success_alert, 1) ?: 0
        soundMap["lose"] = soundPool?.load(context.applicationContext, R.raw.over, 1) ?: 0
        soundMap["draw"] = soundPool?.load(context.applicationContext, R.raw.tf_notification, 1) ?: 0
    }

    // 🎵 Background music
    fun playBgm(context: Context) {
        if (!isBgmEnabled) return
        if (bgmPlayer == null) {
            bgmPlayer = MediaPlayer.create(context.applicationContext, R.raw.bgm)
            bgmPlayer?.isLooping = true
        }
        bgmPlayer?.start()
    }

    fun stopBgm() {
        bgmPlayer?.pause()
    }

    // 🔊 Sound effects
    fun playSound(name: String) {
        if (!isSoundEnabled || soundPool == null) return
        soundMap[name]?.let { id ->
            Log.d("SoundManager", "playSound: $name")
            soundPool?.play(id, 1f, 1f, 0, 0, 1f)
        }
    }


    fun release() {
        bgmPlayer?.release()
        bgmPlayer = null
        soundPool?.release()
        soundPool = null
    }
}
