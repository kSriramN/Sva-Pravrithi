package com.svapravrithi.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

private fun iconFor(item: BottomNavItem): ImageVector = when (item) {
    BottomNavItem.HOME -> Icons.Filled.Home
    BottomNavItem.ADD -> Icons.Filled.Add
    BottomNavItem.PLAN -> Icons.Filled.CalendarMonth
    BottomNavItem.ANALYTICS -> Icons.Filled.BarChart
}

@Composable
fun SvaBottomNavBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        BottomNavItem.entries.forEach { item ->
            val baseRoute = item.destination.route.substringBefore("?").substringBefore("/{")
            val selected = currentRoute?.hierarchy?.any { it.route?.substringBefore("?")?.substringBefore("/{") == baseRoute } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    val target = when (item) {
                        BottomNavItem.ADD -> Destination.AddExpense.build(null)
                        else -> item.destination.route
                    }
                    navController.navigate(target) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(iconFor(item), contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
        }
    }
}
