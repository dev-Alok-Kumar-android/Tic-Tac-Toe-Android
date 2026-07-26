package com.tuto.alokkumar.tictactoe.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuto.alokkumar.tictactoe.R
import com.tuto.alokkumar.tictactoe.data.AiDifficulty
import com.tuto.alokkumar.tictactoe.data.AppLanguage
import com.tuto.alokkumar.tictactoe.data.AppTheme
import com.tuto.alokkumar.tictactoe.data.BoardStyle
import com.tuto.alokkumar.tictactoe.data.FirstMoveBehavior
import com.tuto.alokkumar.tictactoe.data.NextMoveBehavior
import com.tuto.alokkumar.tictactoe.data.Orientation
import com.tuto.alokkumar.tictactoe.ui.components.MyIcons
import com.tuto.alokkumar.tictactoe.ui.components.NumberPicker
import com.tuto.alokkumar.tictactoe.ui.components.SettingSelector
import com.tuto.alokkumar.tictactoe.ui.components.SwitchSetting
import com.tuto.alokkumar.tictactoe.viewModel.SettingsViewModel
import java.util.Calendar

/**
 * Categorized Settings configuration screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val aiDifficulty by viewModel.selectedAiDifficulty.collectAsStateWithLifecycle()
    val isImmersiveMode by viewModel.immersiveMode.collectAsStateWithLifecycle()
    val bgmEnabled by viewModel.bgmEnabled.collectAsStateWithLifecycle()
    val themeDark by viewModel.theme.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()
    val hapticEnabled by viewModel.hapticEnabled.collectAsStateWithLifecycle()
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
    
    val p1Symbol by viewModel.p1Symbol.collectAsStateWithLifecycle()
    val p2Symbol by viewModel.p2Symbol.collectAsStateWithLifecycle()
    val p1Color by viewModel.p1Color.collectAsStateWithLifecycle()
    val p2Color by viewModel.p2Color.collectAsStateWithLifecycle()
    val p1Name by viewModel.p1Name.collectAsStateWithLifecycle()
    val p2Name by viewModel.p2Name.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val version = remember(context) {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "Unknown"
        } catch (_: Exception) { "1.0.0" }
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 1. GAMEPLAY RULES ---
            SettingsCategoryHeader(stringResource(R.string.category_gameplay))
            
            SettingSelector(
                title = stringResource(R.string.first_move),
                dataList = FirstMoveBehavior.entries,
                selected = firstMoveBehavior,
                labelMapper = { (it as FirstMoveBehavior).name },
                onItemSelect = { viewModel.setFirstMoveBehavior(it as FirstMoveBehavior) }
            )

            SettingSelector(
                title = stringResource(R.string.next_game_start),
                dataList = NextMoveBehavior.entries,
                selected = nextMoveBehavior,
                labelMapper = { (it as NextMoveBehavior).name },
                onItemSelect = { viewModel.setNextMoveBehavior(it as NextMoveBehavior) }
            )

            // --- PLAYER CUSTOMIZATION ---
            SettingsCategoryHeader(stringResource(R.string.category_player_customization))
            
            PlayerCustomizationBlock(
                title = stringResource(R.string.player_x),
                name = p1Name,
                symbol = p1Symbol,
                color = p1Color,
                onNameChange = { viewModel.setP1Name(it) },
                onSymbolChange = { if (it != p2Symbol) viewModel.setP1Symbol(it) },
                onColorChange = { viewModel.setP1Color(it) }
            )

            PlayerCustomizationBlock(
                title = stringResource(R.string.player_o),
                name = p2Name,
                symbol = p2Symbol,
                color = p2Color,
                onNameChange = { viewModel.setP2Name(it) },
                onSymbolChange = { if (it != p1Symbol) viewModel.setP2Symbol(it) },
                onColorChange = { viewModel.setP2Color(it) }
            )

            if (p1Symbol == p2Symbol) {
                Text(
                    text = stringResource(R.string.symbol_conflict_warning),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            // --- 2. AI CONFIGURATION ---
            SettingsCategoryHeader(stringResource(R.string.category_ai))
            
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SettingSelector(
                    title = stringResource(R.string.difficulty),
                    dataList = AiDifficulty.entries,
                    selected = aiDifficulty,
                    labelMapper = { data ->
                        val res = when(data as AiDifficulty) {
                            AiDifficulty.EASY -> R.string.mode_easy
                            AiDifficulty.MEDIUM -> R.string.mode_medium
                            AiDifficulty.HARD -> R.string.mode_hard
                            AiDifficulty.IMPOSSIBLE -> R.string.mode_impossible
                        }
                        stringResource(res)
                    },
                    onItemSelect = { viewModel.setAiDifficulty(it as AiDifficulty) }
                )

                Text(stringResource(R.string.ai_skill_level, aiStrength), style = MaterialTheme.typography.labelLarge)
                Slider(
                    value = aiStrength.toFloat(),
                    onValueChange = { viewModel.setAiStrength(it.toInt()) },
                    valueRange = 1f..100f
                )

                SwitchSetting(
                    checked = isAdvancedAiEnabled,
                    onCheckedChange = { viewModel.toggleAdvancedAi() },
                    text = stringResource(R.string.advanced_ai)
                )

                AnimatedVisibility(visible = isAdvancedAiEnabled) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(R.string.manual_depth, manualMaxDepth), style = MaterialTheme.typography.bodySmall)
                        Slider(
                            value = manualMaxDepth.toFloat(),
                            onValueChange = { viewModel.setManualMaxDepth(it.toInt()) },
                            valueRange = 2f..12f,
                            steps = 9
                        )
                        Text(
                            stringResource(R.string.depth_warning),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // --- 3. BOARD SETUP ---
            SettingsCategoryHeader(stringResource(R.string.category_board))
            
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
            }

            // --- 4. AUDIO & FEEDBACK ---
            SettingsCategoryHeader(stringResource(R.string.category_audio_feedback))
            
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SwitchSetting(checked = bgmEnabled, onCheckedChange = { viewModel.toggleBgm() }, text = stringResource(R.string.bgm))
                SwitchSetting(checked = soundEnabled, onCheckedChange = { viewModel.toggleSound() }, text = stringResource(R.string.sfx))
                SwitchSetting(checked = hapticEnabled, onCheckedChange = { viewModel.toggleHaptic() }, text = stringResource(R.string.haptics))
            }

            // --- 5. APPEARANCE & SYSTEM ---
            SettingsCategoryHeader(stringResource(R.string.category_appearance))
            
            SettingSelector(
                title = stringResource(R.string.style),
                dataList = BoardStyle.entries,
                selected = boardStyle,
                labelMapper = { (it as BoardStyle).name },
                onItemSelect = { viewModel.setBoardStyle(it as BoardStyle) }
            )

            SettingSelector(
                title = stringResource(R.string.theme),
                dataList = AppTheme.entries,
                selected = themeDark,
                labelMapper = { (it as AppTheme).name },
                onItemSelect = { viewModel.setTheme(it as AppTheme) }
            )

            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SwitchSetting(checked = dynamicColor, onCheckedChange = { viewModel.toggleDynamicColor() }, text = stringResource(R.string.dynamic_color))
                SwitchSetting(checked = bgAnimationEnabled, onCheckedChange = { viewModel.toggleBgAnimation() }, text = stringResource(R.string.bg_animation))
                SwitchSetting(checked = isImmersiveMode, onCheckedChange = { viewModel.toggleImmersiveMode() }, text = stringResource(R.string.immersive))
            }

            SettingsCategoryHeader(stringResource(R.string.category_system))
            
            SettingSelector(
                title = stringResource(R.string.language),
                dataList = AppLanguage.entries,
                selected = appLanguage,
                labelMapper = {
                    when(it as AppLanguage) {
                        AppLanguage.ENGLISH -> stringResource(R.string.language_english)
                        AppLanguage.HINDI -> stringResource(R.string.language_hindi)
                    }
                },
                onItemSelect = { viewModel.setLanguage(it as AppLanguage) }
            )

            SettingSelector(
                title = stringResource(R.string.orientation),
                dataList = Orientation.entries,
                selected = orientation,
                labelMapper = { (it as Orientation).name },
                onItemSelect = { viewModel.setOrientation(it as Orientation) }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Version v$version", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                Text("© $year Tic Tac Toe Game", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                Text(stringResource(R.string.developed_by), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerCustomizationBlock(
    title: String,
    name: String,
    symbol: String,
    color: Long,
    onNameChange: (String) -> Unit,
    onSymbolChange: (String) -> Unit,
    onColorChange: (Long) -> Unit
) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(color))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text(stringResource(R.string.player_name)) },
                    modifier = Modifier.weight(1.5f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = symbol,
                    onValueChange = { 
                        if (it.isEmpty() || isSingleVisualCharacter(it)) {
                            onSymbolChange(it)
                        }
                    }, 
                    label = { Text(stringResource(R.string.player_symbol)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Text(stringResource(R.string.player_color), style = MaterialTheme.typography.labelSmall)
            ColorRow(selectedColor = color, onColorSelect = onColorChange)
        }
    }
}

@Composable
private fun ColorRow(selectedColor: Long, onColorSelect: (Long) -> Unit) {
    val colors = listOf(
        0xFFE91E63, 0xFF2196F3, 0xFF4CAF50, 0xFFFFEB3B, 0xFFFF9800,
        0xFF9C27B0, 0xFF00BCD4, 0xFF795548, 0xFF607D8B, 0xFFFFFFFF
    )
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(color))
                    .border(
                        width = if (selectedColor == color) 3.dp else 1.dp,
                        color = if (selectedColor == color) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable { onColorSelect(color) }
            )
        }
    }
}

private fun isSingleVisualCharacter(s: String): Boolean {
    if (s.isEmpty()) return false
    val it = java.text.BreakIterator.getCharacterInstance()
    it.setText(s)
    it.first()
    val next = it.next()
    return next == s.length
}

@Composable
private fun SettingsCategoryHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}
