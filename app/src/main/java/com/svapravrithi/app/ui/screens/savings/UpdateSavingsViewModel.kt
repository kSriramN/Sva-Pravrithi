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
                    actualSavingsInput = existing.actualSavings?.let {
                        if (it == it.toLong().toDouble()) it.toLong().toString() else it.toString()
                    } ?: "",
                )
            }
        }
    }

    fun onAmountChange(value: String) { _uiState.value = _uiState.value.copy(actualSavingsInput = value, saved = false) }

    fun save(onDone: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true)
            val existing = repository.get(state.yearMonth)
            val fallback = existing ?: com.svapravrithi.app.data.local.entity.DeclarationEntity(
                yearMonth = state.yearMonth,
                savingsGoal = state.savingsGoal,
                needsBudget = 0.0,
                wantsBudget = 0.0,
                pleasuresBudget = 0.0,
            )
            // Blank input intentionally saves null ("not tracked yet"), not 0 - an
            // explicit 0 is a real recorded shortfall; blank means "haven't updated yet".
            val updated = fallback.copy(actualSavings = state.actualSavingsInput.toDoubleOrNull())
            repository.save(updated)
            _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
            onDone()
        }
    }
}
