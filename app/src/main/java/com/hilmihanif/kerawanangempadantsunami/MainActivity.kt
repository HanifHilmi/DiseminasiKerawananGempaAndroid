package com.hilmihanif.kerawanangempadantsunami

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.hilmihanif.kerawanangempadantsunami.graphs.RootNavigationGraph
import com.hilmihanif.kerawanangempadantsunami.ui.theme.KerawananGempaDanTsunamiTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KerawananGempaDanTsunamiTheme {
                RootNavigationGraph(navController = rememberNavController())
            }
        }
    }
}

