package com.hilmihanif.kerawanangempadantsunami.ui.navGraphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.hilmihanif.kerawanangempadantsunami.firebase.auth.GoogleAuthUiClient
import com.hilmihanif.kerawanangempadantsunami.ui.screens.splash.GoogleSignInScreen
import com.hilmihanif.kerawanangempadantsunami.ui.screens.splash.SplashScreen


fun NavGraphBuilder.authNavGraph(navController: NavHostController,googleAuthUiClient: GoogleAuthUiClient) {
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = AuthScreen.Splash.route
    ) {



        composable(route = AuthScreen.Splash.route){
            SplashScreen(
                googleAuthUiClient = googleAuthUiClient,
                onSplashDone = { isUserSignedIn->
                    navController.popBackStack()
                    if (isUserSignedIn) {
                        navController.navigate(Graph.HOME)
                    } else {
                        navController.navigate(AuthScreen.Login.route)
                    }
                }
            )
        }

        composable(route = AuthScreen.Login.route){
            GoogleSignInScreen(
                googleAuthUiClient =googleAuthUiClient,
                onLoggedIn = {

                    navController.navigate(Graph.HOME)

                }
            )
        }

    }
}





sealed class AuthScreen(val route: String) {
    object Login : AuthScreen(route = "LOGIN")
    object Splash : AuthScreen(route = "SIGN_UP")
}