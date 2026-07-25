package com.tuto.alokkumar.tictactoe.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuto.alokkumar.tictactoe.R
import com.tuto.alokkumar.tictactoe.data.AppLanguage
import com.tuto.alokkumar.tictactoe.data.AppTheme
import com.tuto.alokkumar.tictactoe.data.BoardStyle
import com.tuto.alokkumar.tictactoe.data.FirstMoveBehavior
import com.tuto.alokkumar.tictactoe.data.GameMode
import com.tuto.alokkumar.tictactoe.data.NextMoveBehavior
import com.tuto.alokkumar.tictactoe.data.Orientation
import com.tuto.alokkumar.tictactoe.ui.components.MyIcons
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
    val firstMoveBehavior by viewModel.firstMoveBehavior.collectAsStateWithLifecycle()
    val nextMoveBehavior by viewModel.nextMoveBehavior.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val aiStrength by viewModel.aiStrength.collectAsStateWithLifecycle()
    val isAdvancedAiEnabled by viewModel.isAdvancedAiEnabled.collectAsStateWithLifecycle()
    val manualMaxDepth by viewModel.manualMaxDepth.collectAsStateWithLifecycle()
    
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
            TopAppBar(title = { Text(stringResource(R.string.settings)) }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(MyIcons.ArrowBack, contentDescription = stringResource(R.string.cancel))
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
            SettingSelector(Modifier, stringResource(R.string.difficulty), GameMode.entries, selectedGameMode) { viewModel.setGameMode(it as GameMode) }

            Text(stringResource(R.string.ai_skill_level, aiStrength), style = MaterialTheme.typography.labelLarge, modifier = Modifier.fillMaxWidth())
            Slider(
                value = aiStrength.toFloat(),
                onValueChange = { viewModel.setAiStrength(it.toInt()) },
                valueRange = 1f..100f,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            SwitchSetting(
                checked = isAdvancedAiEnabled,
                onCheckedChange = { viewModel.toggleAdvancedAi() },
                text = stringResource(R.string.advanced_ai),
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(visible = isAdvancedAiEnabled) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.manual_depth, manualMaxDepth), style = MaterialTheme.typography.bodySmall)
                    Slider(
                        value = manualMaxDepth.toFloat(),
                        onValueChange = { viewModel.setManualMaxDepth(it.toInt()) },
                        valueRange = 2f..12f,
                        steps = 9,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        stringResource(R.string.depth_warning),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            HorizontalDivider()

            SettingSelector(Modifier, stringResource(R.string.first_move), FirstMoveBehavior.entries, firstMoveBehavior) { viewModel.setFirstMoveBehavior(it as FirstMoveBehavior) }

            SettingSelector(Modifier, stringResource(R.string.next_game_start), NextMoveBehavior.entries, nextMoveBehavior) { viewModel.setNextMoveBehavior(it as NextMoveBehavior) }

            SettingSelector(Modifier, stringResource(R.string.style), BoardStyle.entries, boardStyle) { viewModel.setBoardStyle(it as BoardStyle) }

            Text(stringResource(R.string.board_config), style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NumberPicker(
                    label = stringResource(R.string.rows),
                    value = boardSize.y,
                    onValueChange = { viewModel.setBoardSize(boardSize.copy(y = it)) },
                    range = 3..10,
                    modifier = Modifier.weight(1f)
                )
                NumberPicker(
                    label = stringResource(R.string.cols),
                    value = boardSize.x,
                    onValueChange = { viewModel.setBoardSize(boardSize.copy(x = it)) },
                    range = 3..10,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NumberPicker(
                    label = stringResource(R.string.layers),
                    value = boardSize.z,
                    onValueChange = { viewModel.setBoardSize(boardSize.copy(z = it)) },
                    range = 1..10,
                    modifier = Modifier.weight(1f)
                )
                NumberPicker(
                    label = stringResource(R.string.to_win),
                    value = boardSize.winCondition,
                    onValueChange = { viewModel.setBoardSize(boardSize.copy(winCondition = it)) },
                    range = 3..maxOf(3, maxOf(boardSize.x, boardSize.y, boardSize.z)),
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider()

            SettingSelector(Modifier, stringResource(R.string.language), AppLanguage.entries, appLanguage) { viewModel.setLanguage(it as AppLanguage) }

            SettingSelector(Modifier, stringResource(R.string.theme), AppTheme.entries, themeDark) { viewModel.setTheme(it as AppTheme)}
            SettingSelector(Modifier, stringResource(R.string.orientation), Orientation.entries, orientation) { viewModel.setOrientation(it as Orientation) }

            SwitchSetting(
                checked = bgmEnabled,
                onCheckedChange = { viewModel.toggleBgm() },
                text = stringResource(R.string.bgm),
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider()

            SwitchSetting(
                checked = soundEnabled,
                onCheckedChange = { viewModel.toggleSound() },
                text = stringResource(R.string.sfx),
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider()

            SwitchSetting(
                checked = isImmersiveMode,
                onCheckedChange = { viewModel.toggleImmersiveMode() },
                text = stringResource(R.string.immersive),
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider()

            SwitchSetting(
                checked = dynamicColor,
                onCheckedChange = { viewModel.toggleDynamicColor() },
                text = stringResource(R.string.dynamic_color),
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider()

            SwitchSetting(
                checked = bgAnimationEnabled,
                onCheckedChange = { viewModel.toggleBgAnimation() },
                text = stringResource(R.string.bg_animation),
                modifier = Modifier.fillMaxWidth()
            )
            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))
            Text("Version v$version", style = MaterialTheme.typography.bodySmall)
            Text("© $year Tic Tac Toe Game", style = MaterialTheme.typography.bodySmall)
            Text(stringResource(R.string.developed_by), style = MaterialTheme.typography.bodySmall)
        }
    }
}
