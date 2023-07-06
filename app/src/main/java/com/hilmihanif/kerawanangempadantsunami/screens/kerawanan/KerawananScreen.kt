package com.hilmihanif.kerawanangempadantsunami.screens.kerawanan

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arcgismaps.tasks.geocode.LocatorTask
import com.hilmihanif.kerawanangempadantsunami.screens.main_map.MapControllerScreen
import com.hilmihanif.kerawanangempadantsunami.utils.KERAWANAN_SCREEN
import com.hilmihanif.kerawanangempadantsunami.viewmodels.MainMapViewModel

@Composable
fun KerawananScreen(
    viewModel :MainMapViewModel,
    isInputProcessNotDone:Boolean,
    onInputToggleChange:(String)->Unit,
    locatorTask:LocatorTask,
    onProcessButtonClick:()->Unit
) {
    MapControllerScreen(currentScreen = KERAWANAN_SCREEN, viewModel = viewModel) {
        Crossfade(targetState = isInputProcessNotDone) {
            when(it){
                true ->{
                    InputKoordinatCard(
                        modifier = Modifier
                            .animateContentSize(),
                        viewModel = viewModel,
                        onToggleChange = onInputToggleChange,
                        locatorTask = locatorTask,
                        onProsesButtonClick = onProcessButtonClick,
                    )
                }
                false ->{
                    ResultCard(
                        modifier = Modifier.animateContentSize(),
                        viewModel = viewModel,
                    )
                }
            }
        }
    }
}