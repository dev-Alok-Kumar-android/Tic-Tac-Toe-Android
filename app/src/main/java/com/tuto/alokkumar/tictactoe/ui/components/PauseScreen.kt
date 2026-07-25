package com.tuto.alokkumar.tictactoe.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tuto.alokkumar.tictactoe.R

/**
 * Overlay overlay rendering standard match control actions when the active game session is paused.
 *
 * Dynamically adjusts its design depending on device constraints to support Landscape and Portrait layouts.
 *
 * @param modifier Modifier applied to the parent overlay container box.
 * @param onPlay Trigger callback when the user resumes the active game session.
 * @param onRestart Trigger callback to restart the game.
 * @param onHome Action callback navigating the user back to the application main menu.
 * @param onSettings Action callback navigating the user to settings.
 * @param visible Visibility state used to animate fade-in or scale-in transitions.
 */
@Composable
fun PauseScreen(
    modifier: Modifier = Modifier,
    onPlay: () -> Unit,
    onRestart: () -> Unit,
    onHome: () -> Unit,
    onSettings: () -> Unit = {},
    visible: Boolean = true,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn()
    ) {
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f)),
            contentAlignment = Alignment.Center
        ) {
            val isLandscape = maxWidth > maxHeight
            val compactMode = maxHeight < 400.dp

            if (isLandscape) {
                // --- LANDSCAPE LAYOUT ---
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(if (compactMode) 16.dp else 32.dp)
                ) {
                    // Left section - Options
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        val sideIconSize = if (compactMode) 64.dp else 80.dp
                        val sideInnerIconSize = if (compactMode) 40.dp else 56.dp

                        IconButton(
                            onClick = onRestart,
                            modifier = Modifier.size(sideIconSize)
                        ) {
                            Icon(
                                imageVector = MyIcons.Refresh,
                                contentDescription = stringResource(R.string.restart_match),
                                modifier = Modifier.size(sideInnerIconSize),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        IconButton(
                            onClick = onHome,
                            modifier = Modifier.size(sideIconSize)
                        ) {
                            Icon(
                                imageVector = MyIcons.Home,
                                contentDescription = stringResource(R.string.home),
                                modifier = Modifier.size(sideInnerIconSize),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        IconButton(
                            onClick = onSettings,
                            modifier = Modifier.size(sideIconSize)
                        ) {
                            Icon(
                                imageVector = MyIcons.Settings,
                                contentDescription = stringResource(R.string.settings),
                                modifier = Modifier.size(sideInnerIconSize),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    // Right section - Text + Play button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = stringResource(R.string.paused),
                            style = if (compactMode) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(if (compactMode) 8.dp else 24.dp))

                        val playSize = if (compactMode) 120.dp else 180.dp
                        val playInnerSize = if (compactMode) 100.dp else 160.dp

                        IconButton(
                            onClick = onPlay,
                            modifier = Modifier.size(playSize)
                        ) {
                            Icon(
                                imageVector = MyIcons.PlayArrow,
                                contentDescription = stringResource(R.string.resume_game),
                                modifier = Modifier.size(playInnerSize),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            } else {
                // --- PORTRAIT LAYOUT ---
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.paused),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    IconButton(
                        onClick = onPlay,
                        modifier = Modifier.size(180.dp)
                    ) {
                        Icon(
                            imageVector = MyIcons.PlayArrow,
                            contentDescription = stringResource(R.string.resume_game),
                            modifier = Modifier.size(160.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val portraitIconSize = 80.dp
                        val portraitInnerIconSize = 60.dp

                        IconButton(
                            onClick = onRestart,
                            modifier = Modifier.size(portraitIconSize)
                        ) {
                            Icon(
                                imageVector = MyIcons.Refresh,
                                contentDescription = stringResource(R.string.restart_match),
                                modifier = Modifier.size(portraitInnerIconSize),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        IconButton(
                            onClick = onHome,
                            modifier = Modifier.size(portraitIconSize)
                        ) {
                            Icon(
                                imageVector = MyIcons.Home,
                                contentDescription = stringResource(R.string.home),
                                modifier = Modifier.size(portraitInnerIconSize),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        IconButton(
                            onClick = onSettings,
                            modifier = Modifier.size(portraitIconSize)
                        ) {
                            Icon(
                                imageVector = MyIcons.Settings,
                                contentDescription = stringResource(R.string.settings),
                                modifier = Modifier.size(portraitInnerIconSize),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 500, heightDp = 300)
@Composable
private fun PauseScreenLandscapeCompactPreview() {
    MaterialTheme {
        Surface {
            PauseScreen(
                onPlay = {},
                onRestart = {},
                onHome = {},
                onSettings = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PauseScreenPortraitPreview() {
    MaterialTheme {
        Surface {
            PauseScreen(
                onPlay = {},
                onRestart = {},
                onHome = {},
                onSettings = {}
            )
        }
    }
}
