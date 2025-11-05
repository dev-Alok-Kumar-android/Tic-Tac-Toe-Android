package com.tuto.alokkumar.tictactoe.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.PreferencesManager
import com.tuto.alokkumar.tictactoe.viewModel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = SettingsViewModel(prefs = PreferencesManager(LocalContext.current)),
    onBack: () -> Unit,
) {
    val selectedGameMode by viewModel.selectedGameMode.collectAsState()
    val isImmersiveMode by viewModel.immersiveMode.collectAsState()
    val bgmEnabled by viewModel.bgmEnabled.collectAsState()
    val themeDark by viewModel.themeDark.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            })
        }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🎮 Game Mode
            Text("Game Difficulty", style = MaterialTheme.typography.titleMedium)
            GameModeSelector(selectedMode = selectedGameMode, onSelect = { viewModel.setGameMode(it) })
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            SwitchSetting(
                checked = bgmEnabled,
                onCheckedChange = { viewModel.toggleBgm() },
                text = "Background Music",
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            SwitchSetting(
                checked = soundEnabled,
                onCheckedChange = { viewModel.toggleSound() },
                text = "Sound Effects",
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            SwitchSetting(
                checked = themeDark,
                onCheckedChange = { viewModel.toggleTheme() },
                text = "Dark Theme",
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            SwitchSetting(
                checked = isImmersiveMode,
                onCheckedChange = { viewModel.toggleImmersiveMode() },
                text = "FullScreen Mode",
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            Spacer(modifier = Modifier.weight(1f))
            Text("Version 1.0", style = MaterialTheme.typography.bodySmall)
            Text("© 2025 Tic Tac Toe Game", style = MaterialTheme.typography.bodySmall)
            Text("Developed by Alok Kumar", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun SwitchSetting(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun GameModeSelector(
    selectedMode: GameMode,
    onSelect: (GameMode) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        GameMode.entries.forEach { mode ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                RadioButton(selected = mode == selectedMode, onClick = { onSelect(mode) })
                Text(mode.name, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
