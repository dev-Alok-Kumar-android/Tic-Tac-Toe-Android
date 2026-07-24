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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.BoardStyle

/**
 * Dispatcher Composable that renders the game board.
 *
 * Automatically branches between flat 2D classic grid boards and stackable isometric 3D designs
 * depending on style preference configurations and active dimensions parameters.
 *
 * @param board Flattened list of cell contents representing the board grid.
 * @param boardSize Dimensions configuration (X, Y, Z parameters).
 * @param activeLayer Index of the active 2D layer being focused/rendered.
 * @param winLine Indices of cells forming a winning line sequence, if decided.
 * @param lastMove Highlighted index indicating the most recent move in this session.
 * @param onCellClick Callback event triggered with the flat index when an empty cell is tapped.
 * @param boardStyle Theme choice (Classic flat grid or Isometric 3D Layered design).
 * @param winner Character representing the winner ('X' or 'O') or 'D' for draw.
 * @param modifier Modifier applied to the container box.
 */
@Composable
fun GameBoard(
    board: List<Char?>,
    boardSize: BoardSize,
    activeLayer: Int,
    winLine: List<Int>?,
    lastMove: Int? = null,
    onCellClick: (Int) -> Unit,
    boardStyle: BoardStyle = BoardStyle.CLASSIC,
    winner: Char? = null,
    modifier: Modifier = Modifier
) {
    if (boardStyle == BoardStyle.LAYERED_3D && boardSize.z > 1) {
        GameBoard3D(
            board = board,
            boardSize = boardSize,
            activeLayer = activeLayer,
            winLine = winLine,
            winner = winner,
            onCellClick = { layer, index2D -> onCellClick(layer * (boardSize.x * boardSize.y) + index2D) },
            modifier = modifier
        )
    } else {
        ClassicGameBoard(
            board = board,
            boardSize = boardSize,
            activeLayer = activeLayer,
            winLine = winLine,
            lastMove = lastMove,
            onCellClick = onCellClick,
            modifier = modifier
        )
    }
}

/**
 * Standard 2D classic flat grid rendering the specified layer cells.
 */
