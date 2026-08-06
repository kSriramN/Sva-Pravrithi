package com.svapravrithi.app.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.svapravrithi.app.domain.model.Guna
import com.svapravrithi.app.ui.components.SvaCard

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Sva-Pravrithi") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.SelfImprovement, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                }
            }
            Text(
                "Sva-Pravrithi",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                "Spend with Awareness. Live with Balance.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            SvaCard {
                Text("What is Sva-Pravrithi?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                Text(
                    "Sva-Pravrithi (Sanskrit: \u0938\u094d\u0935-\u092a\u094d\u0930\u0935\u0943\u0924\u094d\u0924\u093f, roughly \"one's own nature/tendency\") is a mindful expense tracker that goes beyond simply logging what you spend. It looks at the quality of your spending against your own declared intentions, drawing on the classical framework of the Three Gunas \u2014 Sattva, Rajas, and Tamas \u2014 to reflect back how balanced your month has been.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            SvaCard {
                Text("How it works", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                Text(
                    "Each month, you declare a Savings Goal and budgets for your Needs, Wants, and Pleasures. Through the month you log expenses under those three categories and update your actual savings as you set money aside. At month's end, the Reflection Engine compares what you declared against what actually happened \u2014 not any single purchase \u2014 and shows you a Reflection Score and a dominant Guna for the month.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            SvaCard {
                Text("The Three Gunas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                GunaRow(Guna.SATVIK, "Clarity, growth, harmony \u2014 goals met, spending in balance.")
                GunaRow(Guna.RAJASIK, "Activity, desire, ambition \u2014 spending driven by wants.")
                GunaRow(Guna.TAMASIK, "Inertia, confusion, excess \u2014 goals missed, indulgence unchecked.")
            }

            SvaCard {
                Text("Your data", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                Text(
                    "Everything you enter stays on your device by default. You can optionally back up to your own Google Drive from Profile > Backup & Restore \u2014 nothing is ever sent anywhere else.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Text(
                "Version 1.0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun GunaRow(guna: Guna, description: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(guna.color),
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(10.dp))
        Column {
            Text(guna.label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
