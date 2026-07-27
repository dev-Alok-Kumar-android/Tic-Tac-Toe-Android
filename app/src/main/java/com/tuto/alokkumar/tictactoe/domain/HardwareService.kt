package com.tuto.alokkumar.tictactoe.domain

import com.tuto.alokkumar.tictactoe.domain.model.GameFeedback

/**
 * Interface for hardware-related feedback services (haptics and sound).
 */
interface HardwareService {
    
    /** Triggers feedback (sound and haptics) for a specific game event. */
    fun playFeedback(event: GameFeedback, hapticEnabled: Boolean = true)
    
    /** Starts background music playback. */
    fun startBgm()
    
    /** Stops background music playback. */
    fun stopBgm()
}