@Composable
fun ClassicGameBoard(
    board: List<Char?>,
    boardSize: BoardSize,
    activeLayer: Int,
    winLine: List<Int>?,
    lastMove: Int? = null,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val x = boardSize.x
    val y = boardSize.y
    val layerOffset = activeLayer * (x * y)

    val infiniteTransition = rememberInfiniteTransition(label = "lastMovePulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        for (i in 0 until y) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                for (j in 0 until x) {
                    val index = layerOffset + (i * x + j)
                    GameCell(
                        index = index,
                        cellValue = board.getOrNull(index),
                        isWinCell = winLine?.contains(index) == true,
                        isLastMove = lastMove == index,
                        pulseAlpha = pulseAlpha,
                        x = x,
                        y = y,
                        onClick = { onCellClick(index) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Individual interactive grid cell that responds to hover, click actions and pulses on winning conditions.
 */
@Composable
private fun GameCell(
    index: Int,
    cellValue: Char?,
    isWinCell: Boolean,
    isLastMove: Boolean,
    pulseAlpha: Float,
    x: Int,
    y: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cellColor by animateColorAsState(
        targetValue = when {
            isWinCell -> Color(0xFF4CAF50)
            cellValue == 'X' -> MaterialTheme.colorScheme.secondary
            cellValue == 'O' -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
        },
        label = "cellColor"
    )

    val borderColor = when {
        isWinCell -> Color.White
        isLastMove -> MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha)
        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(6.dp)
            .shadow(
                elevation = if (isLastMove || isWinCell) 12.dp else 2.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = if (isLastMove) MaterialTheme.colorScheme.primary else cellColor,
                spotColor = if (isLastMove) MaterialTheme.colorScheme.primary else cellColor
            )
            .background(cellColor, RoundedCornerShape(16.dp))
            .border(
                width = if (isLastMove) 4.dp else if (isWinCell) 3.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .graphicsLayer {
                if (isLastMove) {
                    val scale = 0.95f + (pulseAlpha * 0.05f)
                    scaleX = scale
                    scaleY = scale
                }
            }
            .clickable(enabled = cellValue == null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = cellValue != null,
            enter = fadeIn() + scaleIn(initialScale = 0.5f)
        ) {
            Text(
                text = cellValue?.toString() ?: "",
                fontSize = (220 / maxOf(x, y)).sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.3f),
                        offset = Offset(2f, 4f),
                        blurRadius = 8f
                    )
                )
            )
        }
    }
}

/**
 * Renders stackable 3D multi-layered board structures using perspective transformations.
 */
@Composable
fun GameBoard3D(
    modifier: Modifier = Modifier,
    board: List<Char?>,
    boardSize: BoardSize,
    activeLayer: Int,
    onCellClick: (layer: Int, index2D: Int) -> Unit,
    winLine: List<Int>?,
    winner: Char?,
) {
    Box(
        modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center
    ) {
        for (layer in 0 until boardSize.z) {
            if (layer != activeLayer) {
                BoardLayerVisual(
                    board = board,
                    boardSize = boardSize,
                    layerIndex = layer,
                    isActive = false,
                    onCellClick = null,
                    winLine = winLine,
                    winner = winner,
                    activeLayer = activeLayer
                )
            }
        }

        BoardLayerVisual(
            board = board,
            boardSize = boardSize,
            layerIndex = activeLayer,
            isActive = true,
            onCellClick = { index2D -> onCellClick(activeLayer, index2D) },
            winLine = winLine,
            winner = winner,
            activeLayer = activeLayer
        )
    }
}

/**
 * Visual slice representation of a singular 2D level inside the stackable 3D board view.
 */
@Composable
private fun BoardLayerVisual(
    board: List<Char?>,
    boardSize: BoardSize,
    layerIndex: Int,
    isActive: Boolean,
    onCellClick: ((Int) -> Unit)?,
    activeLayer: Int,
    winLine: List<Int>?,
    winner: Char?,
) {
    val scale by animateFloatAsState(
        targetValue = when {
            isActive -> 1.0f
            layerIndex < activeLayer -> 0.8f - (activeLayer - layerIndex) * 0.1f
            else -> 0.9f + (layerIndex - activeLayer) * 0.05f
        }, label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.4f, label = "alpha"
    )

    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f, targetValue = 1.02f, animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOut), repeatMode = RepeatMode.Reverse
        ), label = "pulseScale"
    )

    val winSet = winLine?.toSet()

    Column(
        modifier = Modifier.graphicsLayer {
            scaleX = scale * if (isActive) pulse else 1f
            scaleY = scale * if (isActive) pulse else 1f
            this.alpha = alpha
            this.translationY = (layerIndex - activeLayer) * 60f
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        repeat(boardSize.y) { row ->
            Row {
                repeat(boardSize.x) { col ->
                    val index2D = row * boardSize.x + col
                    val globalIndex = layerIndex * (boardSize.x * boardSize.y) + index2D
                    val value = board.getOrNull(globalIndex)

                    val isWinningCell = winSet?.contains(globalIndex) == true

                    key(globalIndex) {
                        GameCell3D(
                            value = value,
                            size = (240 / maxOf(boardSize.x, boardSize.y)).dp,
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


/**
 * 3D isometric styled individual grid interactive cell.
 */
@Composable
private fun GameCell3D(
    value: Char?,
    size: androidx.compose.ui.unit.Dp,
    enabled: Boolean,
    isWinningCell: Boolean,
    winner: Char?,
    onClick: () -> Unit,
) {
    val cellColor by animateColorAsState(
        targetValue = when {
            isWinningCell -> Color(0xFF4CAF50)
            winner != null && !isWinningCell -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
            value == 'X' -> MaterialTheme.colorScheme.secondary
            value == 'O' -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.surface
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
        targetValue = if (enabled && value == null) 8.dp else 2.dp, label = "cellElevation"
    )

    Surface(
        modifier = Modifier
            .size(size)
            .padding(maxOf(2.dp, size / 20))
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
        shape = RoundedCornerShape(size / 6),
        color = cellColor,
        shadowElevation = elevation,
        tonalElevation = elevation
    ) {
        Box(contentAlignment = Alignment.Center) {
            CellSymbol(value, fontSize = (size.value * 0.4).sp)
        }
    }
}

/**
 * Scale-animates symbol characters (X or O) when a player updates cell data.
 */
@Composable
private fun CellSymbol(value: Char?, fontSize: androidx.compose.ui.unit.TextUnit) {
    AnimatedVisibility(
        visible = value != null, enter = fadeIn(tween(150)) + scaleIn(
            initialScale = 0.6f, animationSpec = spring(stiffness = Spring.StiffnessLow)
        )
    ) {
        Text(
            text = value?.toString() ?: "",
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
