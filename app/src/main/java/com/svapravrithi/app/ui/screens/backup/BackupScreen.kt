package com.svapravrithi.app.ui.screens.backup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.svapravrithi.app.ui.components.PrimaryButton
import com.svapravrithi.app.ui.components.SecondaryButton
import com.svapravrithi.app.ui.components.SvaCard
import com.svapravrithi.app.ui.theme.Satvik
import com.svapravrithi.app.ui.theme.Tamasik

@Composable
fun BackupScreen(onBack: () -> Unit, viewModel: BackupViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    val signInLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
            viewModel.onSignInResult(account)
        } catch (e: com.google.android.gms.common.api.ApiException) {
            // Most common cause here: the Google Cloud OAuth Client ID for this app's
            // package name + signing certificate hasn't been set up yet (see README's
            // "Enable Google Drive backup" section) - that shows up as a DEVELOPER_ERROR
            // (status code 10). Surface it instead of letting it crash the app.
            viewModel.onSignInError(e)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Backup & Restore") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "Back up all your expenses, plans, and declarations to your own Google Drive, or restore them on a new device.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (state.account == null) {
                SvaCard {
                    Text("Not signed in", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    Text(
                        "Sign in with Google to enable backup and restore.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                    PrimaryButton(text = "Sign in with Google", onClick = { signInLauncher.launch(viewModel.signInIntent()) })
                }
            } else {
                SvaCard {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Signed in as", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(state.account?.email ?: "", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                        }
                        TextButton(onClick = { viewModel.signOut() }) { Text("Sign out") }
                    }
                }

                SvaCard {
                    Text("Export", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    Text(
                        "Upload a fresh backup of everything on this device to Google Drive.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                    PrimaryButton(text = "Export Now", enabled = state.status != BackupStatus.WORKING, onClick = { viewModel.exportNow() })
                }

                SvaCard {
                    Text("Import", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    Text(
                        "Replace everything on this device with your latest Google Drive backup. This cannot be undone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                    SecondaryButton(text = "Import Latest Backup", enabled = state.status != BackupStatus.WORKING, onClick = { viewModel.importNow() })
                }
            }

            if (state.status == BackupStatus.WORKING) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
                    Text("Working...", style = MaterialTheme.typography.bodyMedium)
                }
            }
            state.message?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (state.status == BackupStatus.ERROR) Tamasik else Satvik,
                )
            }
        }
    }
}
