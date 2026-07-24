package com.tuto.alokkumar.tictactoe.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Accessor object grouping custom vector graphic icons compiled as [ImageVector] code representations.
 */
object MyIcons {
    /** Target pointer icon indicating the last move cell spot. */
    val JumpToLast: ImageVector
        get() = ImageVector.Builder(
            name = "JumpToLast",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd
            ) {
                moveTo(12f, 8f)
                curveTo(9.79f, 8f, 8f, 9.79f, 8f, 12f)
                curveTo(8f, 14.21f, 9.79f, 16f, 12f, 16f)
                curveTo(14.21f, 16f, 16f, 14.21f, 16f, 12f)
                curveTo(16f, 9.79f, 14.21f, 8f, 12f, 8f)
                close()
                moveTo(20.94f, 11f)
                curveTo(20.48f, 6.83f, 17.17f, 3.52f, 13f, 3.06f)
                verticalLineTo(1f)
                horizontalLineTo(11f)
                verticalLineTo(3.06f)
                curveTo(6.83f, 3.52f, 3.52f, 6.83f, 3.06f, 11f)
                horizontalLineTo(1f)
                verticalLineTo(13f)
                horizontalLineTo(3.06f)
                curveTo(3.52f, 17.17f, 6.83f, 20.48f, 11f, 20.94f)
                verticalLineTo(23f)
                horizontalLineTo(13f)
                verticalLineTo(20.94f)
                curveTo(17.17f, 20.48f, 20.48f, 17.17f, 20.94f, 13f)
                horizontalLineTo(23f)
                verticalLineTo(11f)
                horizontalLineTo(20.94f)
                close()
                moveTo(12f, 19f)
                curveTo(8.13f, 19f, 5f, 15.87f, 5f, 12f)
                curveTo(5f, 8.13f, 8.13f, 5f, 12f, 5f)
                curveTo(15.87f, 5f, 19f, 8.13f, 19f, 12f)
                curveTo(19f, 15.87f, 15.87f, 19f, 12f, 19f)
                close()
            }
        }.build()

    /** Layered card stack representation icon, used for 3D multi-level indicators. */
    val Layers: ImageVector
        get() = ImageVector.Builder(
            name = "Layers",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(11.99f, 18.54f)
                lineToRelative(-7.37f, -5.73f)
                lineTo(3f, 14.07f)
                lineToRelative(9f, 7f)
                lineToRelative(9f, -7f)
                lineToRelative(-1.63f, -1.27f)
                lineToRelative(-7.38f, 5.74f)
                close()
                moveTo(12f, 16f)
                lineToRelative(7.36f, -5.73f)
                lineTo(21f, 9f)
                lineToRelative(-9f, -7f)
                lineToRelative(-9f, 7f)
                lineToRelative(1.63f, 1.27f)
                lineTo(12f, 16f)
                close()
            }
        }.build()

    /** Double vertical bar pause signifier icon, used inside gameplay overlays. */
    val Pause: ImageVector
        get() = ImageVector.Builder(
            name = "Pause",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(6f, 19f)
                horizontalLineToRelative(4f)
                verticalLineTo(5f)
                horizontalLineTo(6f)
                verticalLineTo(19f)
                close()
                moveTo(14f, 5f)
                verticalLineTo(19f)
                horizontalLineToRelative(4f)
                verticalLineTo(5f)
                horizontalLineTo(14f)
                close()
            }
        }.build()
}
