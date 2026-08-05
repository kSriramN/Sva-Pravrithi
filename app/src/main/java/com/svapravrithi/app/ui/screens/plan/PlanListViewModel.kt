package com.svapravrithi.app.ui.screens.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svapravrithi.app.data.local.entity.PlanEntity
import com.svapravrithi.app.data.repository.PlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlanListUiState(
    val upcoming: List<PlanEntity> = emptyList(),
    val completed: List<PlanEntity> = emptyList(),
)

@HiltViewModel
class PlanListViewModel @Inject constructor(
    private val repository: PlanRepository,
) : ViewModel() {

    val uiState: StateFlow<PlanListUiState> = combine(
        repository.observeUpcoming(),
        repository.observeCompleted(),
    ) { upcoming, completed -> PlanListUiState(upcoming, completed) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PlanListUiState())

    fun markCompleted(plan: PlanEntity) {
        viewModelScope.launch { repository.markCompleted(plan) }
    }

    fun delete(plan: PlanEntity) {
        viewModelScope.launch { repository.delete(plan) }
    }
}
