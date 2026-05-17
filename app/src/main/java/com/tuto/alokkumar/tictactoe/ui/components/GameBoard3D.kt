package com.tuto.alokkumar.tictactoe.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameBoard3D(
    modifier: Modifier = Modifier,
    board: List<Char?>,
    activeLayer: Int,
    onCellClick: (layer: Int, index2D: Int) -> Unit,
    winLine: List<Int>?,
    winner: Char?,
) {
    Box(
        modifier = modifier, contentAlignment = Alignment.Center
    ) {
        // Step 1: Draw all INACTIVE layers first (in back-to-front order)
        for (layer in 0..2) {
            if (layer != activeLayer) {
                BoardLayerVisual(
                    board = board,
                    layerIndex = layer,
                    isActive = false,
                    onCellClick = null,
                    winLine = winLine,
                    winner = winner
                )
            }
        }

        // Step 2: Draw the ACTIVE layer LAST → it's on top and clickable
        BoardLayerVisual(
            board = board,
            layerIndex = activeLayer,
            isActive = true,
            onCellClick = { index2D -> onCellClick(activeLayer, index2D) },
            winLine = winLine,
            winner = winner
        )
    }
}

@Composable
private fun BoardLayerVisual(
    board: List<Char?>,
    layerIndex: Int,
    isActive: Boolean,
    onCellClick: ((Int) -> Unit)?,
    activeLayer: Int = 1,
    winLine: List<Int>?,
    winner: Char?,
) {
    val scale by animateFloatAsState(
        targetValue = when {
            isActive -> 1.1f
            layerIndex < activeLayer -> 0.9f - (activeLayer - layerIndex) * 0.1f
            else -> 1.0f + (layerIndex - activeLayer) * 0.05f
        }, label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.4f, label = "alpha"
    )

    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f, targetValue = 1.01f, animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOut), repeatMode = RepeatMode.Reverse
        ), label = "pulseScale"
    )

    val winSet = winLine?.toSet()

    Column(
        modifier = Modifier.graphicsLayer {
            scaleX = scale * if (isActive) pulse else 1f
            scaleY = scale * if (isActive) pulse else 1f
            this.alpha = alpha
            this.translationY = (layerIndex - 1) * 30f
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        repeat(3) { row ->
            Row {
                repeat(3) { col ->
                    val index2D = row * 3 + col
                    val globalIndex = layerIndex * 9 + index2D
                    val value = board.getOrNull(globalIndex)

                    val isWinningCell = winSet?.contains(globalIndex) == true

                    key(globalIndex) {
                        GameCell3D(
                            value = value,
                            enabled = isActive && value == null,
                            isWinningCell = isWinningCell,
                            winner = winner,
                            onClick = { onCellClick?.invoke(index2D) })
                    }

                }
            }
        }
    }
}


@Composable
private fun GameCell3D(
    value: Char?,
    enabled: Boolean,
    isWinningCell: Boolean,
    winner: Char?,
    onClick: () -> Unit,
) {
    val cellColor by animateColorAsState(
        targetValue = when {
            winner != null && !isWinningCell -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
            value == 'X' -> MaterialTheme.colorScheme.secondary
            value == 'O' -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.onSurface
        }, label = "cellColor"
    )
    val hasWinner = winner != null

    var pressed by remember { mutableStateOf(false) }

    val pulse = if (hasWinner && isWinningCell) {
        rememberInfiniteTransition(label = "winPulse")
            .animateFloat(
                1f,
                1.06f,
                animationSpec = infiniteRepeatable(
                    tween(900, easing = EaseInOut),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulse"
            ).value
    } else 1f

    val scale by animateFloatAsState(
        targetValue = when {
            hasWinner && isWinningCell -> 1.1f
            pressed -> 0.92f
            else -> 1f
        }, animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow
        ), label = "cellScale"
    )


    val elevation by animateDpAsState(
        targetValue = if (enabled && value == null) 16.dp else 6.dp, label = "cellElevation"
    )

    Surface(
        modifier = Modifier
            .size(90.dp)
            .padding(8.dp)
            .graphicsLayer {
                scaleX = scale * pulse
                scaleY = scale * pulse
            }
            .clickable(
                enabled = enabled && value == null, onClick = {
                    pressed = true
                    onClick()
                    pressed = false
                }),
        shape = RoundedCornerShape(16.dp),
        color = cellColor,
        shadowElevation = elevation,
        tonalElevation = elevation
    ) {
        Box(contentAlignment = Alignment.Center) {
            CellSymbol(value)
        }
    }
}

@Composable
private fun CellSymbol(value: Char?) {
    AnimatedVisibility(
        visible = value != null, enter = fadeIn(tween(150)) + scaleIn(
            initialScale = 0.6f, animationSpec = spring(stiffness = Spring.StiffnessLow)
        )
    ) {
        Text(
            text = value?.toString() ?: "", fontSize = 42.sp, fontWeight = FontWeight.Bold
        )
    }
}


@Preview(
    showBackground = true, backgroundColor = 0xFF000000, widthDp = 420, heightDp = 700
)
@Composable
fun GameBoard3DPreview() {
    // 27 cells for 3×3×3
    val board = remember {
        mutableStateListOf<Char?>().apply {
            repeat(27) { add(null) }
        }
    }

    var activeLayer by remember { mutableIntStateOf(1) }
    var lastClick by remember { mutableStateOf("None") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Active Layer: $activeLayer",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(12.dp))

        GameBoard3D(
            board = board, activeLayer = activeLayer, onCellClick = { layer, index2D ->
            val globalIndex = layer * 9 + index2D

            // Preview-only fake move
            if (board[globalIndex] == null) {
                board[globalIndex] = if ((globalIndex % 2) == 0) 'X' else 'O'
            }

            lastClick = "Layer $layer, Cell $index2D (global $globalIndex)"
        }, winLine = List(3) { it }, winner = null
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Last Click: $lastClick", fontSize = 14.sp
        )

        Spacer(Modifier.height(24.dp))

        // 🔽 Layer control buttons (Preview-friendly)
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    activeLayer = (activeLayer - 1).coerceAtLeast(0)
                }, enabled = activeLayer > 0
            ) {
                Text("⬇ Layer")
            }

            Button(
                onClick = {
                    activeLayer = (activeLayer + 1).coerceAtMost(2)
                }, enabled = activeLayer < 2
            ) {
                Text("⬆ Layer")
            }
        }
    }
}
