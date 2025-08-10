package com.example.expensetracker.ui.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.R
import com.example.expensetracker.ui.screens.entry.ExpenseEntryScreen
import com.example.expensetracker.ui.screens.list.ExpenseListScreen
import com.example.expensetracker.ui.screens.report.ExpenseReportScreen
import com.example.expensetracker.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    settingsViewModel: SettingsViewModel
) {
    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

    val items = listOf(
        BottomNavItem("entry", "Entry", R.drawable.ic_add),
        BottomNavItem("list", "List", R.drawable.ic_list),
        BottomNavItem("report", "Report", R.drawable.ic_bar_chart)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("") },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = if (isDarkTheme) stringResource(R.string.theme_dark_title) else stringResource(R.string.theme_light_title),
                            modifier = Modifier.padding(end = 10.dp)
                        )
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { settingsViewModel.toggleTheme() }
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painterResource(id = item.icon),
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "entry",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("entry") { ExpenseEntryScreen() }
            composable("list") { ExpenseListScreen() }
            composable("report") { ExpenseReportScreen() }
        }
    }
}
