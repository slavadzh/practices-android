package com.example.practices_android.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.practices_android.R
import com.example.practices_android.ui.films.details.FilmDetailsScreen
import com.example.practices_android.ui.films.list.FilmsListScreen
import com.example.practices_android.ui.stub.StubScreen

@Composable
fun AppRoot() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomBar(navController) },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomTab.FILMS.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            navigation(startDestination = Routes.FILMS_LIST, route = Routes.FILMS_GRAPH) {
                composable(Routes.FILMS_LIST) {
                    FilmsListScreen(
                        onFilmClick = { id -> navController.navigate(Routes.filmDetails(id)) },
                    )
                }
                composable(
                    route = Routes.FILM_DETAILS,
                    arguments = listOf(navArgument(Routes.FILM_ID_ARG) { type = NavType.StringType }),
                ) {
                    FilmDetailsScreen(onBack = { navController.popBackStack() })
                }
            }
            composable(Routes.FAVORITES) {
                StubScreen(title = stringResource(R.string.tab_favorites))
            }
            composable(Routes.PROFILE) {
                StubScreen(title = stringResource(R.string.tab_profile))
            }
        }
    }
}

@Composable
private fun BottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    NavigationBar {
        BottomTab.entries.forEach { tab ->
            val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (selected) {
                        navController.popBackStack(tab.startRoute, inclusive = false)
                    } else {
                        navController.navigate(tab.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(tab.icon, contentDescription = null) },
                label = { Text(stringResource(tab.titleRes)) },
            )
        }
    }
}
