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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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

            if (isLandscape) {
                // --- LANDSCAPE LAYOUT ---
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                ) {
                    // Left section - Options
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onRestart,
                            modifier = Modifier.size(70.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Restart",
                                modifier = Modifier.size(50.dp),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        IconButton(
                            onClick = onHome,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home",
                                modifier = Modifier.size(60.dp),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        IconButton(
                            onClick = onSettings,
                            modifier = Modifier.size(70.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                modifier = Modifier.size(50.dp),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    // Right section - Text + Play button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Paused",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        IconButton(
                            onClick = onPlay,
                            modifier = Modifier.size(160.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Resume Game",
                                modifier = Modifier.size(140.dp),
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
                        text = "Paused",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    IconButton(
                        onClick = onPlay,
                        modifier = Modifier.size(180.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Resume Game",
                            modifier = Modifier.size(160.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onRestart,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Restart",
                                modifier = Modifier.size(60.dp),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        IconButton(
                            onClick = onHome,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home",
                                modifier = Modifier.size(60.dp),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        IconButton(
                            onClick = onSettings,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                modifier = Modifier.size(60.dp),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 700, heightDp = 400)
@Composable
private fun PauseScreenLandscapePreview() {
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
