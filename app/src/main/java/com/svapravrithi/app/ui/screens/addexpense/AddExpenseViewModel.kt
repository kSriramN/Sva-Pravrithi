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
    /** 0 = new expense being created; non-zero = editing an existing one. */
    val id: Long = 0L,
    val amount: String = "",
    val category: String = "",
    val type: ExpenseType? = null,
    val guna: Guna? = null,
    val paymentMethod: PaymentMethod? = null,
    val comments: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isLoaded: Boolean = false,
    val error: String? = null,
) {
    val dateLabel: String get() = DateUtil.dayLabel(dateMillis)
    val isValid: Boolean get() = amount.toDoubleOrNull() != null && amount.toDoubleOrNull()!! > 0 && type != null
    val isEditing: Boolean get() = id != 0L
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
        // Guarded by isLoaded too, so this never clobbers a category loaded from an existing
        // expense being edited, even if the categories flow emits again after load() runs.
        categories.onEach { list ->
            if (!_uiState.value.isLoaded && _uiState.value.category.isBlank() && list.isNotEmpty()) {
                _uiState.value = _uiState.value.copy(category = list.first())
            }
        }.launchIn(viewModelScope)
    }

    /** Call with an existing expense's id to switch this screen into edit mode. Pass null (or don't call) for a new expense. */
    fun load(expenseId: Long?) {
        if (expenseId == null || expenseId == 0L) {
            _uiState.value = _uiState.value.copy(isLoaded = true)
            return
        }
        viewModelScope.launch {
            val existing = repository.getById(expenseId)
            if (existing != null) {
                _uiState.value = AddExpenseUiState(
                    id = existing.id,
                    amount = existing.amount.let { if (it == it.toLong().toDouble()) it.toLong().toString() else it.toString() },
                    category = existing.category,
                    type = existing.type,
                    guna = existing.guna,
                    paymentMethod = existing.paymentMethod,
                    comments = existing.comments,
                    dateMillis = existing.date,
                    isLoaded = true,
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoaded = true)
            }
        }
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
                    // Passing the existing id (Room's REPLACE conflict strategy) updates the
                    // row in place when editing; id=0 lets Room autogenerate a new one.
                    id = state.id,
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

    fun delete(onDone: () -> Unit) {
        val state = _uiState.value
        if (!state.isEditing) return
        viewModelScope.launch {
            _uiState.value = state.copy(isDeleting = true)
            repository.getById(state.id)?.let { repository.delete(it) }
            _uiState.value = AddExpenseUiState()
            onDone()
        }
    }
}
