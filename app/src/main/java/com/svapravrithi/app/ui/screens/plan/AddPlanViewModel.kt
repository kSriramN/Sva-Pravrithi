package com.svapravrithi.app.ui.screens.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svapravrithi.app.data.local.entity.PlanEntity
import com.svapravrithi.app.data.repository.PlanRepository
import com.svapravrithi.app.domain.model.DateUtil
import com.svapravrithi.app.domain.model.ExpenseType
import com.svapravrithi.app.domain.model.Guna
import com.svapravrithi.app.domain.model.PlanPriority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddPlanUiState(
    val title: String = "",
    val estimatedAmount: String = "",
    val dueDateMillis: Long = System.currentTimeMillis(),
    val type: ExpenseType? = null,
    val guna: Guna? = null,
    val priority: PlanPriority = PlanPriority.MEDIUM,
    val notes: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
) {
    val dueDateLabel: String get() = DateUtil.dayLabel(dueDateMillis)
    val isValid: Boolean get() = title.isNotBlank() && estimatedAmount.toDoubleOrNull() != null && estimatedAmount.toDoubleOrNull()!! > 0
}

@HiltViewModel
class AddPlanViewModel @Inject constructor(
    private val repository: PlanRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPlanUiState())
    val uiState: StateFlow<AddPlanUiState> = _uiState.asStateFlow()

    private var editingId: Long? = null

    fun load(planId: Long?) {
        editingId = planId
        if (planId == null) return
        viewModelScope.launch {
            // For simplicity plans are loaded via the upcoming/completed flows in PlanListViewModel;
            // this screen re-fetches directly if opened via deep link/edit.
        }
    }

    fun onTitleChange(v: String) { _uiState.value = _uiState.value.copy(title = v, error = null) }
    fun onAmountChange(v: String) { _uiState.value = _uiState.value.copy(estimatedAmount = v, error = null) }
    fun onDueDateChange(v: Long) { _uiState.value = _uiState.value.copy(dueDateMillis = v) }
    fun onTypeChange(v: ExpenseType) { _uiState.value = _uiState.value.copy(type = v) }
    fun onGunaChange(v: Guna) { _uiState.value = _uiState.value.copy(guna = v) }
    fun onPriorityChange(v: PlanPriority) { _uiState.value = _uiState.value.copy(priority = v) }
    fun onNotesChange(v: String) { _uiState.value = _uiState.value.copy(notes = v) }

    fun save(onDone: () -> Unit) {
        val state = _uiState.value
        if (!state.isValid) {
            _uiState.value = state.copy(error = "Enter a title and a valid amount")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true)
            repository.save(
                PlanEntity(
                    id = editingId ?: 0,
                    title = state.title,
                    estimatedAmount = state.estimatedAmount.toDouble(),
                    dueDate = state.dueDateMillis,
                    type = state.type ?: ExpenseType.WANT,
                    guna = state.guna ?: Guna.RAJASIK,
                    priority = state.priority,
                    notes = state.notes,
                    yearMonth = DateUtil.yearMonthOf(state.dueDateMillis),
                ),
            )
            _uiState.value = AddPlanUiState()
            onDone()
        }
    }
}
