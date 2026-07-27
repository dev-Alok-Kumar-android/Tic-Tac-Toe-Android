package com.tuto.alokkumar.tictactoe.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuto.alokkumar.tictactoe.R
import com.tuto.alokkumar.tictactoe.domain.model.MatchProbabilities

@Composable
fun ProbabilityAnalysisCard(
    probabilities: MatchProbabilities?,
    p1Name: String,
    p2Name: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.match_analysis),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                if (probabilities == null) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    val statusText = when {
                        kotlin.math.abs(probabilities.p1WinChance - probabilities.p2WinChance) < 0.05f -> stringResource(R.string.fair_match)
                        probabilities.p1WinChance > probabilities.p2WinChance -> stringResource(R.string.p1_advantage)
                        else -> stringResource(R.string.p2_advantage)
                    }
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (probabilities != null) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProbabilityBar(
                        label = p1Name,
                        chance = probabilities.p1WinChance,
                        color = Color(0xFFE91E63)
                    )
                    ProbabilityBar(
                        label = p2Name,
                        chance = probabilities.p2WinChance,
                        color = Color(0xFF2196F3)
                    )
                    ProbabilityBar(
                        label = stringResource(R.string.draw_message),
                        chance = probabilities.drawChance,
                        color = Color.Gray
                    )
                }
                
                Text(
                    text = stringResource(R.string.analysis_sample_size, probabilities.sampleSize),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun ProbabilityBar(
    label: String,
    chance: Float,
    color: Color
) {
    val animatedProgress by animateFloatAsState(targetValue = chance, label = "progress")
    
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text("${(chance * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.1f),
        )
    }
}
