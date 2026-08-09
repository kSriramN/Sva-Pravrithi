package com.svapravrithi.app.ui.screens.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svapravrithi.app.data.local.entity.PlanEntity
import com.svapravrithi.app.data.repository.MonthCycleRepository
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
    /** 0 = new plan being created; non-zero = editing an existing one. */
    val id: Long = 0L,
    val title: String = "",
    val estimatedAmount: String = "",
    val dueDateMillis: Long = System.currentTimeMillis(),
    val type: ExpenseType? = null,
    val guna: Guna? = null,
    val priority: PlanPriority = PlanPriority.MEDIUM,
    val notes: String = "",
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isLoaded: Boolean = false,
    val error: String? = null,
) {
    val dueDateLabel: String get() = DateUtil.dayLabel(dueDateMillis)
    val isValid: Boolean get() = title.isNotBlank() && estimatedAmount.toDoubleOrNull() != null && estimatedAmount.toDoubleOrNull()!! > 0
    val isEditing: Boolean get() = id != 0L
}

@HiltViewModel
class AddPlanViewModel @Inject constructor(
    private val repository: PlanRepository,
    private val monthCycleRepository: MonthCycleRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPlanUiState())
    val uiState: StateFlow<AddPlanUiState> = _uiState.asStateFlow()

    /** Call with an existing plan's id to switch this screen into edit mode. Pass null (or don't call) for a new plan. */
    fun load(planId: Long?) {
        if (planId == null || planId == 0L) {
            _uiState.value = _uiState.value.copy(isLoaded = true)
            return
        }
        viewModelScope.launch {
            val existing = repository.getById(planId)
            if (existing != null) {
                _uiState.value = AddPlanUiState(
                    id = existing.id,
                    title = existing.title,
                    estimatedAmount = existing.estimatedAmount.let { if (it == it.toLong().toDouble()) it.toLong().toString() else it.toString() },
                    dueDateMillis = existing.dueDate,
                    type = existing.type,
                    guna = existing.guna,
                    priority = existing.priority,
                    notes = existing.notes,
                    isLoaded = true,
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoaded = true)
            }
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
                    // Passing the existing id (Room's REPLACE conflict strategy) updates the
                    // row in place when editing; id=0 lets Room autogenerate a new one.
                    id = state.id,
                    title = state.title,
                    estimatedAmount = state.estimatedAmount.toDouble(),
                    dueDate = state.dueDateMillis,
                    type = state.type ?: ExpenseType.WANT,
                    guna = state.guna ?: Guna.RAJASIK,
                    priority = state.priority,
                    notes = state.notes,
                    yearMonth = DateUtil.cycleKeyFor(state.dueDateMillis, monthCycleRepository.startDay.value),
                ),
            )
            _uiState.value = AddPlanUiState()
            onDone()
        }
    }

    fun delete(onDone: () -> Unit) {
        val state = _uiState.value
        if (!state.isEditing) return
        viewModelScope.launch {
            _uiState.value = state.copy(isDeleting = true)
            repository.getById(state.id)?.let { repository.delete(it) }
            _uiState.value = AddPlanUiState()
            onDone()
        }
    }
}
