package com.svapravrithi.app.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.svapravrithi.app.domain.model.DateUtil
import com.svapravrithi.app.ui.screens.addexpense.AddExpenseScreen
import com.svapravrithi.app.ui.screens.analytics.AnalyticsOverviewScreen
import com.svapravrithi.app.ui.screens.analytics.GunaAnalyticsScreen
import com.svapravrithi.app.ui.screens.analytics.SavingsAnalyticsScreen
import com.svapravrithi.app.ui.screens.analytics.SpendingAnalyticsScreen
import com.svapravrithi.app.ui.screens.backup.BackupScreen
import com.svapravrithi.app.ui.screens.declaration.MonthlyDeclarationScreen
import com.svapravrithi.app.ui.screens.home.HomeScreen
import com.svapravrithi.app.ui.screens.onboarding.OnboardingScreen
import com.svapravrithi.app.ui.screens.plan.AddPlanScreen
import com.svapravrithi.app.ui.screens.plan.PlanListScreen
import com.svapravrithi.app.ui.screens.profile.ProfileScreen
import com.svapravrithi.app.ui.screens.reflection.MonthlyReflectionScreen
import com.svapravrithi.app.ui.screens.savings.UpdateSavingsScreen
import com.svapravrithi.app.ui.screens.splash.SplashScreen

private val SCREENS_WITH_BOTTOM_NAV = setOf(
    "home", "add_expense", "plan_list", "analytics_overview",
    "guna_analytics", "spending_analytics", "savings_analytics",
)

@Composable
fun SvaNavGraph(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val baseRoute = backStackEntry?.destination?.route?.substringBefore("?")?.substringBefore("/{")
    val showBottomBar = baseRoute in SCREENS_WITH_BOTTOM_NAV

    Scaffold(
        bottomBar = { if (showBottomBar) SvaBottomNavBar(navController) },
    ) { innerPadding ->
        val contentModifier = if (showBottomBar) Modifier.padding(innerPadding) else Modifier.padding(PaddingValues(0.dp))
        NavHost(
            navController = navController,
            startDestination = Destination.Splash.route,
            modifier = contentModifier,
        ) {
            composable(Destination.Splash.route) {
                SplashScreen(
                    onFinished = { onboardingSeen ->
                        val dest = if (onboardingSeen) Destination.Home.route else Destination.Onboarding.route
                        navController.navigate(dest) { popUpTo(Destination.Splash.route) { inclusive = true } }
                    },
                )
            }
            composable(Destination.Onboarding.route) {
                OnboardingScreen(
                    onFinished = {
                        val ym = DateUtil.currentYearMonth()
                        navController.navigate(Destination.MonthlyDeclaration.build(ym)) {
                            popUpTo(Destination.Onboarding.route) { inclusive = true }
                        }
                    },
                )
            }
            composable(
                route = Destination.MonthlyDeclaration.route,
                arguments = listOf(navArgument("yearMonth") { type = NavType.StringType }),
            ) { backStack ->
                val yearMonth = backStack.arguments?.getString("yearMonth") ?: DateUtil.currentYearMonth()
                MonthlyDeclarationScreen(
                    yearMonth = yearMonth,
                    onSaved = {
                        navController.navigate(Destination.Home.route) {
                            popUpTo(Destination.MonthlyDeclaration.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Destination.Home.route) {
                HomeScreen(
                    onAddExpense = { navController.navigate(Destination.AddExpense.build(null)) },
                    onOpenDeclaration = { navController.navigate(Destination.MonthlyDeclaration.build(DateUtil.currentYearMonth())) },
                    onOpenAnalytics = { navController.navigate(Destination.AnalyticsOverview.route) },
                    onOpenPlan = { navController.navigate(Destination.PlanList.route) },
                    onOpenProfile = { navController.navigate(Destination.Profile.route) },
                )
            }
            composable(
                route = Destination.AddExpense.route,
                arguments = listOf(navArgument("expenseId") { type = NavType.LongType; defaultValue = -1L }),
            ) {
                AddExpenseScreen(onSaved = { navController.popBackStack() }, onBack = { navController.popBackStack() })
            }
            composable(Destination.PlanList.route) {
                PlanListScreen(
                    onAddPlan = { navController.navigate(Destination.AddPlan.build(null)) },
                    onEditPlan = { id -> navController.navigate(Destination.AddPlan.build(id)) },
                )
            }
            composable(
                route = Destination.AddPlan.route,
                arguments = listOf(navArgument("planId") { type = NavType.LongType; defaultValue = -1L }),
            ) { backStack ->
                val planId = backStack.arguments?.getLong("planId")?.takeIf { it >= 0 }
                AddPlanScreen(planId = planId, onSaved = { navController.popBackStack() }, onBack = { navController.popBackStack() })
            }
            composable(Destination.AnalyticsOverview.route) {
                AnalyticsOverviewScreen(
                    onOpenGuna = { navController.navigate(Destination.GunaAnalytics.route) },
                    onOpenSpending = { navController.navigate(Destination.SpendingAnalytics.route) },
                    onOpenSavings = { navController.navigate(Destination.SavingsAnalytics.route) },
                    onOpenReflection = { navController.navigate(Destination.MonthlyReflection.route) },
                )
            }
            composable(Destination.GunaAnalytics.route) { GunaAnalyticsScreen(onBack = { navController.popBackStack() }) }
            composable(Destination.SpendingAnalytics.route) { SpendingAnalyticsScreen(onBack = { navController.popBackStack() }) }
            composable(Destination.SavingsAnalytics.route) { SavingsAnalyticsScreen(onBack = { navController.popBackStack() }) }
            composable(Destination.MonthlyReflection.route) { MonthlyReflectionScreen(onBack = { navController.popBackStack() }) }
            composable(Destination.Profile.route) { ProfileScreen(onBack = { navController.popBackStack() }, onUpdateSavings = { navController.navigate(Destination.UpdateSavings.route) }, onBackupRestore = { navController.navigate(Destination.Backup.route) }) }
            composable(Destination.UpdateSavings.route) { UpdateSavingsScreen(onBack = { navController.popBackStack() }) }
            composable(Destination.Backup.route) { BackupScreen(onBack = { navController.popBackStack() }) }
        }
    }
}
