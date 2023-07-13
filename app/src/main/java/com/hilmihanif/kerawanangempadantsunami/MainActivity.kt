package com.hilmihanif.kerawanangempadantsunami

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.hilmihanif.kerawanangempadantsunami.ui.navGraphs.RootNavigationGraph
import com.hilmihanif.kerawanangempadantsunami.ui.theme.KerawananGempaDanTsunamiTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KerawananGempaDanTsunamiTheme {
                Surface{
                    RootNavigationGraph(navController = rememberNavController())
                }
            }
        }
    }
}

