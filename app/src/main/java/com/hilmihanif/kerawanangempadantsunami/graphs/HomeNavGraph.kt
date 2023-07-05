package com.hilmihanif.kerawanangempadantsunami.graphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.hilmihanif.kerawanangempadantsunami.BottomBarScreen
import com.hilmihanif.kerawanangempadantsunami.screens.kerawanan.KerawananScreen
import com.hilmihanif.kerawanangempadantsunami.screens.ScreenContent
import com.hilmihanif.kerawanangempadantsunami.utils.BERANDA_SCREEN
import com.hilmihanif.kerawanangempadantsunami.utils.KERAWANAN_SCREEN


@Composable
fun HomeNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = BottomBarScreen.Profil.route
    ) {
        composable(route = BottomBarScreen.Profil.route) {
            ScreenContent(
                name = BottomBarScreen.Profil.route,
                onClick = {
                    navController.navigate(Graph.DETAILS)
                }
            )
        }

        composable(route = BottomBarScreen.Beranda.route) {
//            ScreenContent(
//                name = BottomBarScreen.Beranda.route,
//                onClick = { }
//            )
            KerawananScreen(BERANDA_SCREEN)
        }
        composable(route = BottomBarScreen.Kerawanan.route) {
            KerawananScreen(KERAWANAN_SCREEN)
        }
        detailsNavGraph(navController = navController)
    }
}

fun NavGraphBuilder.detailsNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.DETAILS,
        startDestination = DetailsScreen.Information.route
    ) {
        composable(route = DetailsScreen.Information.route) {
            ScreenContent(name = DetailsScreen.Information.route) {
                navController.navigate(DetailsScreen.Overview.route)
            }
        }
        composable(route = DetailsScreen.Overview.route) {
            ScreenContent(name = DetailsScreen.Overview.route) {
                navController.popBackStack(
                    route = DetailsScreen.Information.route,
                    inclusive = false
                )
            }
        }
    }
}

sealed class DetailsScreen(val route: String) {
    object Information : DetailsScreen(route = "INFORMATION")
    object Overview : DetailsScreen(route = "OVERVIEW")
}
