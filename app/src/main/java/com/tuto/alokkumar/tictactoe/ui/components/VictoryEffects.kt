package com.tuto.alokkumar.tictactoe.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.random.Random

/**
 * Renders a celebratory confetti effect using a particle system.
 * Distributed across the full screen width with realistic fluttering.
 */
@Composable
fun VictoryEffects() {
    // Create particles with normalized coordinates (0 to 1) to avoid measurement race conditions
    val particles = remember {
        List(80) {
            ConfettiParticle(
                relativeX = Random.nextFloat(),
                relativeY = -Random.nextFloat() * 0.5f, // Start above the screen
                color = listOf(
                    Color(0xFFF44336), // Red
                    Color(0xFFFFEB3B), // Yellow
                    Color(0xFF2196F3), // Blue
                    Color(0xFF4CAF50), // Green
                    Color(0xFFFF9800), // Orange
                    Color(0xFF9C27B0)  // Purple
                ).random(),
                speedMultiplier = 0.8f + Random.nextFloat() * 1.5f,
                sizePx = 20f + Random.nextFloat() * 30f,
                rotationSpeed = 0.5f + Random.nextFloat() * 2f,
                horizontalOscillation = 0.2f + Random.nextFloat() * 0.8f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            // Actual pixel position based on normalized coordinates
            val startX = particle.relativeX * size.width
            val startY = particle.relativeY * size.height

            // Calculate current vertical position with looping
            // We multiply by size.height + 400 to ensure they fall past the bottom
            val verticalTravel = progress * particle.speedMultiplier * (size.height + 400f)
            val currentY = (startY + verticalTravel) % (size.height + 400f)
            
            // Add side-to-side sway
            val sway = kotlin.math.sin(progress * 8f + startX) * 40f * particle.horizontalOscillation
            val currentX = startX + sway

            // Only draw if on screen
            if (currentY in 0f..size.height) {
                withTransform({
                    rotate(
                        degrees = progress * 720f * particle.rotationSpeed,
                        pivot = Offset(currentX + particle.sizePx / 2, currentY + particle.sizePx / 2)
                    )
                }) {
                    drawRect(
                        color = particle.color,
                        topLeft = Offset(currentX, currentY),
                        size = Size(particle.sizePx, particle.sizePx * 0.6f)
                    )
                }
            }
        }
    }
}

private data class ConfettiParticle(
    val relativeX: Float,
    val relativeY: Float,
    val color: Color,
    val speedMultiplier: Float,
    val sizePx: Float,
    val rotationSpeed: Float,
    val horizontalOscillation: Float
)
