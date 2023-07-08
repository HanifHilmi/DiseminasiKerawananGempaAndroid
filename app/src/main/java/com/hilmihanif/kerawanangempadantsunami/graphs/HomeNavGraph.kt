package com.hilmihanif.kerawanangempadantsunami.graphs

import android.app.Activity
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.hilmihanif.kerawanangempadantsunami.BottomBarScreen
import com.hilmihanif.kerawanangempadantsunami.firebase.auth.GoogleAuthUiClient
import com.hilmihanif.kerawanangempadantsunami.screens.ScreenContent
import com.hilmihanif.kerawanangempadantsunami.screens.main_map.BackHandlerConfirmationDialog
import com.hilmihanif.kerawanangempadantsunami.screens.main_map.MainMapScreen
import com.hilmihanif.kerawanangempadantsunami.screens.profil.ProfileScreen
import com.hilmihanif.kerawanangempadantsunami.utils.BERANDA_SCREEN
import com.hilmihanif.kerawanangempadantsunami.utils.KERAWANAN_SCREEN
import kotlinx.coroutines.launch


@Composable
fun HomeNavGraph(navController: NavHostController,googleAuthUiClient: GoogleAuthUiClient) {
    val currentActivity  = LocalContext.current as Activity?
    val currentLifecycleOwne = LocalLifecycleOwner.current
    var isFirstTime by rememberSaveable {mutableStateOf(true)}
    var isLoggedIn by rememberSaveable { mutableStateOf(googleAuthUiClient.isLoggedIn())}

    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = BottomBarScreen.Profil.route,

    ) {
        composable(
            route = BottomBarScreen.Profil.route,
        ) {

            BackHandlerConfirmationDialog(currentActivity)
            if(isFirstTime){
                navController.navigate(BottomBarScreen.Beranda.route)
                isFirstTime = false
            }
            /*
            ScreenContent(
                name = BottomBarScreen.Profil.route,
                onClick = {
                    navController.navigate(Graph.DETAILS)
                    Log.d(TEST_LOG,"current destination ${navController.currentDestination?.route ?: "Null"}")
                }
            )

             */
            ProfileScreen(
                userData = googleAuthUiClient.getSignedInUser(),
                onSignOut = {
                    currentLifecycleOwne.lifecycleScope.launch {
                        googleAuthUiClient.sighOut()
                        Toast.makeText(
                            currentActivity,
                            "Signed Out",
                            Toast.LENGTH_LONG
                        ).show()
                        isLoggedIn = false
                    }
                },
                onReSignIn ={

                },
                isLoggedIn = isLoggedIn
            )
        }

        composable(route = BottomBarScreen.Beranda.route) {
            BackHandlerConfirmationDialog(currentActivity)
            MainMapScreen(
                targetScreen = BERANDA_SCREEN,
            )
        }
        composable(route = BottomBarScreen.Kerawanan.route) {
           BackHandlerConfirmationDialog(currentActivity)
            MainMapScreen(
                targetScreen = KERAWANAN_SCREEN,
            )
        }

        //detailsNavGraph(navController = navController)
    }
}



fun NavGraphBuilder.detailsNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.DETAILS,
        startDestination = DetailsScreen.Information.route
    ) {
        composable(route = DetailsScreen.Information.route) {
            ScreenContent(name = DetailsScreen.Information.route) {
//                navController.navigate(DetailsScreen.Overview.route)
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


