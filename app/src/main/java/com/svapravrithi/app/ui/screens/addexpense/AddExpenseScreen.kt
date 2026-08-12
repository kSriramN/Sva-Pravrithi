package com.svapravrithi.app.ui.screens.addexpense

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
import com.svapravrithi.app.domain.model.PaymentMethod
import com.svapravrithi.app.ui.components.CurrencyVisualTransformation
import com.svapravrithi.app.ui.components.FormFieldRow
import com.svapravrithi.app.ui.theme.LocalCurrency

private enum class ActiveSheet { NONE, CATEGORY, ACCOUNT, TYPE, GUNA }

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    expenseId: Long?,
    onSaved: () -> Unit,
    onDeleted: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddExpenseViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val currency = LocalCurrency.current
    var activeSheet by remember { mutableStateOf(ActiveSheet.NONE) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val amountFocusRequester = remember { FocusRequester() }

    LaunchedEffect(expenseId) { viewModel.load(expenseId) }
    LaunchedEffect(state.isLoaded) {
        if (state.isLoaded) amountFocusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Edit Expense" else "Add Expense") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.isEditing) {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete expense")
                        }
                    }
                    androidx.compose.material3.TextButton(
                        onClick = { viewModel.save(onSaved) },
                        enabled = !state.isSaving && !state.isDeleting,
                    ) {
                        Text("Save", fontWeight = FontWeight.SemiBold)
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
            FormFieldRow(label = "Date", onClick = { showDatePicker = true }) {
                Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(6.dp))
                Text(state.dateLabel, style = MaterialTheme.typography.bodyLarge)
            }

            FormFieldRow(label = "Account", onClick = { activeSheet = ActiveSheet.ACCOUNT }) {
                Text(
                    state.paymentMethod?.label ?: "Select",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (state.paymentMethod == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                )
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            FormFieldRow(label = "Category", onClick = { activeSheet = ActiveSheet.CATEGORY }) {
                Text(state.category.ifBlank { "Select" }, style = MaterialTheme.typography.bodyLarge)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            FormFieldRow(label = "Amount", contentArrangement = Arrangement.Start) {
                Text(currency.symbol, style = MaterialTheme.typography.bodyLarge, color = com.svapravrithi.app.ui.theme.Tamasik)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
                androidx.compose.foundation.text.BasicTextField(
                    value = state.amount,
                    onValueChange = viewModel::onAmountChange,
                    textStyle = TextStyle(textAlign = TextAlign.Start, fontSize = MaterialTheme.typography.bodyLarge.fontSize, color = com.svapravrithi.app.ui.theme.Tamasik, fontWeight = FontWeight.Medium),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = CurrencyVisualTransformation(currency),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(com.svapravrithi.app.ui.theme.Tamasik),
                    modifier = Modifier.widthIn(min = 60.dp).focusRequester(amountFocusRequester),
                )
            }

            FormFieldRow(label = "Note") {
                androidx.compose.foundation.text.BasicTextField(
                    value = state.comments,
                    onValueChange = viewModel::onCommentsChange,
                    textStyle = TextStyle(textAlign = TextAlign.End, fontSize = MaterialTheme.typography.bodyLarge.fontSize, color = MaterialTheme.colorScheme.onSurface),
                    singleLine = true,
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.widthIn(min = 60.dp),
                )
            }

            FormFieldRow(label = "Type", onClick = { activeSheet = ActiveSheet.TYPE }) {
                Text(state.type?.label ?: "Select", style = MaterialTheme.typography.bodyLarge, color = if (state.type == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            FormFieldRow(label = "Guna", onClick = { activeSheet = ActiveSheet.GUNA }) {
                Text(state.guna?.label ?: "Optional", style = MaterialTheme.typography.bodyLarge, color = if (state.guna == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            state.error?.let {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(16.dp))
        }
    }

    if (activeSheet != ActiveSheet.NONE) {
        ModalBottomSheet(onDismissRequest = { activeSheet = ActiveSheet.NONE }) {
            when (activeSheet) {
                ActiveSheet.ACCOUNT -> {
                    SheetHeader("Account")
                    PaymentMethod.entries.forEach { method ->
                        SheetRow(
                            label = method.label,
                            selected = state.paymentMethod == method,
                            onClick = { viewModel.onPaymentMethodChange(method); activeSheet = ActiveSheet.NONE },
                        )
                    }
                }
                ActiveSheet.CATEGORY -> {
                    SheetHeader("Category")
                    categories.forEach { category ->
                        SheetRow(
                            label = category,
                            selected = state.category == category,
                            onClick = { viewModel.onCategoryChange(category); activeSheet = ActiveSheet.NONE },
                        )
                    }
                }
                ActiveSheet.TYPE -> {
                    SheetHeader("Type")
                    ExpenseType.entries.forEach { type ->
                        SheetRow(
                            label = type.label,
                            selected = state.type == type,
                            onClick = { viewModel.onTypeChange(type); activeSheet = ActiveSheet.NONE },
                        )
                    }
                }
                ActiveSheet.GUNA -> {
                    SheetHeader("Guna (optional \u2014 for your own reflection, doesn't affect your score)")
                    Guna.entries.forEach { guna ->
                        SheetRow(
                            label = guna.label,
                            selected = state.guna == guna,
                            onClick = { viewModel.onGunaChange(guna); activeSheet = ActiveSheet.NONE },
                            accentColor = guna.color,
                        )
                    }
                }
                ActiveSheet.NONE -> {}
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(20.dp))
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.onDateChange(DateUtil.fromDatePickerMillis(it)) }
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
            title = { Text("Delete this expense?") },
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
