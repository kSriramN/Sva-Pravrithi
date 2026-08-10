package com.svapravrithi.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.svapravrithi.app.data.local.entity.ExpenseEntity
import com.svapravrithi.app.data.repository.DeclarationRepository
import com.svapravrithi.app.data.repository.ExpenseRepository
import com.svapravrithi.app.data.repository.MonthCycleRepository
import com.svapravrithi.app.data.repository.PlanRepository
import com.svapravrithi.app.data.repository.ScoringConfigRepository
import com.svapravrithi.app.domain.engine.DominantGunaEngine
import com.svapravrithi.app.domain.engine.GunaDistribution
import com.svapravrithi.app.domain.engine.MonthlyFinancials
import com.svapravrithi.app.domain.engine.ReflectionEngine
import com.svapravrithi.app.domain.model.DateUtil
import com.svapravrithi.app.domain.model.ExpenseType
import com.svapravrithi.app.domain.model.Guna
import com.svapravrithi.app.ui.theme.colorForCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class TypeBreakdown(
    val type: ExpenseType,
    val total: Double,
    val budget: Double,
    val recent: List<ExpenseEntity>,
    /** Upcoming/planned spend for this type \u2014 indicator only, excluded from scoring. */
    val planned: Double = 0.0,
)

data class CategorySlice(
    val category: String,
    val amount: Double,
    val percent: Int,
    val color: androidx.compose.ui.graphics.Color,
)

data class HomeUiState(
    val yearMonth: String = DateUtil.currentYearMonth(),
    val gunaDistribution: GunaDistribution = GunaDistribution(Guna.entries.associateWith { 0.0 }, Guna.SATVIK),
    val gunaReason: String = "",
    val needs: TypeBreakdown = TypeBreakdown(ExpenseType.NEED, 0.0, 0.0, emptyList()),
    val wants: TypeBreakdown = TypeBreakdown(ExpenseType.WANT, 0.0, 0.0, emptyList()),
    val pleasures: TypeBreakdown = TypeBreakdown(ExpenseType.PLEASURE, 0.0, 0.0, emptyList()),
    val savingsGoal: Double = 0.0,
    val actualSavings: Double? = null,
    val totalSpent: Double = 0.0,
    val reflectionScore: Int = 0,
    val categoryBreakdown: List<CategorySlice> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    expenseRepository: ExpenseRepository,
    planRepository: PlanRepository,
    declarationRepository: DeclarationRepository,
    scoringConfigRepository: ScoringConfigRepository,
    monthCycleRepository: MonthCycleRepository,
) : ViewModel() {

    private val _selectedYearMonth = MutableStateFlow(DateUtil.currentCycleKey(monthCycleRepository.startDay.value))

    val uiState: StateFlow<HomeUiState> = _selectedYearMonth.flatMapLatest { yearMonth ->
        combine(
            expenseRepository.observeForMonth(yearMonth),
            planRepository.observeForMonth(yearMonth),
            declarationRepository.observe(yearMonth),
            scoringConfigRepository.observe(),
        ) { expenses, plans, declaration, config ->
            fun breakdown(type: ExpenseType, budget: Double): TypeBreakdown {
                val forType = expenses.filter { it.type == type }
                val plannedForType = plans.filter { !it.isCompleted && it.type == type }.sumOf { it.estimatedAmount }
                return TypeBreakdown(
                    type = type,
                    total = forType.sumOf { it.amount },
                    budget = budget,
                    recent = forType.sortedByDescending { it.date }.take(5),
                    planned = plannedForType,
                )
            }

            val needsTotal = expenses.filter { it.type == ExpenseType.NEED }.sumOf { it.amount }
            val wantsTotal = expenses.filter { it.type == ExpenseType.WANT }.sumOf { it.amount }
            val pleasuresTotal = expenses.filter { it.type == ExpenseType.PLEASURE }.sumOf { it.amount }

            val financials = MonthlyFinancials(
                savingsGoal = declaration?.savingsGoal ?: 0.0,
                needsBudget = declaration?.needsBudget ?: 0.0,
                wantsBudget = declaration?.wantsBudget ?: 0.0,
                pleasuresBudget = declaration?.pleasuresBudget ?: 0.0,
                actualSavings = declaration?.actualSavings,
                actualNeeds = needsTotal,
                actualWants = wantsTotal,
                actualPleasures = pleasuresTotal,
            )

            val reflection = ReflectionEngine(config).computeReflection(
                savingsGoal = financials.savingsGoal,
                savingsActual = financials.actualSavings,
                wantsBudget = financials.wantsBudget,
                wantsActual = financials.actualWants,
                pleasuresBudget = financials.pleasuresBudget,
                pleasuresActual = financials.actualPleasures,
            )
            val gunaResult = DominantGunaEngine().compute(reflection, config)

            val totalSpentForCategories = expenses.sumOf { it.amount }.takeIf { it > 0.0 } ?: 1.0
            val categoryBreakdown = expenses
                .groupBy { it.category }
                .map { (category, categoryExpenses) ->
                    val amount = categoryExpenses.sumOf { it.amount }
                    CategorySlice(
                        category = category,
                        amount = amount,
                        percent = ((amount / totalSpentForCategories) * 100).toInt(),
                        color = colorForCategory(category),
                    )
                }
                .sortedByDescending { it.amount }

            HomeUiState(
                yearMonth = yearMonth,
                gunaDistribution = GunaDistribution(gunaResult.visualWeights, gunaResult.dominant),
                gunaReason = gunaResult.reason,
                needs = breakdown(ExpenseType.NEED, budget = financials.needsBudget),
                wants = breakdown(ExpenseType.WANT, budget = financials.wantsBudget),
                pleasures = breakdown(ExpenseType.PLEASURE, budget = financials.pleasuresBudget),
                savingsGoal = financials.savingsGoal,
                actualSavings = financials.actualSavings,
                totalSpent = expenses.sumOf { it.amount },
                reflectionScore = reflection.roundedTotal,
                categoryBreakdown = categoryBreakdown,
                isLoading = false,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun changeMonth(delta: Int) {
        _selectedYearMonth.value = DateUtil.addMonths(_selectedYearMonth.value, delta)
    }
}
