package com.svapravrithi.app.ui.screens.savings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svapravrithi.app.data.repository.DeclarationRepository
import com.svapravrithi.app.domain.model.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UpdateSavingsUiState(
    val yearMonth: String = DateUtil.currentYearMonth(),
    val savingsGoal: Double = 0.0,
    val actualSavingsInput: String = "",
    val isSaving: Boolean = false,
    val saved: Boolean = false,
)

@HiltViewModel
class UpdateSavingsViewModel @Inject constructor(
    private val repository: DeclarationRepository,
) : ViewModel() {

    private val yearMonth = DateUtil.currentYearMonth()
    private val _uiState = MutableStateFlow(UpdateSavingsUiState(yearMonth = yearMonth))
    val uiState: StateFlow<UpdateSavingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.get(yearMonth)?.let { existing ->
                _uiState.value = _uiState.value.copy(
                    savingsGoal = existing.savingsGoal,
                    actualSavingsInput = if (existing.actualSavings == 0.0) "" else existing.actualSavings.let {
                        if (it == it.toLong().toDouble()) it.toLong().toString() else it.toString()
                    },
                )
            }
        }
    }

    fun onAmountChange(value: String) { _uiState.value = _uiState.value.copy(actualSavingsInput = value, saved = false) }

    fun save() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true)
            val existing = repository.get(state.yearMonth)
            val updated = (existing ?: com.svapravrithi.app.data.local.entity.DeclarationEntity(
                yearMonth = state.yearMonth,
                savingsGoal = state.savingsGoal,
                needsBudget = 0.0,
                wantsBudget = 0.0,
                pleasuresBudget = 0.0,
            )).copy(actualSavings = state.actualSavingsInput.toDoubleOrNull() ?: 0.0)
            repository.save(updated)
            _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
        }
    }
}
