package com.svapravrithi.app.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.svapravrithi.app.data.local.entity.FaqEntity
import com.svapravrithi.app.ui.components.SecondaryButton
import com.svapravrithi.app.ui.components.SvaCard

@Composable
fun HelpSupportScreen(onBack: () -> Unit, viewModel: HelpSupportViewModel = hiltViewModel()) {
    val faqs by viewModel.faqs.collectAsState()
    val context = LocalContext.current
    var showAddFaqDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SvaCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                    Column {
                        Text("Contact Us", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                        Text(SUPPORT_EMAIL, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                SecondaryButton(
                    text = "Email Support",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:$SUPPORT_EMAIL")
                            putExtra(Intent.EXTRA_SUBJECT, "Sva-Pravrithi Support")
                        }
                        context.startActivity(Intent.createChooser(intent, "Send email"))
                    },
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Frequently Asked Questions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                TextButton(onClick = { showAddFaqDialog = true }) { Text("+ Add") }
            }

            if (faqs.isEmpty()) {
                Text("No FAQs yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                faqs.forEach { faq ->
                    FaqCard(faq = faq, onDelete = { viewModel.deleteFaq(faq) })
                }
            }
        }
    }

    if (showAddFaqDialog) {
        var question by remember { mutableStateOf("") }
        var answer by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddFaqDialog = false },
            title = { Text("Add FAQ") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = question, onValueChange = { question = it }, label = { Text("Question") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = answer, onValueChange = { answer = it }, label = { Text("Answer") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (question.isNotBlank() && answer.isNotBlank()) viewModel.addFaq(question, answer)
                    showAddFaqDialog = false
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddFaqDialog = false }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun FaqCard(faq: FaqEntity, onDelete: () -> Unit) {
    SvaCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(faq.question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete FAQ", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
        Text(faq.answer, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
