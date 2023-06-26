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
       // ArcGISEnvironment.apiKey = ApiKey.create("AAPK1ea8700a6a7f494597e61dbb57614a4e9GS6Vs50fB5fxBFNgemkUWNzbnoP107T_HfylVr8RPbrFsJaWE-3jXLg2XGVdJt_")

        setContent {
            KerawananGempaDanTsunamiTheme {
                RootNavigationGraph(navController = rememberNavController())

            }
        }

    }

}

