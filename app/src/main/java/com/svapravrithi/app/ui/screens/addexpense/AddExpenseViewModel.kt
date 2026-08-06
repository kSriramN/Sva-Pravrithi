package com.svapravrithi.app.ui.screens.addexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svapravrithi.app.data.local.entity.ExpenseEntity
import com.svapravrithi.app.data.repository.CategoryRepository
import com.svapravrithi.app.data.repository.DEFAULT_CATEGORIES
import com.svapravrithi.app.data.repository.ExpenseRepository
import com.svapravrithi.app.domain.model.DateUtil
import com.svapravrithi.app.domain.model.ExpenseType
import com.svapravrithi.app.domain.model.Guna
import com.svapravrithi.app.domain.model.PaymentMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddExpenseUiState(
    val amount: String = "",
    val category: String = "",
    val type: ExpenseType? = null,
    val guna: Guna? = null,
    val paymentMethod: PaymentMethod? = null,
    val comments: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val isSaving: Boolean = false,
    val error: String? = null,
) {
    val dateLabel: String get() = DateUtil.dayLabel(dateMillis)
    val isValid: Boolean get() = amount.toDoubleOrNull() != null && amount.toDoubleOrNull()!! > 0 && type != null
}

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    /** User-editable category list (Profile > Categories), seeded from the original defaults on first run. */
    val categories: StateFlow<List<String>> = categoryRepository.observeAll()
        .map { list -> list.map { it.name } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DEFAULT_CATEGORIES)

    init {
        viewModelScope.launch { categoryRepository.ensureSeeded() }
        // Auto-select the first available category once the list loads, if nothing chosen yet.
        categories.onEach { list ->
            if (_uiState.value.category.isBlank() && list.isNotEmpty()) {
                _uiState.value = _uiState.value.copy(category = list.first())
            }
        }.launchIn(viewModelScope)
    }

    fun onAmountChange(value: String) { _uiState.value = _uiState.value.copy(amount = value, error = null) }
    fun onCategoryChange(value: String) { _uiState.value = _uiState.value.copy(category = value) }
    fun onTypeChange(value: ExpenseType) { _uiState.value = _uiState.value.copy(type = value) }
    fun onGunaChange(value: Guna) { _uiState.value = _uiState.value.copy(guna = value) }
    fun onPaymentMethodChange(value: PaymentMethod) { _uiState.value = _uiState.value.copy(paymentMethod = value) }
    fun onCommentsChange(value: String) { _uiState.value = _uiState.value.copy(comments = value) }
    fun onDateChange(millis: Long) { _uiState.value = _uiState.value.copy(dateMillis = millis) }

    fun save(onDone: () -> Unit) {
        val state = _uiState.value
        if (!state.isValid) {
            _uiState.value = state.copy(error = "Enter an amount and select a Type")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true)
            val day = DateUtil.startOfDay(state.dateMillis)
            repository.save(
                ExpenseEntity(
                    amount = state.amount.toDouble(),
                    category = state.category,
                    type = state.type!!,
                    guna = state.guna,
                    paymentMethod = state.paymentMethod,
                    comments = state.comments,
                    date = day,
                    yearMonth = DateUtil.yearMonthOf(day),
                ),
            )
            _uiState.value = AddExpenseUiState()
            onDone()
        }
    }
}
