package com.tuto.alokkumar.tictactoe.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuto.alokkumar.tictactoe.data.AppTheme
import com.tuto.alokkumar.tictactoe.data.BoardStyle
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.Orientation
import com.tuto.alokkumar.tictactoe.ui.components.NumberPicker
import com.tuto.alokkumar.tictactoe.ui.components.SettingSelector
import com.tuto.alokkumar.tictactoe.ui.components.SwitchSetting
import com.tuto.alokkumar.tictactoe.viewModel.SettingsViewModel
import java.util.Calendar

/**
 * Settings configuration screen providing full controls over difficulty, grid sizes, themes, orientation, BGM and sounds,
 * fullscreen layouts, dynamic wallpapers, background animation, and pre-loading build metadata.
 *
 * @param viewModel Attached settings state view model.
 * @param onBack Callback back navigation action.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val selectedGameMode by viewModel.selectedGameMode.collectAsStateWithLifecycle()
    val isImmersiveMode by viewModel.immersiveMode.collectAsStateWithLifecycle()
    val bgmEnabled by viewModel.bgmEnabled.collectAsStateWithLifecycle()
    val themeDark by viewModel.theme.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()
    val dynamicColor by viewModel.dynamicColor.collectAsStateWithLifecycle()
    val boardSize by viewModel.boardSize.collectAsStateWithLifecycle()
    val bgAnimationEnabled by viewModel.bgAnimationEnabled.collectAsStateWithLifecycle()
    val boardStyle by viewModel.boardStyle.collectAsStateWithLifecycle()
    val orientation by viewModel.orientation.collectAsStateWithLifecycle()
    
    val context = LocalContext.current
    val version = remember(context) {
        try {
            context.packageManager.getPackageInfo(
                context.packageName,
                0
            ).versionName ?: "Unknown"
        } catch (_: Exception) {
            "1.0.0"
        }
    }
    val year = Calendar.getInstance().get(Calendar.YEAR)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SettingSelector(Modifier, "Difficulty", GameMode.entries, selectedGameMode) { viewModel.setGameMode(it as GameMode) }

            SettingSelector(Modifier, "Board Style", BoardStyle.entries, boardStyle) { viewModel.setBoardStyle(it as BoardStyle) }

            Text("Board Configuration", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NumberPicker(
                    label = "Rows",
                    value = boardSize.y,
                    onValueChange = { viewModel.setBoardSize(boardSize.copy(y = it)) },
                    range = 3..10,
                    modifier = Modifier.weight(1f)
                )
                NumberPicker(
                    label = "Cols",
                    value = boardSize.x,
                    onValueChange = { viewModel.setBoardSize(boardSize.copy(x = it)) },
                    range = 3..10,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NumberPicker(
                    label = "Layers",
                    value = boardSize.z,
                    onValueChange = { viewModel.setBoardSize(boardSize.copy(z = it)) },
                    range = 1..10,
                    modifier = Modifier.weight(1f)
                )
                NumberPicker(
                    label = "To Win",
                    value = boardSize.winCondition,
                    onValueChange = { viewModel.setBoardSize(boardSize.copy(winCondition = it)) },
                    range = minOf(3, maxOf(boardSize.x, boardSize.y, boardSize.z))..maxOf(boardSize.x, boardSize.y, boardSize.z),
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider()

            SettingSelector(Modifier, "Theme", AppTheme.entries, themeDark) { viewModel.setTheme(it as AppTheme)}
            SettingSelector(Modifier, "Orientation", Orientation.entries, orientation) { viewModel.setOrientation(it as Orientation) }

            SwitchSetting(
                checked = bgmEnabled,
                onCheckedChange = { viewModel.toggleBgm() },
                text = "Background Music",
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider()

            SwitchSetting(
                checked = soundEnabled,
                onCheckedChange = { viewModel.toggleSound() },
                text = "Sound Effects",
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider()

            SwitchSetting(
                checked = isImmersiveMode,
                onCheckedChange = { viewModel.toggleImmersiveMode() },
                text = "FullScreen Mode",
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider()

            SwitchSetting(
                checked = dynamicColor,
                onCheckedChange = { viewModel.toggleDynamicColor() },
                text = "Dynamic Color",
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider()

            SwitchSetting(
                checked = bgAnimationEnabled,
                onCheckedChange = { viewModel.toggleBgAnimation() },
                text = "Background Animations",
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))
            Text("Version v$version", style = MaterialTheme.typography.bodySmall)
            Text("© $year Tic Tac Toe Game", style = MaterialTheme.typography.bodySmall)
            Text("Developed by Alok Kumar", style = MaterialTheme.typography.bodySmall)
        }
    }
}
