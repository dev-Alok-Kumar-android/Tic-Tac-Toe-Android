package com.tuto.alokkumar.tictactoe.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Unique "Cyber-Grid" icon collection for Tic Tac Toe.
 */
object MyIcons {

    /** Tactical Crosshair - Jump to last move */
    val JumpToLast: ImageVector
        get() = ImageVector.Builder(
            name = "JumpToLast",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(11f, 1f)
                verticalLineToRelative(3f)
                curveTo(7.1f, 4.5f, 4.1f, 7.6f, 3.5f, 11.5f)
                horizontalLineTo(1f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(2.5f)
                curveTo(4.1f, 17.4f, 7.1f, 20.5f, 11f, 21f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(-2f)
                curveTo(16.9f, 20.5f, 19.9f, 17.4f, 20.5f, 13.5f)
                horizontalLineTo(23f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(-2.5f)
                curveTo(19.9f, 7.6f, 16.9f, 4.5f, 13f, 4f)
                verticalLineTo(1f)
                horizontalLineToRelative(-2f)
                close()
                // Inner Target
                moveTo(12f, 7f)
                curveToRelative(2.8f, 0f, 5f, 2.2f, 5f, 5f)
                reflectiveCurveToRelative(-2.2f, 5f, -5f, 5f)
                reflectiveCurveToRelative(-5f, -2.2f, -5f, -5f)
                reflectiveCurveToRelative(2.2f, -5f, 5f, -5f)
                close()
                moveTo(12f, 10f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                reflectiveCurveToRelative(0.9f, 2f, 2f, 2f)
                reflectiveCurveToRelative(2f, -0.9f, 2f, -2f)
                reflectiveCurveToRelative(-0.9f, -2f, -2f, -2f)
                close()
            }
        }.build()

    /** Isometric 3D Board Stack */
    val Layers: ImageVector
        get() = ImageVector.Builder(
            name = "Layers",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Top layer
                moveTo(12f, 2f)
                lineTo(3f, 7f)
                lineToRelative(9f, 5f)
                lineToRelative(9f, -5f)
                lineToRelative(-9f, -5f)
                close()
                // Middle layer shadow
                moveTo(12f, 13f)
                lineTo(3.5f, 8.5f)
                verticalLineToRelative(2f)
                lineTo(12f, 15f)
                lineToRelative(8.5f, -4.5f)
                verticalLineToRelative(-2f)
                lineTo(12f, 13f)
                close()
                // Bottom layer
                moveTo(12f, 17f)
                lineTo(3.5f, 12.5f)
                verticalLineToRelative(2f)
                lineTo(12f, 19f)
                lineToRelative(8.5f, -4.5f)
                verticalLineToRelative(-2f)
                lineTo(12f, 17f)
                close()
            }
        }.build()

    /** Bold Tactical Pause Bars */
    val Pause: ImageVector
        get() = ImageVector.Builder(
            name = "Pause",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(5f, 4f)
                horizontalLineToRelative(6f)
                verticalLineToRelative(16f)
                lineTo(5f, 20f)
                verticalLineTo(4f)
                close()
                moveTo(13f, 4f)
                horizontalLineToRelative(6f)
                verticalLineToRelative(16f)
                horizontalLineToRelative(-6f)
                verticalLineTo(4f)
                close()
            }
        }.build()

    /** Modern Robot AI Head */
    val Face: ImageVector
        get() = ImageVector.Builder(
            name = "Face",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Head shell
                moveTo(4f, 9f)
                curveToRelative(0f, -1.1f, 0.9f, -2f, 2f, -2f)
                horizontalLineToRelative(12f)
                curveToRelative(1.1f, 0f, 2f, 0.9f, 2f, 2f)
                verticalLineToRelative(9f)
                curveToRelative(0f, 1.1f, -0.9f, 2f, -2f, 2f)
                lineTo(6f, 20f)
                curveToRelative(-1.1f, 0f, -2f, -0.9f, -2f, -2f)
                verticalLineTo(9f)
                close()
                // Left Eye
                moveTo(9f, 13.5f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                reflectiveCurveToRelative(-0.9f, -2f, -2f, -2f)
                reflectiveCurveToRelative(-2f, 0.9f, -2f, 2f)
                reflectiveCurveToRelative(0.9f, 2f, 2f, 2f)
                close()
                // Right Eye
                moveTo(15f, 13.5f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                reflectiveCurveToRelative(-0.9f, -2f, -2f, -2f)
                reflectiveCurveToRelative(-2f, 0.9f, -2f, 2f)
                reflectiveCurveToRelative(0.9f, 2f, 2f, 2f)
                close()
                // Antenna
                moveTo(11f, 3f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(4f)
                horizontalLineToRelative(-2f)
                verticalLineTo(3f)
                close()
            }
        }.build()

    /** Bold Tactical Chevron Down */
    val KeyboardArrowDown: ImageVector
        get() = ImageVector.Builder(
            name = "KeyboardArrowDown",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 17f)
                lineTo(4f, 9f)
                horizontalLineToRelative(16f)
                lineToRelative(-8f, 8f)
                close()
            }
        }.build()

    /** Bold Tactical Chevron Up */
    val KeyboardArrowUp: ImageVector
        get() = ImageVector.Builder(
            name = "KeyboardArrowUp",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 7f)
                lineToRelative(8f, 8f)
                lineTo(4f, 15f)
                lineToRelative(8f, -8f)
                close()
            }
        }.build()

    /** Tactical Tapered Loop */
    val Refresh: ImageVector
        get() = ImageVector.Builder(
            name = "Refresh",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 4f)

                curveTo(16.42f, 4f, 20f, 7.58f, 20f, 12f)
                curveTo(20f, 16.42f, 16.42f, 20f, 12f, 20f)
                curveTo(8.5f, 20f, 5.5f, 17.8f, 4.2f, 14.5f)

                lineTo(1f, 14.5f)
                lineTo(5.5f, 9f)
                lineTo(10f, 14.5f)

                lineTo(7.5f, 14.5f)
                curveTo(8.5f, 16.5f, 10.1f, 17.5f, 12f, 17.5f)
                curveTo(15f, 17.5f, 17.5f, 15f, 17.5f, 12f)
                curveTo(17.5f, 9f, 15f, 6.5f, 12f, 6.5f)

                close()
            }
        }.build()

    /** Tactical Visor Pilot */
    val Person: ImageVector
        get() = ImageVector.Builder(
            name = "Person",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Helmet
                moveTo(12f, 2f)
                curveTo(9.24f, 2f, 7f, 4.24f, 7f, 7f)
                curveToRelative(0f, 2.76f, 2.24f, 5f, 5f, 5f)
                reflectiveCurveToRelative(5f, -2.24f, 5f, -5f)
                curveToRelative(0f, -2.76f, -2.24f, -5f, -5f, -5f)
                close()
                // Visor line
                moveTo(8f, 7.5f)
                horizontalLineToRelative(8f)
                verticalLineToRelative(1f)
                horizontalLineTo(8f)
                verticalLineTo(7.5f)
                close()
                // Shoulders
                moveTo(4f, 16f)
                curveToRelative(0f, -1.1f, 0.9f, -2f, 2f, -2f)
                horizontalLineToRelative(12f)
                curveToRelative(1.1f, 0f, 2f, 0.9f, 2f, 2f)
                verticalLineToRelative(4f)
                horizontalLineTo(4f)
                verticalLineTo(16f)
                close()
            }
        }.build()

    /** Nova Technical Star */
    val Star: ImageVector
        get() = ImageVector.Builder(
            name = "Star",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 2f)
                lineToRelative(2.5f, 7.5f)
                lineTo(22f, 12f)
                lineToRelative(-7.5f, 2.5f)
                lineTo(12f, 22f)
                lineToRelative(-2.5f, -7.5f)
                lineTo(2f, 12f)
                lineToRelative(7.5f, -2.5f)
                lineTo(12f, 2f)
                close()
                // Inner core
                moveTo(12f, 9.5f)
                lineToRelative(1f, 2.5f)
                lineToRelative(2.5f, 1f)
                lineToRelative(-2.5f, 1f)
                lineToRelative(-1f, 2.5f)
                lineToRelative(-1f, -2.5f)
                lineToRelative(-2.5f, -1f)
                lineToRelative(2.5f, -1f)
                lineToRelative(1f, -2.5f)
                close()
            }
        }.build()

    /** Isometric History Stack */
    val History: ImageVector
        get() = ImageVector.Builder(
            name = "History",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Stack of boards
                moveTo(12f, 3f)
                lineTo(3f, 8f)
                lineToRelative(9f, 5f)
                lineToRelative(9f, -5f)
                lineToRelative(-9f, -5f)
                close()
                moveTo(3f, 11f)
                lineToRelative(9f, 5f)
                lineToRelative(9f, -5f)
                verticalLineToRelative(2f)
                lineToRelative(-9f, 5f)
                lineToRelative(-9f, -5f)
                verticalLineToRelative(-2f)
                close()
                // Clock overlay
                moveTo(19f, 16f)
                curveToRelative(-1.66f, 0f, -3f, 1.34f, -3f, 3f)
                reflectiveCurveToRelative(1.34f, 3f, 3f, 3f)
                reflectiveCurveToRelative(3f, -1.34f, 3f, -3f)
                reflectiveCurveToRelative(-1.34f, -3f, -3f, -3f)
                close()
                moveTo(19f, 18f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(1f)
                verticalLineToRelative(0.5f)
                horizontalLineToRelative(-1.5f)
                verticalLineTo(18f)
                horizontalLineTo(19f)
                close()
            }
        }.build()

    /** Tech-Gear Settings */
    val Settings: ImageVector
        get() = ImageVector.Builder(
            name = "Settings",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Outer Hex-Gear
                moveTo(12f, 1f)
                lineToRelative(-3.5f, 2f)
                lineTo(5f, 3f)
                verticalLineToRelative(4f)
                lineToRelative(-3f, 2f)
                verticalLineToRelative(6f)
                lineToRelative(3f, 2f)
                verticalLineToRelative(4f)
                horizontalLineToRelative(3.5f)
                lineToRelative(3.5f, 2f)
                lineToRelative(3.5f, -2f)
                horizontalLineTo(19f)
                verticalLineToRelative(-4f)
                lineToRelative(3f, -2f)
                verticalLineTo(9f)
                lineToRelative(-3f, -2f)
                verticalLineTo(3f)
                horizontalLineToRelative(-3.5f)
                lineTo(12f, 1f)
                close()

                // Central Hollow (creates a gear look)
                moveTo(12f, 15.5f)
                curveToRelative(-1.93f, 0f, -3.5f, -1.57f, -3.5f, -3.5f)
                reflectiveCurveToRelative(1.57f, -3.5f, 3.5f, -3.5f)
                reflectiveCurveToRelative(3.5f, 1.57f, 3.5f, 3.5f)
                reflectiveCurveToRelative(-1.57f, 3.5f, -3.5f, 3.5f)
                close()

                // Core Pin
                moveTo(12f, 10.5f)
                curveToRelative(-0.83f, 0f, -1.5f, 0.67f, -1.5f, 1.5f)
                reflectiveCurveToRelative(0.67f, 1.5f, 1.5f, 1.5f)
                reflectiveCurveToRelative(1.5f, -0.67f, 1.5f, -1.5f)
                reflectiveCurveToRelative(-0.67f, -1.5f, -1.5f, -1.5f)
                close()
            }
        }.build()

    /** Solid Tech Info */
    val Info: ImageVector
        get() = ImageVector.Builder(
            name = "Info",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Circle outline
                moveTo(12f, 2f)
                curveTo(6.5f, 2f, 2f, 6.5f, 2f, 12f)
                reflectiveCurveToRelative(4.5f, 10f, 10f, 10f)
                reflectiveCurveToRelative(10f, -4.5f, 10f, -10f)
                reflectiveCurveTo(17.5f, 2f, 12f, 2f)
                close()
                // The "i" as a cutout
                moveTo(11f, 7f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(-2f)
                verticalLineTo(7f)
                close()
                moveTo(11f, 10f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(7f)
                horizontalLineToRelative(-2f)
                verticalLineTo(10f)
                close()
            }
        }.build()

    /** Bold Play Triangle */
    val PlayArrow: ImageVector
        get() = ImageVector.Builder(
            name = "PlayArrow",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(7f, 4f)
                verticalLineToRelative(16f)
                lineToRelative(13f, -8f)
                lineTo(7f, 4f)
                close()
                // Motion tail
                moveTo(4f, 6f)
                horizontalLineToRelative(1.5f)
                verticalLineToRelative(12f)
                lineTo(4f, 18f)
                verticalLineTo(6f)
                close()
            }
        }.build()

    /** Bold Tactical Plus */
    val Add: ImageVector
        get() = ImageVector.Builder(
            name = "Add",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19f, 11f)
                horizontalLineToRelative(-6f)
                verticalLineTo(5f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(6f)
                horizontalLineTo(5f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(6f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(-6f)
                horizontalLineToRelative(6f)
                verticalLineToRelative(-2f)
                close()
            }
        }.build()

    /** Bold Tactical Minus */
    val Remove: ImageVector
        get() = ImageVector.Builder(
            name = "Remove",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19f, 11f)
                horizontalLineTo(5f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(14f)
                verticalLineToRelative(-2f)
                close()
            }
        }.build()

    /** Solid Tech Base */
    val Home: ImageVector
        get() = ImageVector.Builder(
            name = "Home",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(10.0f, 20.0f)
                verticalLineToRelative(-6.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(6.0f)
                horizontalLineToRelative(5.0f)
                verticalLineToRelative(-8.0f)
                horizontalLineToRelative(3.0f)
                lineTo(12.0f, 3.0f)
                lineTo(2.0f, 12.0f)
                horizontalLineToRelative(3.0f)
                verticalLineToRelative(8.0f)
                close()
            }
        }.build()

    /** Clean Tactical Back Arrow */
    val ArrowBack: ImageVector
        get() = ImageVector.Builder(
            name = "ArrowBack",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(21f, 11f)
                horizontalLineTo(6.8f)
                lineToRelative(4.6f, -4.6f)
                lineTo(10f, 5f)
                lineToRelative(-7f, 7f)
                lineToRelative(7f, 7f)
                lineToRelative(1.4f, -1.4f)
                lineTo(6.8f, 13f)
                horizontalLineTo(21f)
                verticalLineToRelative(-2f)
                close()
            }
        }.build()

    /** Multi-select Done/Check */
    val Done: ImageVector
        get() = ImageVector.Builder(
            name = "Done",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(9f, 16.2f)
                lineTo(4.8f, 12f)
                lineToRelative(-1.4f, 1.4f)
                lineTo(9f, 19f)
                lineTo(21f, 7f)
                lineToRelative(-1.4f, -1.4f)
                lineTo(9f, 16.2f)
                close()
            }
        }.build()

    /** Multi-select Delete/Trash */
    val Delete: ImageVector
        get() = ImageVector.Builder(
            name = "Delete",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(6f, 19f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(8f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(7f)
                horizontalLineTo(6f)
                verticalLineTo(19f)
                close()
                moveTo(19f, 4f)
                horizontalLineToRelative(-3.5f)
                lineToRelative(-1f, -1f)
                horizontalLineToRelative(-5f)
                lineToRelative(-1f, 1f)
                horizontalLineTo(5f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(14f)
                verticalLineTo(4f)
                close()
            }
        }.build()

    val Close: ImageVector
        get() = ImageVector.Builder(
            name = "Close",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19f, 6.41f)
                lineTo(17.59f, 5f)
                lineTo(12f, 10.59f)
                lineTo(6.41f, 5f)
                lineTo(5f, 6.41f)
                lineTo(10.59f, 12f)
                lineTo(5f, 17.59f)
                lineTo(6.41f, 19f)
                lineTo(12f, 13.41f)
                lineTo(17.59f, 19f)
                lineTo(19f, 17.59f)
                lineTo(13.41f, 12f)
                lineTo(19f, 6.41f)
                close()
            }
        }.build()

    /** Filter/Slider bars icon */
    val Filter: ImageVector
        get() = ImageVector.Builder(
            name = "Filter",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(3f, 17f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(6f)
                verticalLineToRelative(-2f)
                lineTo(3f, 17f)
                close()
                moveTo(3f, 5f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(18f)
                verticalLineToRelative(-2f)
                lineTo(3f, 5f)
                close()
                moveTo(3f, 11f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(12f)
                verticalLineToRelative(-2f)
                lineTo(3f, 11f)
                close()
            }
        }.build()

}


@Composable
fun IconPreviewList() {
    val icons = listOf(
        "JumpToLast" to MyIcons.JumpToLast,
        "Layers" to MyIcons.Layers,
        "Pause" to MyIcons.Pause,
        "Face" to MyIcons.Face,
        "KeyboardArrowDown" to MyIcons.KeyboardArrowDown,
        "KeyboardArrowUp" to MyIcons.KeyboardArrowUp,
        "Refresh" to MyIcons.Refresh,
        "Person" to MyIcons.Person,
        "Star" to MyIcons.Star,
        "History" to MyIcons.History,
        "Settings" to MyIcons.Settings,
        "Info" to MyIcons.Info,
        "PlayArrow" to MyIcons.PlayArrow,
        "Add" to MyIcons.Add,
        "Remove" to MyIcons.Remove,
        "Home" to MyIcons.Home,
        "ArrowBack" to MyIcons.ArrowBack,
        "Done" to MyIcons.Done,
        "Delete" to MyIcons.Delete,
        "Close" to MyIcons.Close
    )

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(icons) { (name, icon) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = icon,
                        contentDescription = name,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IconPreviewListPreview() {
    MaterialTheme {
        IconPreviewList()
    }
}
