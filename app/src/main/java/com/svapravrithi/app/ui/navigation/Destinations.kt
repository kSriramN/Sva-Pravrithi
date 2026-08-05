package com.svapravrithi.app.ui.navigation

sealed class Destination(val route: String) {
    data object Splash : Destination("splash")
    data object Onboarding : Destination("onboarding")
    data object MonthlyDeclaration : Destination("declaration/{yearMonth}") {
        fun build(yearMonth: String) = "declaration/$yearMonth"
    }
    data object Home : Destination("home")
    data object AddExpense : Destination("add_expense?expenseId={expenseId}") {
        fun build(expenseId: Long? = null) = "add_expense?expenseId=${expenseId ?: -1L}"
    }
    data object PlanList : Destination("plan_list")
    data object AddPlan : Destination("add_plan?planId={planId}") {
        fun build(planId: Long? = null) = "add_plan?planId=${planId ?: -1L}"
    }
    data object AnalyticsOverview : Destination("analytics_overview")
    data object GunaAnalytics : Destination("guna_analytics")
    data object SpendingAnalytics : Destination("spending_analytics")
    data object SavingsAnalytics : Destination("savings_analytics")
    data object MonthlyReflection : Destination("monthly_reflection")
    data object Profile : Destination("profile")
    data object UpdateSavings : Destination("update_savings")
    data object Backup : Destination("backup")
}

/** The 4 bottom-nav destinations, matching the design reference's tab bar. */
enum class BottomNavItem(val destination: Destination, val label: String) {
    HOME(Destination.Home, "Home"),
    ADD(Destination.AddExpense, "Add"),
    PLAN(Destination.PlanList, "Plan"),
    ANALYTICS(Destination.AnalyticsOverview, "Analytics"),
}
