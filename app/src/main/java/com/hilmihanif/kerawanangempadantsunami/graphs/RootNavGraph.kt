package com.hilmihanif.kerawanangempadantsunami.graphs

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.android.gms.auth.api.identity.Identity
import com.hilmihanif.kerawanangempadantsunami.firebase.auth.GoogleAuthUiClient
import com.hilmihanif.kerawanangempadantsunami.screens.home.HomeScreen

@Composable
fun RootNavigationGraph(navController: NavHostController) {

    val localContext = LocalContext.current
    val googleAuthUiClient by lazy{
        GoogleAuthUiClient(
            context = localContext,
            oneTapClient = Identity.getSignInClient(localContext)
        )
    }
    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.AUTHENTICATION
    ) {
        authNavGraph(navController = navController,googleAuthUiClient)
        composable(route = Graph.HOME) {
            HomeScreen(googleAuthUiClient)
        }
    }
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
    const val DETAILS = "details_graph"
}