package com.svapravrithi.app.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svapravrithi.app.data.repository.DeclarationRepository
import com.svapravrithi.app.data.repository.ExpenseRepository
import com.svapravrithi.app.data.repository.PlanRepository
import com.svapravrithi.app.data.repository.ScoringConfigRepository
import com.svapravrithi.app.domain.engine.DominantGunaEngine
import com.svapravrithi.app.domain.engine.GunaDistribution
import com.svapravrithi.app.domain.engine.MonthlyFinancials
import com.svapravrithi.app.domain.engine.ReflectionEngine
import com.svapravrithi.app.domain.engine.ReflectionResult
import com.svapravrithi.app.domain.model.DateUtil
import com.svapravrithi.app.domain.model.ExpenseType
import com.svapravrithi.app.domain.model.Guna
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Planned (future) spend by Need/Want/Pleasure type, from not-yet-completed Plan items.
 * Display-only — this is an indicator shown alongside actual spend so the user can see
 * what's committed/upcoming. It never feeds the Reflection Engine or Dominant Guna
 * calculation, which are based purely on actual spend and declared budgets/savings.
 */
data class PlannedSpend(
    val needs: Double = 0.0,
    val wants: Double = 0.0,
    val pleasures: Double = 0.0,
)

data class AnalyticsUiState(
    val yearMonth: String = DateUtil.currentYearMonth(),
    val financials: MonthlyFinancials = MonthlyFinancials(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
    val reflection: ReflectionResult = ReflectionEngine().computeReflection(0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
    val gunaDistribution: GunaDistribution = GunaDistribution(Guna.entries.associateWith { 0.0 }, Guna.SATVIK),
    val gunaReason: String = "",
    /** Optional per-expense Guna tags, for the secondary "personal reflection" chart only. */
    val taggedGunaSpend: Map<Guna, Double> = emptyMap(),
    /** Upcoming/planned spend by type \u2014 indicator only, excluded from all scoring. */
    val planned: PlannedSpend = PlannedSpend(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    expenseRepository: ExpenseRepository,
    planRepository: PlanRepository,
    declarationRepository: DeclarationRepository,
    scoringConfigRepository: ScoringConfigRepository,
) : ViewModel() {

    private val yearMonth = DateUtil.currentYearMonth()

    val uiState: StateFlow<AnalyticsUiState> = combine(
        expenseRepository.observeForMonth(yearMonth),
        planRepository.observeForMonth(yearMonth),
        declarationRepository.observe(yearMonth),
        scoringConfigRepository.observe(),
    ) { expenses, plans, declaration, config ->
        val needsTotal = expenses.filter { it.type == ExpenseType.NEED }.sumOf { it.amount }
        val wantsTotal = expenses.filter { it.type == ExpenseType.WANT }.sumOf { it.amount }
        val pleasuresTotal = expenses.filter { it.type == ExpenseType.PLEASURE }.sumOf { it.amount }

        val financials = MonthlyFinancials(
            savingsGoal = declaration?.savingsGoal ?: 0.0,
            needsBudget = declaration?.needsBudget ?: 0.0,
            wantsBudget = declaration?.wantsBudget ?: 0.0,
            pleasuresBudget = declaration?.pleasuresBudget ?: 0.0,
            actualSavings = declaration?.actualSavings ?: 0.0,
            actualNeeds = needsTotal,
            actualWants = wantsTotal,
            actualPleasures = pleasuresTotal,
        )

        // Scoring & Dominant Guna use ONLY actual spend + declared budgets/savings above.
        // Planned/future spend (below) never enters this calculation.
        val reflection = ReflectionEngine(config).computeReflection(
            savingsGoal = financials.savingsGoal,
            savingsActual = financials.actualSavings,
            wantsBudget = financials.wantsBudget,
            wantsActual = financials.actualWants,
            pleasuresBudget = financials.pleasuresBudget,
            pleasuresActual = financials.actualPleasures,
        )
        val gunaResult = DominantGunaEngine().compute(reflection, config)

        val taggedSpend = expenses.mapNotNull { it.guna?.let { g -> g to it.amount } }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, amounts) -> amounts.sum() }

        val notCompletedPlans = plans.filter { !it.isCompleted }
        val planned = PlannedSpend(
            needs = notCompletedPlans.filter { it.type == ExpenseType.NEED }.sumOf { it.estimatedAmount },
            wants = notCompletedPlans.filter { it.type == ExpenseType.WANT }.sumOf { it.estimatedAmount },
            pleasures = notCompletedPlans.filter { it.type == ExpenseType.PLEASURE }.sumOf { it.estimatedAmount },
        )

        AnalyticsUiState(
            yearMonth = yearMonth,
            financials = financials,
            reflection = reflection,
            gunaDistribution = GunaDistribution(gunaResult.visualWeights, gunaResult.dominant),
            gunaReason = gunaResult.reason,
            taggedGunaSpend = taggedSpend,
            planned = planned,
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnalyticsUiState())
}
