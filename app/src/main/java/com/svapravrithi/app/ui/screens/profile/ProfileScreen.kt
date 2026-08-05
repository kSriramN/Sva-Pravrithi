package com.svapravrithi.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.svapravrithi.app.ui.components.SvaCard

private data class SettingsRow(val icon: ImageVector, val label: String, val action: ProfileAction = ProfileAction.NONE)

private enum class ProfileAction { NONE, UPDATE_SAVINGS, BACKUP_RESTORE }

private val settingsRows = listOf(
    SettingsRow(Icons.Filled.CalendarViewMonth, "Monthly Declarations"),
    SettingsRow(Icons.Filled.Savings, "Update Savings", ProfileAction.UPDATE_SAVINGS),
    SettingsRow(Icons.Filled.Category, "Categories"),
    SettingsRow(Icons.Filled.Settings, "Scoring Settings"),
    SettingsRow(Icons.Filled.Backup, "Backup & Restore", ProfileAction.BACKUP_RESTORE),
    SettingsRow(Icons.AutoMirrored.Filled.HelpOutline, "Help & Support"),
    SettingsRow(Icons.Filled.Info, "About Sva-Pravrithi"),
)

@Composable
fun ProfileScreen(onBack: () -> Unit, onUpdateSavings: () -> Unit, onBackupRestore: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                Column {
                    Text("Your Profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text(
                        "All data is stored locally on this device",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            SvaCard {
                settingsRows.forEachIndexed { index, row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                when (row.action) {
                                    ProfileAction.UPDATE_SAVINGS -> onUpdateSavings()
                                    ProfileAction.BACKUP_RESTORE -> onBackupRestore()
                                    ProfileAction.NONE -> {}
                                }
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(row.icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                        Text(row.label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (index != settingsRows.lastIndex) {
                        androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    }
                }
            }

            Text(
                "Sva-Pravrithi v1.0 \u2014 Spend with Awareness. Live with Balance.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}
