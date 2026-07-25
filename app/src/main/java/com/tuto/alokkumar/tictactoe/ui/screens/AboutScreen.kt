package com.tuto.alokkumar.tictactoe.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.tuto.alokkumar.tictactoe.R
import com.tuto.alokkumar.tictactoe.ui.components.MyIcons

/**
 * About screen listing game instructions, system specifications, contact forms,
 * developer references, and links to external social media/portfolios.
 *
 * @param onBack Callback event triggered when clicking the top bar back navigation arrow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit = {},
) {
    val context = LocalContext.current
    val scroll = rememberScrollState()
    val year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
    val version = LocalContext.current.packageManager.getPackageInfo(
        LocalContext.current.packageName,
        0
    ).versionName ?: "Unknown"


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.about)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = MyIcons.ArrowBack,
                            contentDescription = stringResource(R.string.cancel)
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
                stringResource(R.string.app_name) + " Game",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))
            Text(
                stringResource(R.string.about_description),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(Modifier.height(12.dp))

            // 🧩 How to Play
            Text(
                stringResource(R.string.how_to_play),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.how_to_play_details),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(Modifier.height(12.dp))

            // 📩 Contact
            Text(
                stringResource(R.string.contact_me),
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
                Text(stringResource(R.string.send_email))
            }

            TextButton(
                onClick = {
                    val githubIntent = Intent(
                        Intent.ACTION_VIEW,
                        "https://github.com/AppsByAlok".toUri()
                    )
                    context.startActivity(githubIntent)
                }
            ) {
                Text(stringResource(R.string.view_github))
            }

            TextButton(
                onClick = {
                    val websiteIntent = Intent(
                        Intent.ACTION_VIEW,
                        "https://appsbyalok.netlify.app/".toUri()
                    )
                    context.startActivity(websiteIntent)
                }
            ) {
                Text(stringResource(R.string.view_portfolio))
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(Modifier.height(12.dp))

            // 📱 More
            Text(
                stringResource(R.string.more),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.more_details),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(36.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(Modifier.height(100.dp))

            // 🧾 Footer
            Text("Version v$version", style = MaterialTheme.typography.bodySmall)
            Text("© $year Tic Tac Toe Game", style = MaterialTheme.typography.bodySmall)
            Text("Licensed under MIT", style = MaterialTheme.typography.bodySmall)
            Text(stringResource(R.string.developed_by), style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))
        }
    }
}
