package com.tuto.alokkumar.tictactoe.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuto.alokkumar.tictactoe.data.BoardSize
import com.tuto.alokkumar.tictactoe.data.BoardStyle

/**
 * Dispatcher Composable that renders the game board.
 */
@Composable
fun GameBoard(
    board: List<String?>,
    boardSize: BoardSize,
    activeLayer: Int,
    winLine: List<Int>?,
    p1Symbol: String,
    p2Symbol: String,
    p1Color: Color,
    p2Color: Color,
    modifier: Modifier = Modifier,
    lastMove: Int? = null,
    onCellClick: (Int) -> Unit,
    boardStyle: BoardStyle = BoardStyle.CLASSIC,
    winner: String? = null,
) {
    if (boardStyle == BoardStyle.LAYERED_3D && boardSize.z > 1) {
        GameBoard3D(
            board = board,
            boardSize = boardSize,
            activeLayer = activeLayer,
            winLine = winLine,
            winner = winner,
            p1Symbol = p1Symbol,
            p2Symbol = p2Symbol,
            p1Color = p1Color,
            p2Color = p2Color,
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
            p1Symbol = p1Symbol,
            p2Symbol = p2Symbol,
            p1Color = p1Color,
            p2Color = p2Color,
            onCellClick = onCellClick,
            modifier = modifier
        )
    }
}

/**
 * Standard 2D classic flat grid rendering.
 */
@Composable
fun ClassicGameBoard(
    board: List<String?>,
    boardSize: BoardSize,
    activeLayer: Int,
    winLine: List<Int>?,
    p1Symbol: String,
    p2Symbol: String,
    p1Color: Color,
    p2Color: Color,
    modifier: Modifier = Modifier,
    lastMove: Int? = null,
    onCellClick: (Int) -> Unit,
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
                        cellValue = board.getOrNull(index),
                        isWinCell = winLine?.contains(index) == true,
                        isLastMove = lastMove == index,
                        pulseAlpha = pulseAlpha,
                        x = x,
                        y = y,
                        p1Symbol = p1Symbol,
                        p2Symbol = p2Symbol,
                        p1Color = p1Color,
                        p2Color = p2Color,
                        onClick = { onCellClick(index) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Individual interactive grid cell.
 */
@Composable
private fun GameCell(
    cellValue: String?,
    isWinCell: Boolean,
    isLastMove: Boolean,
    pulseAlpha: Float,
    x: Int,
    y: Int,
    p1Symbol: String,
    p2Symbol: String,
    p1Color: Color,
    p2Color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cellColor by animateColorAsState(
        targetValue = when {
            isWinCell -> Color(0xFF4CAF50)
            cellValue == p1Symbol -> p1Color
            cellValue == p2Symbol -> p2Color
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
            .padding(4.dp)
            .shadow(
                elevation = if (isLastMove || isWinCell) 8.dp else 1.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .background(cellColor, RoundedCornerShape(12.dp))
            .border(
                width = if (isLastMove) 3.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = cellValue == null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = cellValue != null,
            enter = fadeIn() + scaleIn(initialScale = 0.5f)
        ) {
            Text(
                text = cellValue ?: "",
                fontSize = (200 / maxOf(x, y)).sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 3D multi-layered board structures.
 */
@Composable
fun GameBoard3D(
    modifier: Modifier = Modifier,
    board: List<String?>,
    boardSize: BoardSize,
    activeLayer: Int,
    p1Symbol: String,
    p2Symbol: String,
    p1Color: Color,
    p2Color: Color,
    onCellClick: (layer: Int, index2D: Int) -> Unit,
    winLine: List<Int>?,
    winner: String?,
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
                    activeLayer = activeLayer,
                    p1Symbol = p1Symbol,
                    p2Symbol = p2Symbol,
                    p1Color = p1Color,
                    p2Color = p2Color
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
            activeLayer = activeLayer,
            p1Symbol = p1Symbol,
            p2Symbol = p2Symbol,
            p1Color = p1Color,
            p2Color = p2Color
        )
    }
}

@Composable
private fun BoardLayerVisual(
    board: List<String?>,
    boardSize: BoardSize,
    layerIndex: Int,
    isActive: Boolean,
    onCellClick: ((Int) -> Unit)?,
    activeLayer: Int,
    winLine: List<Int>?,
    winner: String?,
    p1Symbol: String,
    p2Symbol: String,
    p1Color: Color,
    p2Color: Color
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

    val winSet = winLine?.toSet()

    Column(
        modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
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
                            size = (220 / maxOf(boardSize.x, boardSize.y)).dp,
                            enabled = isActive && value == null,
                            isWinningCell = isWinningCell,
                            winner = winner,
                            p1Symbol = p1Symbol,
                            p2Symbol = p2Symbol,
                            p1Color = p1Color,
                            p2Color = p2Color,
                            onClick = { onCellClick?.invoke(index2D) })
                    }
                }
            }
        }
    }
}

@Composable
private fun GameCell3D(
    value: String?,
    size: androidx.compose.ui.unit.Dp,
    enabled: Boolean,
    isWinningCell: Boolean,
    winner: String?,
    p1Symbol: String,
    p2Symbol: String,
    p1Color: Color,
    p2Color: Color,
    onClick: () -> Unit,
) {
    val cellColor by animateColorAsState(
        targetValue = when {
            isWinningCell -> Color(0xFF4CAF50)
            winner != null && !isWinningCell -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
            value == p1Symbol -> p1Color
            value == p2Symbol -> p2Color
            else -> MaterialTheme.colorScheme.surface
        }, label = "cellColor"
    )

    Surface(
        modifier = Modifier
            .size(size)
            .padding(2.dp)
            .clickable(enabled = enabled && value == null, onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = cellColor,
        shadowElevation = if (enabled) 4.dp else 1.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            AnimatedVisibility(visible = value != null) {
                Text(
                    text = value ?: "",
                    fontSize = (size.value * 0.5).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
