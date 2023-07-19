package com.hilmihanif.kerawanangempadantsunami.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hilmihanif.kerawanangempadantsunami.BottomBarScreen
import com.hilmihanif.kerawanangempadantsunami.firebase.auth.GoogleAuthUiClient
import com.hilmihanif.kerawanangempadantsunami.ui.navGraphs.HomeNavGraph


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(googleAuthUIClient:GoogleAuthUiClient,navController: NavHostController = rememberNavController()) {


    Scaffold(
        topBar = {},
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)){
            HomeNavGraph(navController = navController,googleAuthUIClient)
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomBarScreen.Profil,
        BottomBarScreen.Beranda,
        BottomBarScreen.Kerawanan,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestination = screens.any { it.route == currentDestination?.route }
    if (bottomBarDestination) {
        NavigationBar() {
            screens.forEach { screen ->
                AddItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    navController = navController
                )
            }
        }
    }
}

/**
 * Add Navigation item
 *
 */
@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    NavigationBarItem(
        label = {
            Text(text = screen.title)
        },
        icon = {
            Icon(
                imageVector = screen.icon!!,
                contentDescription = "Navigation Icon"
            )
        },
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route
        } == true,
        //colors = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
        onClick = {
            if(navController.currentDestination?.route != screen.route){
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            }
        }
    )
}