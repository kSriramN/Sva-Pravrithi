package com.svapravrithi.app.ui.screens.declaration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svapravrithi.app.data.local.entity.DeclarationEntity
import com.svapravrithi.app.data.repository.DeclarationRepository
import com.svapravrithi.app.domain.model.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeclarationUiState(
    val yearMonth: String = DateUtil.currentYearMonth(),
    val savingsGoal: String = "",
    val needsBudget: String = "",
    val wantsBudget: String = "",
    val pleasuresBudget: String = "",
    val isSaving: Boolean = false,
)

@HiltViewModel
class MonthlyDeclarationViewModel @Inject constructor(
    private val repository: DeclarationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeclarationUiState())
    val uiState: StateFlow<DeclarationUiState> = _uiState.asStateFlow()

    fun load(yearMonth: String) {
        _uiState.value = _uiState.value.copy(yearMonth = yearMonth)
        viewModelScope.launch {
            repository.get(yearMonth)?.let { existing ->
                _uiState.value = _uiState.value.copy(
                    savingsGoal = existing.savingsGoal.toPlainStringOrEmpty(),
                    needsBudget = existing.needsBudget.toPlainStringOrEmpty(),
                    wantsBudget = existing.wantsBudget.toPlainStringOrEmpty(),
                    pleasuresBudget = existing.pleasuresBudget.toPlainStringOrEmpty(),
                )
            }
        }
    }

    fun onSavingsGoalChange(value: String) { _uiState.value = _uiState.value.copy(savingsGoal = value) }
    fun onNeedsBudgetChange(value: String) { _uiState.value = _uiState.value.copy(needsBudget = value) }
    fun onWantsBudgetChange(value: String) { _uiState.value = _uiState.value.copy(wantsBudget = value) }
    fun onPleasuresBudgetChange(value: String) { _uiState.value = _uiState.value.copy(pleasuresBudget = value) }

    fun save(onDone: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true)
            val existingActualSavings = repository.get(state.yearMonth)?.actualSavings ?: 0.0
            repository.save(
                DeclarationEntity(
                    yearMonth = state.yearMonth,
                    savingsGoal = state.savingsGoal.toDoubleOrNull() ?: 0.0,
                    needsBudget = state.needsBudget.toDoubleOrNull() ?: 0.0,
                    wantsBudget = state.wantsBudget.toDoubleOrNull() ?: 0.0,
                    pleasuresBudget = state.pleasuresBudget.toDoubleOrNull() ?: 0.0,
                    actualSavings = existingActualSavings,
                ),
            )
            _uiState.value = _uiState.value.copy(isSaving = false)
            onDone()
        }
    }
}

private fun Double.toPlainStringOrEmpty(): String = if (this == 0.0) "" else {
    if (this == this.toLong().toDouble()) this.toLong().toString() else this.toString()
}
