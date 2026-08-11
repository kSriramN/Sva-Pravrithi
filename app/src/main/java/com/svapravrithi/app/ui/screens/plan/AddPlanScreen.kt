package com.svapravrithi.app.ui.screens.plan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.svapravrithi.app.domain.model.DateUtil
import com.svapravrithi.app.domain.model.ExpenseType
import com.svapravrithi.app.domain.model.Guna
import com.svapravrithi.app.domain.model.PlanPriority
import com.svapravrithi.app.ui.components.CurrencyVisualTransformation
import com.svapravrithi.app.ui.components.FormFieldRow
import com.svapravrithi.app.ui.components.PrimaryButton
import com.svapravrithi.app.ui.theme.LocalCurrency

private enum class ActivePlanSheet { NONE, TYPE, GUNA, PRIORITY }

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AddPlanScreen(
    planId: Long?,
    onSaved: () -> Unit,
    onDeleted: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddPlanViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val currency = LocalCurrency.current
    var activeSheet by remember { mutableStateOf(ActivePlanSheet.NONE) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val amountFocusRequester = remember { FocusRequester() }
    LaunchedEffect(planId) { viewModel.load(planId) }
    LaunchedEffect(state.isLoaded) {
        if (state.isLoaded) amountFocusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Edit Plan" else "Add Plan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.isEditing) {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete plan")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            FormFieldRow(label = "Title") {
                androidx.compose.foundation.text.BasicTextField(
                    value = state.title,
                    onValueChange = viewModel::onTitleChange,
                    textStyle = TextStyle(textAlign = TextAlign.End, fontSize = MaterialTheme.typography.bodyLarge.fontSize, color = MaterialTheme.colorScheme.onSurface),
                    singleLine = true,
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.widthIn(min = 60.dp),
                )
            }

            FormFieldRow(label = "Amount", contentArrangement = Arrangement.Start) {
                Text(currency.symbol, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
                androidx.compose.foundation.text.BasicTextField(
                    value = state.estimatedAmount,
                    onValueChange = viewModel::onAmountChange,
                    textStyle = TextStyle(textAlign = TextAlign.Start, fontSize = MaterialTheme.typography.bodyLarge.fontSize, color = MaterialTheme.colorScheme.onSurface),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = CurrencyVisualTransformation(currency),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.widthIn(min = 60.dp).focusRequester(amountFocusRequester),
                )
            }

            FormFieldRow(label = "Due Date", onClick = { showDatePicker = true }) {
                Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(6.dp))
                Text(state.dueDateLabel, style = MaterialTheme.typography.bodyLarge)
            }

            FormFieldRow(label = "Type", onClick = { activeSheet = ActivePlanSheet.TYPE }) {
                Text(state.type?.label ?: "Select", style = MaterialTheme.typography.bodyLarge, color = if (state.type == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            FormFieldRow(label = "Guna", onClick = { activeSheet = ActivePlanSheet.GUNA }) {
                Text(state.guna?.label ?: "Select", style = MaterialTheme.typography.bodyLarge, color = if (state.guna == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            FormFieldRow(label = "Priority", onClick = { activeSheet = ActivePlanSheet.PRIORITY }) {
                Text(state.priority.label, style = MaterialTheme.typography.bodyLarge)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            FormFieldRow(label = "Notes") {
                androidx.compose.foundation.text.BasicTextField(
                    value = state.notes,
                    onValueChange = viewModel::onNotesChange,
                    textStyle = TextStyle(textAlign = TextAlign.End, fontSize = MaterialTheme.typography.bodyLarge.fontSize, color = MaterialTheme.colorScheme.onSurface),
                    singleLine = true,
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.widthIn(min = 60.dp),
                )
            }

            state.error?.let {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))
            PrimaryButton(
                text = if (state.isEditing) "Update Plan" else "Save Plan",
                enabled = !state.isSaving && !state.isDeleting,
                onClick = { viewModel.save(onSaved) },
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))
        }
    }

    if (activeSheet != ActivePlanSheet.NONE) {
        ModalBottomSheet(onDismissRequest = { activeSheet = ActivePlanSheet.NONE }) {
            when (activeSheet) {
                ActivePlanSheet.TYPE -> {
                    SheetHeader("Type")
                    ExpenseType.entries.forEach { type ->
                        SheetRow(
                            label = type.label,
                            selected = state.type == type,
                            onClick = { viewModel.onTypeChange(type); activeSheet = ActivePlanSheet.NONE },
                        )
                    }
                }
                ActivePlanSheet.GUNA -> {
                    SheetHeader("Guna")
                    Guna.entries.forEach { guna ->
                        SheetRow(
                            label = guna.label,
                            selected = state.guna == guna,
                            onClick = { viewModel.onGunaChange(guna); activeSheet = ActivePlanSheet.NONE },
                            accentColor = guna.color,
                        )
                    }
                }
                ActivePlanSheet.PRIORITY -> {
                    SheetHeader("Priority")
                    PlanPriority.entries.forEach { p ->
                        SheetRow(
                            label = p.label,
                            selected = state.priority == p,
                            onClick = { viewModel.onPriorityChange(p); activeSheet = ActivePlanSheet.NONE },
                        )
                    }
                }
                ActivePlanSheet.NONE -> {}
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(20.dp))
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.dueDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.onDueDateChange(DateUtil.fromDatePickerMillis(it)) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete this plan?") },
            text = { Text("This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.delete(onDeleted)
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun SheetHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
    )
}

@Composable
private fun SheetRow(label: String, selected: Boolean, onClick: () -> Unit, accentColor: androidx.compose.ui.graphics.Color? = null) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (accentColor != null) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier.size(12.dp).background(accentColor, androidx.compose.foundation.shape.CircleShape),
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(10.dp))
                }
                Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
            }
            if (selected) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            }
        }
        HorizontalDivider()
    }
}
