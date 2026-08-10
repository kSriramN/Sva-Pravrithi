package com.svapravrithi.app.ui.screens.addexpense

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.svapravrithi.app.domain.model.DateUtil
import com.svapravrithi.app.domain.model.PaymentMethod
import com.svapravrithi.app.ui.components.CurrencyVisualTransformation
import com.svapravrithi.app.ui.components.FormFieldRow
import com.svapravrithi.app.ui.components.GunaSelector
import com.svapravrithi.app.ui.components.PrimaryButton
import com.svapravrithi.app.ui.components.SvaCard
import com.svapravrithi.app.ui.components.TypeSelector
import com.svapravrithi.app.ui.theme.LocalCurrency

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
    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var showPaymentMethodSheet by remember { mutableStateOf(false) }
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
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            SvaCard {
                Column {
                    // Date - restyled as a flat list row matching the reference layout
                    FormFieldRow(label = "Date", onClick = { showDatePicker = true }) {
                        Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(6.dp))
                        Text(state.dateLabel, style = MaterialTheme.typography.bodyLarge)
                    }

                    // Payment Method / Account - opens a bottom sheet picker (Cash / Card / Account)
                    FormFieldRow(label = "Account", onClick = { showPaymentMethodSheet = true }) {
                        Text(
                            state.paymentMethod?.label ?: "Select",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (state.paymentMethod == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(6.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    // Category - same dropdown menu as before, triggered from a flat row instead of a boxed field
                    androidx.compose.foundation.layout.Box {
                        FormFieldRow(label = "Category", onClick = { categoryMenuExpanded = true }) {
                            Text(state.category.ifBlank { "Select" }, style = MaterialTheme.typography.bodyLarge)
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        DropdownMenu(expanded = categoryMenuExpanded, onDismissRequest = { categoryMenuExpanded = false }) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = { viewModel.onCategoryChange(category); categoryMenuExpanded = false },
                                )
                            }
                        }
                    }

                    // Amount - flat, underlined, right-aligned, live comma formatting, auto-focused
                    FormFieldRow(label = "Amount") {
                        Text(currency.symbol, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        TextField(
                            value = state.amount,
                            onValueChange = viewModel::onAmountChange,
                            textStyle = TextStyle(textAlign = TextAlign.End, fontSize = MaterialTheme.typography.bodyLarge.fontSize),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            visualTransformation = CurrencyVisualTransformation(currency),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            modifier = Modifier.weight(1f).focusRequester(amountFocusRequester),
                        )
                    }

                    // Note - flat, matching Money Manager's "Note" field
                    FormFieldRow(label = "Note") {
                        TextField(
                            value = state.comments,
                            onValueChange = viewModel::onCommentsChange,
                            textStyle = TextStyle(textAlign = TextAlign.End, fontSize = MaterialTheme.typography.bodyLarge.fontSize),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Type", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                TypeSelector(selected = state.type, onSelect = viewModel::onTypeChange)
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Guna (Optional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Text(
                    "For your own reflection \u2014 doesn't affect your monthly Guna score",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                GunaSelector(selected = state.guna, onSelect = viewModel::onGunaChange)
            }

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            PrimaryButton(
                text = if (state.isEditing) "Update Expense" else "Save Expense",
                enabled = !state.isSaving && !state.isDeleting,
                onClick = { viewModel.save(onSaved) },
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
        }
    }

    if (showPaymentMethodSheet) {
        ModalBottomSheet(onDismissRequest = { showPaymentMethodSheet = false }) {
            Text(
                "Account",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            )
            PaymentMethod.entries.forEach { method ->
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.onPaymentMethodChange(method)
                                showPaymentMethodSheet = false
                            }
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(method.label, style = MaterialTheme.typography.bodyLarge)
                        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    HorizontalDivider()
                }
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
