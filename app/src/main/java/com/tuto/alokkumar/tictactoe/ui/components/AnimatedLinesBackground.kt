package com.tuto.alokkumar.tictactoe.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

/**
 * Renders an interactive canvas background featuring floating animated spring chains (snakes)
 * that smoothly float and react to user touch coordinates with gravity-like pull forces.
 *
 * Supports dynamic speed, stiffness linkage calculations, and parallax/Z-Index layer ordering.
 *
 * @param modifier Modifier to apply to the canvas container.
 * @param baseSpeed Speed multiplier for snake point translations.
 * @param color Color used to render lines.
 * @param chainCount Number of snake chains to simulate simultaneously.
 * @param jointsPerChain Length of each chain (number of joint segments).
 * @param stiffness Elasticity factor for link spring simulation. Range: 0.05 (soft) to 0.5 (rigid).
 * @param touchRange Physical attraction radius (in pixels) around drag inputs.
 * @param modifierZIndex Enable 3D depth illusion (parallax layer sorting and opacity offsets).
 */
@Composable
fun AnimatedLinesBackground(
    modifier: Modifier = Modifier,
    baseSpeed: Float = 1f,
    color: Color = Color.Cyan,
    chainCount: Int = 10,
    jointsPerChain: Int = 5,       // How long the snake is
    stiffness: Float = 0.1f,       // Lower = more elastic/soft (0.05 - 0.5)
    touchRange: Float = 300f,      // Radius to pull lines
    modifierZIndex: Boolean = true // Depth illusion
) {
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var frameTick by remember { mutableLongStateOf(0L) }
    var touchPos by remember { mutableStateOf(Offset.Unspecified) }

    data class Chain(
        val points: MutableList<Offset>,
        val velocities: MutableList<Offset>,
        val minSegLen: Float,
        val maxSegLen: Float,
        val zIndex: Float, // 0.1 (far) to 1.0 (near)
        val strokeWidth: Float,
        val alpha: Float
    )

    val chains = remember(chainCount, jointsPerChain) {
        List(chainCount) {
            val z = if (modifierZIndex) Random.nextFloat() * 0.8f + 0.2f else 1f

            val stroke = (2f + Random.nextFloat() * 4f) * z
            val alpha = (0.3f + Random.nextFloat() * 0.7f) * z

            val avgLen = 50f + Random.nextFloat() * 100f
            val minLen = avgLen * 0.5f
            val maxLen = avgLen * 1.5f

            Chain(
                points = MutableList(jointsPerChain) { Offset.Zero },
                velocities = MutableList(jointsPerChain) { Offset.Zero },
                minSegLen = minLen,
                maxSegLen = maxLen,
                zIndex = z,
                strokeWidth = stroke,
                alpha = alpha
            )
        }
    }

    LaunchedEffect(canvasSize) {
        if (canvasSize == Size.Zero) return@LaunchedEffect
        chains.forEach { chain ->
            val startPos = Offset(
                Random.nextFloat() * canvasSize.width,
                Random.nextFloat() * canvasSize.height
            )
            for (i in chain.points.indices) {
                chain.points[i] = startPos + Offset(i * 20f, i * 20f)

                chain.velocities[i] = Offset(
                    (Random.nextFloat() - 0.5f) * 5f,
                    (Random.nextFloat() - 0.5f) * 5f
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(16L.milliseconds)
            if (canvasSize == Size.Zero) continue

            chains.forEach { chain ->
                val zSpeed = baseSpeed * chain.zIndex // Parallax effect: closer moves faster

                chain.points.indices.forEach { i ->
                    val accel = Offset(
                        (Random.nextFloat() - 0.5f) * 0.5f,
                        (Random.nextFloat() - 0.5f) * 0.5f
                    ) * zSpeed

                    var touchForce = Offset.Zero
                    if (touchPos != Offset.Unspecified) {
                        val diff = touchPos - chain.points[i]
                        val dist = diff.getDistance()
                        if (dist < touchRange && dist > 0f) {
                            // Inverse square law for gravity-like pull
                            val strength = (1f - (dist / touchRange)).pow(2) * 2.0f
                            touchForce = diff * (strength * 0.2f) // 0.2f is pull factor
                        }
                    }

                    chain.velocities[i] = (chain.velocities[i] + accel + touchForce) * 0.98f
                    chain.points[i] += chain.velocities[i]
                }

                repeat(1) {
                    for (i in 0 until chain.points.lastIndex) {
                        val p1 = chain.points[i]
                        val p2 = chain.points[i + 1]

                        val adjustedP1 = resolveSpring(p1, p2, chain.minSegLen, chain.maxSegLen, stiffness)
                        val adjustedP2 = resolveSpring(p2, p1, chain.minSegLen, chain.maxSegLen, stiffness)

                        chain.points[i] = adjustedP1
                        chain.points[i + 1] = adjustedP2
                    }
                }

                chain.points.indices.forEach { i ->
                    val (newP, newV) = bounce(chain.points[i], chain.velocities[i], canvasSize)
                    chain.points[i] = newP
                    chain.velocities[i] = newV
                }
            }
            frameTick++
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { touchPos = it },
                    onDragEnd = { touchPos = Offset.Unspecified },
                    onDragCancel = { touchPos = Offset.Unspecified },
                    onDrag = { change, _ ->
                        touchPos = change.position
                    }
                )
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                        val change = event.changes.firstOrNull() ?: continue
                        touchPos =
                            if (change.pressed) change.position
                            else Offset.Unspecified
                    }
                }
            }

    ) {
        frameTick

        canvasSize = size

        val sortedChains = chains.sortedBy { it.zIndex }

        sortedChains.forEach { chain ->
            drawPoints(
                points = chain.points,
                pointMode = PointMode.Polygon, // Connects points smoothly
                color = color.copy(alpha = chain.alpha),
                strokeWidth = chain.strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * Calculates physics spring logic, pushing and pulling point locations to satisfy segment bounds.
 */
private fun resolveSpring(
    current: Offset,
    anchor: Offset,
    minLen: Float,
    maxLen: Float,
    stiffness: Float
): Offset {
    val diff = current - anchor
    val dist = diff.getDistance()

    if (dist == 0f) return current + Offset(0.1f, 0f) // Prevent 0 division

    if (dist in minLen..maxLen) return current

    val targetDist = dist.coerceIn(minLen, maxLen)
    val targetPos = anchor + (diff / dist) * targetDist

    return androidx.compose.ui.geometry.lerp(current, targetPos, stiffness)
}

/**
 * Calculates border collisions and bounces velocity back when exceeding canvas boundaries.
 */
private fun bounce(
    pos: Offset,
    vel: Offset,
    size: Size
): Pair<Offset, Offset> {
    var p = pos
    var v = vel

    if (p.x < 0f) {
        p = p.copy(x = 0f)
        v = v.copy(x = -v.x * 0.9f)
    } else if (p.x > size.width) {
        p = p.copy(x = size.width)
        v = v.copy(x = -v.x * 0.9f)
    }

    if (p.y < 0f) {
        p = p.copy(y = 0f)
        v = v.copy(y = -v.y * 0.9f)
    } else if (p.y > size.height) {
        p = p.copy(y = size.height)
        v = v.copy(y = -v.y * 0.9f)
    }

    return p to v
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun AnimatedSnakePreview() {
    AnimatedLinesBackground(
        baseSpeed = 2f,
        chainCount = 30,
        jointsPerChain = 4,
        stiffness = 0.08f,
        color = Color.Cyan,
        modifierZIndex = true
    )
}