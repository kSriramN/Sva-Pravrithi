package com.svapravrithi.app.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svapravrithi.app.data.local.entity.ExpenseEntity
import com.svapravrithi.app.data.repository.ExpenseRepository
import com.svapravrithi.app.data.repository.MonthCycleRepository
import com.svapravrithi.app.domain.model.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class TransactionGroup(val dateLabel: String, val dateMillis: Long, val expenses: List<ExpenseEntity>)

data class TransactionsUiState(
    val yearMonth: String = DateUtil.currentYearMonth(),
    val groups: List<TransactionGroup> = emptyList(),
    val totalSpent: Double = 0.0,
    val isLoading: Boolean = true,
)

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    monthCycleRepository: MonthCycleRepository,
) : ViewModel() {

    private val _selectedYearMonth = MutableStateFlow(DateUtil.currentCycleKey(monthCycleRepository.startDay.value))

    val uiState: StateFlow<TransactionsUiState> = _selectedYearMonth.flatMapLatest { yearMonth ->
        expenseRepository.observeForMonth(yearMonth).map { expenses ->
            val groups = expenses
                .sortedByDescending { it.date }
                .groupBy { DateUtil.startOfDay(it.date) }
                .map { (dayMillis, dayExpenses) ->
                    TransactionGroup(
                        dateLabel = DateUtil.dayLabel(dayMillis),
                        dateMillis = dayMillis,
                        expenses = dayExpenses,
                    )
                }
                .sortedByDescending { it.dateMillis }
            TransactionsUiState(
                yearMonth = yearMonth,
                groups = groups,
                totalSpent = expenses.sumOf { it.amount },
                isLoading = false,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TransactionsUiState())

    fun changeMonth(delta: Int) {
        _selectedYearMonth.value = DateUtil.addMonths(_selectedYearMonth.value, delta)
    }
}
