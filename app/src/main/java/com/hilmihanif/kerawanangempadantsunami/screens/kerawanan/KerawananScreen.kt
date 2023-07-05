package com.hilmihanif.kerawanangempadantsunami.screens.kerawanan

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcgismaps.LoadStatus
import com.arcgismaps.geometry.Point
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.view.MapView
import com.arcgismaps.tasks.geocode.LocatorTask
import com.hilmihanif.kerawanangempadantsunami.R
import com.hilmihanif.kerawanangempadantsunami.utils.BERANDA_SCREEN
import com.hilmihanif.kerawanangempadantsunami.utils.KERAWANAN_SCREEN


@Composable
fun KerawananScreen(
    currentScreen :String,
    kerawananViewModel: KerawananViewModel = viewModel()
) {
    val toggleList = stringArrayResource(id = R.array.toggle_list).toList()

    val localcontext = LocalContext.current
    val mapUiState by kerawananViewModel.mapUiState.collectAsState()

    kerawananViewModel.updateMapDesc(mapUiState.map.loadStatus.collectAsState())
    kerawananViewModel.setInitToggleState(toggleList)
    kerawananViewModel.setInputDesc(localcontext)
    kerawananViewModel.updateLocatorLoading()

    KerawananContent(
        map = kerawananViewModel.mapUiState.collectAsState().value.map,
        viewpoint = mapUiState.currentViewPoint,
        mapStatus = mapUiState.map.loadStatus.collectAsState(),
        locatorTask = mapUiState.locatorTask!!,
        mapStatusDesc = mapUiState.currentMapStatusDesc,
        onSingleTap = { point, mapView ->
            //kerawananViewModel.setInitMapView(mapView)
            when (currentScreen) {
                BERANDA_SCREEN -> { }//TODO
                KERAWANAN_SCREEN -> {kerawananViewModel.setOnTapPinLocation(point, mapView, toggleList) }
            }
        },
        onPinch = { kerawananViewModel.updateMapScale() },
        onInputToggleChange = {
            when (currentScreen) {
                BERANDA_SCREEN -> {}
                KERAWANAN_SCREEN -> {kerawananViewModel.updateToggleState(select = it) }
            }
        },
        onProcessButtonClick = {
            when (currentScreen) {
                BERANDA_SCREEN -> {}
                KERAWANAN_SCREEN -> { kerawananViewModel.setOnProcessButtonClicked(
                        prov = mapUiState.currentPinLocation?.provinsi ?: ""
                    )
                }
            }
        },
        setInitMapView = {
            kerawananViewModel.setInitMapView(it)
        },
        isInputProcessNotDone = mapUiState.isInputProcessNotDone,
        currentScreen = currentScreen,
        viewModel = kerawananViewModel
    )
}


/**
 * Set Kerawanan MapView to Composable
 */
@Composable
fun KerawananContent(
    map:ArcGISMap,
    viewpoint:Viewpoint,
    mapStatus: State<LoadStatus>,
    mapStatusDesc:String,
    isInputProcessNotDone:Boolean,
    locatorTask: LocatorTask,
    setInitMapView:(MapView)->Unit,
    onSingleTap: (Point?,MapView) -> Unit,
    onPinch: () -> Unit,
    onInputToggleChange:(String)->Unit,
    currentScreen :String,
    onProcessButtonClick:()->Unit,
    viewModel: KerawananViewModel
) = Box(
        modifier = Modifier
            .fillMaxSize(),
) {

    val configuration = LocalConfiguration.current
    MapViewWithCompose(
        arcGISMap = map,
        viewpoint = viewpoint,
        onSingleTap = onSingleTap,
        onPinch = onPinch,
        setInitMapView = setInitMapView
    )
    Row(modifier = Modifier.align(Alignment.Center)) {
        Text(text = mapStatusDesc)
        AnimatedVisibility (mapStatus.value != LoadStatus.Loaded) {
            CircularProgressIndicator()
        }

    }

    when(configuration.orientation){
        Configuration.ORIENTATION_PORTRAIT -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                AnimatedVisibility(
                    visible = mapStatus.value == LoadStatus.Loaded,
                    modifier = Modifier.weight(1f)
                ){
                    MapControllerScreen(
                        viewModel = viewModel,
                        currentScreen = currentScreen
                    )
                }

                when(currentScreen){
                    KERAWANAN_SCREEN->{
                        AnimatedVisibility(
                            visible = (mapStatus.value == LoadStatus.Loaded),
                            modifier = Modifier//.align(Alignment.BottomCenter)
                        ) {
                            Crossfade(targetState = isInputProcessNotDone) {
                                when(it){
                                    true ->{
                                        InputKoordinatCard(
                                            modifier = Modifier
                                                .animateContentSize(),
                                            viewModel = viewModel,
                                            onToggleChange = { onInputToggleChange(it) },
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
                }
            }
        }
        else ->{
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AnimatedVisibility(
                    visible = mapStatus.value == LoadStatus.Loaded,
                    modifier = Modifier.weight(1f)
                ){
                    MapControllerScreen(
                        viewModel = viewModel,
                        currentScreen = currentScreen
                    )
                }

                when(currentScreen){
                    KERAWANAN_SCREEN->{
                        AnimatedVisibility(
                            visible = (mapStatus.value == LoadStatus.Loaded),
                            modifier = Modifier//.align(Alignment.BottomCenter)
                        ) {
                            Crossfade(targetState = isInputProcessNotDone) {
                                when(it){
                                    true ->{
                                        InputKoordinatCard(
                                            modifier = Modifier
                                                .animateContentSize(),
                                            viewModel = viewModel,
                                            onToggleChange = { onInputToggleChange(it) },
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
                }
            }
        }
    }



}




