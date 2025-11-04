package com.tuto.alokkumar.tictactoe.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    version: String = "1.0.0",
    onBack: () -> Unit = {},
) {
    val context = LocalContext.current
    val scroll = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("About") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scroll),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Tic Tac Toe Game",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))
            Text(
                "Play Tic Tac Toe with friends or against AI of different difficulty levels. "
                        + "Challenge yourself and test your strategy!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(Modifier.height(12.dp))

            // 🧩 How to Play
            Text(
                "How to Play",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "1. Choose your mode: Player vs Player or Player vs AI.\n" +
                        "2. Take turns placing Xs and Os on the grid.\n" +
                        "3. The first player to align 3 symbols in a row, column, or diagonal wins.\n" +
                        "4. If the board fills without a winner, it’s a draw!",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(Modifier.height(12.dp))

            // 📩 Contact
            Text(
                "Contact Me",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:alokkumar01242@gmail.com".toUri()
                        putExtra(Intent.EXTRA_SUBJECT, "Tic Tac Toe Feedback")
                    }
                    context.startActivity(intent)
                }
            ) {
                Text("📧 Send Email")
            }

            TextButton(
                onClick = {
                    val githubIntent = Intent(Intent.ACTION_VIEW,
                        "https://github.com/dev-Alok-Kumar-android".toUri())
                    context.startActivity(githubIntent)
                }
            ) {
                Text("🌐 View My GitHub")
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(Modifier.height(12.dp))

            // 📱 More
            Text(
                "More",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "More exciting games and apps are coming soon! Stay tuned.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(36.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(Modifier.height(100.dp))

            // 🧾 Footer
            Text("Version $version", style = MaterialTheme.typography.bodySmall)
            Text("© 2025 Tic Tac Toe Game", style = MaterialTheme.typography.bodySmall)
            Text("Developed by Alok Kumar", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))
        }
    }
}
