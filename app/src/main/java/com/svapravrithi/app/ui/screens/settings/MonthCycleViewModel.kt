package com.svapravrithi.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svapravrithi.app.data.repository.MonthCycleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MonthCycleUiState(
    val startDay: Int = 1,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
)

@HiltViewModel
class MonthCycleViewModel @Inject constructor(
    private val repository: MonthCycleRepository,
) : ViewModel() {

    val startDay: StateFlow<Int> = repository.startDay

    private val _uiState = MutableStateFlow(MonthCycleUiState(startDay = repository.startDay.value))
    val uiState: StateFlow<MonthCycleUiState> = _uiState.asStateFlow()

    fun onStartDaySelected(day: Int) {
        _uiState.value = _uiState.value.copy(startDay = day, saved = false)
    }

    /** Applies the change - this is what triggers the recalculation of existing data. */
    fun apply() {
        val target = _uiState.value.startDay
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            repository.setStartDay(target)
            _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
        }
    }
}
